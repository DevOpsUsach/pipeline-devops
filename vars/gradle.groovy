/*
        forma de invocación de método call:
        def ejecucion = load 'script.groovy'
        ejecucion.call()
*/

def call(String pipelineType){

figlet pipelineType
figlet 'Gradle'
println "${env.GIT_BRANCH}"

if (pipelineType == 'CI'){
        figlet 'Integracion Continua'
        stage('build') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "./gradlew clean build"
                }
        }
        stage('sonar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    def scannerHome = tool 'scaner-devops';
                    withSonarQubeEnv('sonar-devops') {
                    //bat "C:/Users/Patric~1/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner/bin/sonar-scanner.bat -Dsonar.projectKey=pipeline-devops-gradle -Dsonar.sources=src -Dsonar.java.binaries=build"
                    }
                }
        }
        stage('run') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start /min gradlew bootRun &"
                    sleep 20
                }
        }
        stage('test') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start chrome http://localhost:8081/rest/mscovid/test?msg=testing"
                }
        }
        stage('nexusci') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/er-M3-CI-CD_Taller-M3-CI_develop"
                    env.STAGE=env.STAGE_NAME
                    nexusPublisher nexusInstanceId: 'devops-nexus', nexusRepositoryId: 'devops-nexus',
                    packages: [[$class: 'MavenPackage',
                        mavenAssetList: [[classifier: '',
                        extension: '',
                        filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]],
                        mavenCoordinate: [artifactId: 'DevOpsUsach2020',
                        groupId: 'com.devopsusach2020',
                        packaging: 'jar',
                        version: '0.0.1']
                        ]]
                }
        }
} else {
        figlet 'Delivery Continuo'
        stage('nexusDownload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "curl -X GET -u admin:Pelusa50# http://localhost:8082/repository/devops-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
                    bat "dir"
                }
        }
        stage('run') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start /min gradlew bootRun &"
                    sleep 20
                }
        }
        stage('test') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start chrome http://localhost:8081/rest/mscovid/test?msg=testing"
                }
        }
        stage('nexuscd') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/Taller-M3-CI-CD/Taller-M3-CD"
                    env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/er-M3-CI-CD_Taller-M3-CI_develop"
                    env.STAGE=env.STAGE_NAME
                    nexusPublisher nexusInstanceId: 'devops-nexus', nexusRepositoryId: 'devops-nexus',
                    packages: [[$class: 'MavenPackage',
                        mavenAssetList: [[classifier: '',
                        extension: '',
                        filePath: "${env.WORKSPACE}/DevOpsUsach2020-0.0.1.jar"]],
                        mavenCoordinate: [artifactId: 'DevOpsUsach2020',
                        groupId: 'com.devopsusach2020',
                        packaging: 'jar',
                        version: '1.0.0']
                        ]]
                }
        }
}
}

return this;


