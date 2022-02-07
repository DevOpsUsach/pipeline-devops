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
		    if (checkOs()=="Windows") {
                    	bat "./gradlew clean build"
		    }
                }
        }
        stage('sonar') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
                    env.STAGE=env.STAGE_NAME
                    def scannerHome = tool 'scaner-devops';
                    withSonarQubeEnv('sonar-devops') {
                    bat "C:/Users/Patric~1/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonar-scanner/bin/sonar-scanner.bat -Dsonar.projectKey=pipeline-devops-gradle -Dsonar.sources=src -Dsonar.java.binaries=build"
                    }
                }
        }
        stage('nexusUpload') {
                if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
		    figlet "Stage: ${env.STAGE_NAME}"
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
                    bat "start /min gradlew bootRun &"
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
                    env.WORKSPACE="C:/Users/Patric~1/.jenkins/workspace/Taller-M3-CI-CD/Taller-M3-CD"
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


