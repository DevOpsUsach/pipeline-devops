/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
    stage('Compile') {
        sh "mvn clean compile -e"
    }

    stage('Test') {
        sh "mvn clean test -e"
    }

    stage('Jar') {
        sh "mvn clean package -e"
    }

    stage('SonarQube analysis') {
        def scannerHome = tool 'SonarQube Scanner 4.6.2'
            withSonarQubeEnv('SonarQube local'){
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=sonarqube-token -Dsonar.java.binaries=build"
        }
    }

    stage('Run') {
        sh "nohup mvn spring-boot:run &"
    }

    stage('Wait') {
        println "Sleep 20 seconds"
        sleep(time: 20, unit: "SECONDS")
    }

    stage('Curl') {
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }

    stage('Nexus') {
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'ejemplo-maven',
        packages: [
            [
                $class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/DevOpsUsach2020-0.0.1.jar"]
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