/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
    stage('Build & Unit Test'){
        STAGE = env.STAGE_NAME
        sh 'env'
        sh './gradlew clean build'
        println "Stage: ${env.STAGE_NAME}"
    }

    stage('SonarQube') {
        STAGE = env.STAGE_NAME
        def scannerHome = tool 'SonarQube Scanner 4.6.2'
            withSonarQubeEnv('SonarQube local'){
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=sonarqube-token -Dsonar.java.binaries=build"
        }
    }

    stage('Run'){
        STAGE = env.STAGE_NAME
        sh "nohup bash gradlew bootRun &"
    }

    stage('Wait') {
        STAGE = env.STAGE_NAME
        println "Sleep 20 seconds"
        sleep(time: 20, unit: "SECONDS")
    }

    stage('Curl'){
        STAGE = env.STAGE_NAME
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }

    stage('Nexus') {
        STAGE = env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'ejemplo-gradle',
        packages: [
            [
                $class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]
                ],
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '1.0.0'
                ]
            ]
        ]
    }

}

return this;