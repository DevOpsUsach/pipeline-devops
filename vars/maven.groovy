/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(String[] stages){

    def allStages = false;

    stage('Validate'){
        if (stages.size() == 0){
            allStages = true
        }
    }
  
    stage('Compile') {
        if (stages.contains("Compile") || allStage) {
            sh "mvn clean compile -e"
        }
    }

    stage('Test') {
        if (stages.contains("Test")) {
            sh "mvn clean test -e"
        }
    }

    stage('Jar') {
        if (stages.contains("Jar")) {
            sh "mvn clean package -e"
        }
    }

    stage('SonarQube') {
        if (stages.contains("SonarQube")) {
            def scannerHome = tool 'SonarQube Scanner 4.6.2'
                withSonarQubeEnv('SonarQube local'){
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=sonarqube-token -Dsonar.java.binaries=build"
            }
        }
    }

    stage('Run') {
        if (stages.contains("Run")) {
            sh "nohup mvn spring-boot:run &"
        }
    }

    stage('Wait') {
        if (stages.contains("Wait")) {
            println "Sleep 20 seconds"
            sleep(time: 20, unit: "SECONDS")
        }
    }

    stage('Curl') {
        if (stages.contains("Curl")) {
            sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
        }
    }

    stage('Nexus') {
        if (stages.contains("Nexus")) {
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

}

return this;