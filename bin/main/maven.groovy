def call(String[] stages, String pipelineType){

    figlet "Maven"

    if (pipelineType == "CI"){
        figlet "Integración Continua"
    
        stage('Compile') {
            figlet "Stage: ${env.STAGE_NAME}"
            sh "mvn clean compile -e"
        }

        stage('Test') {
            figlet "Stage: ${env.STAGE_NAME}"
            sh "mvn clean test -e"
        }

        stage('Jar') {
            figlet "Stage: ${env.STAGE_NAME}"
            sh "mvn clean package -e"
        }

        stage('SonarQube') {
            figlet "Stage: ${env.STAGE_NAME}"
            def scannerHome = tool 'SonarQube Scanner 4.6.2'
                withSonarQubeEnv('SonarQube local'){
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=sonarqube-token -Dsonar.java.binaries=build"
            }
        }

        stage('Run'){
            figlet "Stage: ${env.STAGE_NAME}"
            sh "nohup bash gradlew bootRun &"
        }

        stage('Wait') {
            figlet "Stage: ${env.STAGE_NAME}"
            figlet "Sleep 20 seconds"
            sleep(time: 20, unit: "SECONDS")
        }

        stage('Curl'){
            figlet "Stage: ${env.STAGE_NAME}"
            sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
        }

        stage('UploadSnapshotJar') {
            figlet "Stage: ${env.STAGE_NAME}"
            nexusPublisher nexusInstanceId: 'nexus',
            nexusRepositoryId: 'ejemplo-gradle',
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
                        version: '0.0.1å'
                    ]
                ]
            ]
        }
    } else {
        figlet "Delivery Continuo"

        stage('DownloadSnapshotJar'){
            figlet "Stage: ${env.STAGE_NAME}"
            sh "curl -X GET -u admin:q1w2e3r4 http://localhost:8082/repository/nexus-taller10/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
        }

        stage('RunSnapshotJar'){
            figlet "Stage: ${env.STAGE_NAME}"
            sh "nohup java -jar DevOpsUsach2020-0.0.1.jar &"
            figlet "Sleep 20 seconds"
            sleep(time: 20, unit: "SECONDS")
        }

        stage('TestSnapshotJar'){
            figlet "Stage: ${env.STAGE_NAME}"
            sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
        }

        stage('UploadSnapshotJar'){
            steps {
                nexusArtifactUploader artifacts: [
                        [
                            artifactId: 'DevOpsUsach2020', 
                            classifier: '', 
                            file: 'DevOpsUsach2020-0.0.1.jar', 
                            type: 'jar'
                        ]
                    ], 
                    credentialsId: 'nexus-admin-user', 
                    groupId: 'com.devopsusach2020', 
                    nexusUrl: 'localhost:8082', 
                    nexusVersion: 'nexus3', 
                    protocol: 'http', 
                    repository: 'ejemplo-gradle', 
                    version: '1.0.0'
                }
        }
    }

}

return this;