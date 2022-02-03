$ cat gradle.groovy
/*
        forma de invocación de método call:
        def ejecucion = load 'script.groovy'
        ejecucion.call()
*/

def call(){

        stage('BuildTestJar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "./gradlew clean build"
                }
        }
        stage('Sonar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    def scannerHome = tool 'sonar-scanner';
                    withSonarQubeEnv('sonar-server') {
                    bat "C:/Users/Patric~1/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner/bin/sonar-scanner.bat -Dsonar.projectKey=ejemplo-gradle -Dsonar.sources=src -Dsonar.java.binaries=build"
                    }
                }
        }
        stage('Run') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start /min gradlew bootRun &"
                    sleep 20
                }
        }
        stage('TestApp') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "start chrome http://localhost:8082/rest/mscovid/test?msg=testing"
                }
        }
        stage('NexusUpload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    nexusPublisher nexusInstanceId: 'test-nexus', nexusRepositoryId: 'test.nexus',
                    packages: [[$class: 'MavenPackage',
                        mavenAssetList: [[classifier: '',
                        extension: '',
                        filePath: 'C:/Users/Patric~1/Desktop/Ejercicio/ejemplo-gradle/build/libs/DevOpsUsach2020-0.0.1.jar']],
                        mavenCoordinate: [artifactId: 'DevOpsUsach2020',
                        groupId: 'com.devopsusach2020',
                        packaging: 'jar',
                        version: '0.0.1']
                        ]]
                }
        }
        stage('NexusDownload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    env.STAGE=env.STAGE_NAME
                    bat "curl -X GET -u admin:Pelusa50# http://localhost:8081/repository/test.nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
                    bat "dir"
                }
        }

}

return this;
