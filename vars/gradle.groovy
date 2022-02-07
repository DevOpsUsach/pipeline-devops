import helpers.*

def call(String pipelineType){

figlet pipelineType
figlet 'Gradle'
println "GIT_BRANCH: ${env.GIT_BRANCH}"
println "GIT_LOCAL_BRANCH: ${env.GIT_LOCAL_BRANCH}"

if (pipelineType == 'CI'){
        figlet 'Integracion Continua'
        stage('build') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
		    if (ejecucion.checkOs()=="Windows") {
                    	bat "./gradlew clean build"
		    } else {
		    	sh "./gradlew clean build"
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
                        scannerHomeW="C:/Users/Patric~1/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner"
                        bat "${scannerHomeW}/bin/sonar-scanner -Dsonar.projectKey=pipeline-devops-labm3-gradle -Dsonar.sources=src -Dsonar.java.binaries=build/libs"
                    } else {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=pipeline-devops-labm3-gradle -Dsonar.sources=src -Dsonar.java.binaries=build/libs"
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
                    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'pipeline-devops-labm3',
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
                    if (ejecucion.checkOs()=="Windows") {
                        bat "curl -X GET -u 'admin:koba' http://localhost:8082/repository/pipeline-devops-labm3/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
                        bat "echo ${env.WORKSPACE}"
                        bat "dir"
                    } else {
                        sh "curl -X GET -u 'admin:koba' http://localhost:8082/repository/pipeline-devops-labm3/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
                        sh "echo ${env.WORKSPACE}"
                        sh "ls -ltr"
                    }
                }
        }
        stage('run') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    if (ejecucion.checkOs()=="Windows") {
                    	bat "start /min gradlew bootRun &"
                    } else {
    			sh "JENKINS_NODE_COOKIE=dontKillMe nohup bash gradlew bootRun &"
                    }
                    sleep 20
                }
        }
        stage('test') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    if (ejecucion.checkOs()=="Windows") {
                    	bat "start chrome http://localhost:8081/rest/mscovid/test?msg=testing"
                    } else {
			sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
                    }
                }
        }
        stage('nexusUpload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    if (ejecucion.checkOs()=="Windows") {
                    	env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/Taller-M3-CI-CD/Taller-M3-CD"
		    }
                    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'pipeline-devops-labm3',
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


