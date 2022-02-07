import helpers.*

def call(String pipelineType){

figlet pipelineType
figlet 'Maven'
println "GIT_BRANCH: ${env.GIT_BRANCH}"
println "GIT_LOCAL_BRANCH: ${env.GIT_LOCAL_BRANCH}"

if (pipelineType == 'CI'){
        figlet 'Integracion Continua'
        stage('compile') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
		    if (ejecucion.checkOs()=="Windows") {
                    	bat "./mvnw.cmd clean compile -e"
		    } else {
		    	sh "./mvnw clean compile -e"
		    }
                }
        }
        stage('unitTest') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
		    if (ejecucion.checkOs()=="Windows") {
                    	bat "./mvnw.cmd clean test -e"
		    } else {
		    	sh "./mvnw clean test -e"
		    }
                }
        }
        stage('jar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
		    if (ejecucion.checkOs()=="Windows") {
                    	bat "./mvnw.cmd clean package -e"
		    } else {
		    	sh "./mvnw clean package -e"
		    }
                }
        }
        stage('sonar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    def scannerHome = tool 'sonar-scanner';
                    withSonarQubeEnv('sonarqube-server') {
		    if (ejecucion.checkOs()=="Windows") {
                    	bat "C:/Users/Patric~1/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner/bin/sonar-scanner.bat -Dsonar.projectKey=pipeline-devops -Dsonar.sources=src -Dsonar.java.binaries=build"
		    } else {
		    	sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=pipeline-devops-labm3-maven -Dsonar.sources=src -Dsonar.java.binaries=build"
		    }
                    }
                }
        }
        stage('nexusUpload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
		    if (ejecucion.checkOs()=="Windows") {
                    	env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/er-M3-CI-CD_Taller-M3-CI_develop"
		    }
                    nexusPublisher nexusInstanceId: 'devops-nexus', nexusRepositoryId: 'devops-nexus',
                    packages: [[$class: 'MavenPackage',
                        mavenAssetList: [[classifier: '',
                        extension: '',
                        filePath: "${env.WORKSPACE}/build/DevOpsUsach2020-0.0.1.jar"]],
                        mavenCoordinate: [artifactId: 'DevOpsUsach2020',
                        groupId: 'com.devopsusach2020',
                        packaging: 'jar',
                        version: '0.0.1']
                        ]]
                }
        }

	if ("${env.GIT_BRANCH}" == "develop") {
		stage('gitCreateRelease') {
                	if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    		figlet "Stage: ${env.STAGE_NAME}"
                    		env.STAGE=env.STAGE_NAME
			}
		}
	}
} else {
        figlet 'Delivery Continuo'
        stage('nexusDownload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    bat "curl -X GET -u admin:Pelusa50# http://localhost:8082/repository/devops-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
                    bat "dir"
                }
        }
        stage('run') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    bat "start /min mvnw spring-boot:run &"
                    sleep 20
                }
        }
        stage('test') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    bat "start chrome http://localhost:8081/rest/mscovid/test?msg=testing"
                }
        }
        stage('nexusUpload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.WORKSPACE='C:/Users/Patric~1/.jenkins/workspace/Taller-M3-CI-CD/Taller-M3-CD'
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

	stage('gitMergeMain') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    def git = new helpers.Git()
                    git.merge("${env.GIT_LOCAL_BRANCH}", 'main')
                }
        }

        stage('gitMergeDevelop') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    def git = new helpers.Git()
                    git.merge("${env.GIT_LOCAL_BRANCH}", 'develop')
                }
        }

        stage('gitTagMaster') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
                    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    def git = new helpers.Git()
                    git.tag("${env.GIT_LOCAL_BRANCH}", 'main')
                }
        }

}
}

return this;

