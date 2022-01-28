/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(String[] stages){

    boolean allStages = false;
    String[] availableStageList = ["BuildTest", "SonarQube", "Run", "Wait", "Curl", "Nexus"];

    stage('Validate'){
        if (stages.size() == 0){
            allStages = true
        }else{
            for(_stage in stages){
                if(!availableStageList.contains(_stage) ){
                    error("El stage ${_stage} no existe para ${params.stage}")
                }
            }
        }
    }
  
    stage('BuildTest'){
        if (stages.contains("BuildTest") || allStages ) {
            sh 'env'
            sh './gradlew clean build'
        }
    }

    stage('SonarQube') {
        if (stages.contains("SonarQube") || allStages ) {
            def scannerHome = tool 'SonarQube Scanner 4.6.2'
                withSonarQubeEnv('SonarQube local'){
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=sonarqube-token -Dsonar.java.binaries=build"
            }
        }
    }

    stage('Run'){
        if (stages.contains("Run") || allStages ) {
            sh "nohup bash gradlew bootRun &"
        }
    }

    stage('Wait') {
        if (stages.contains("Wait") || allStages ) {
            println "Sleep 20 seconds"
            sleep(time: 20, unit: "SECONDS")
        }
    }

    stage('Curl'){
        if (stages.contains("Curl") || allStages ) {
            sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
        }
    }

    stage('Nexus') {
        if (stages.contains("Nexus") || allStages ) {
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

}

return this;