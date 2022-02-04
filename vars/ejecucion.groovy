def call(){
  
pipeline {

	agent any

    environment {
	    STAGE = ''
	}

    parameters {
    choice (name: 'buildTool', choices: ['gradle', 'maven'])
    string (name: 'stage', defaultValue: '')
    }

	stages{
        stage('Pipeline'){
            steps{
                script{
                    env.STAGE = null
                    env.PSTAGE = null
                    if (params.stage == ''){
                        figlet 'Stage Vacío'
                        env.PSTAGE = "ALL"
                        if (params.buildTool == 'gradle') {
                            gradle(verifyBranchName())
                        }else {
                            maven(verifyBranchName())
                        }
                    } else {
                        def stages = params.stage.split(";")
                        println "STAGES: ${stages}"
                        println "CANTIDAD de STAGES: ${stages.size()}"
                        for (i=0; i < stages.size(); i++) {
                            env.STAGE = null
                            env.PSTAGE = stages[i]
                            println "ESTOY EN: ${env.PSTAGE}" 
                            if (params.buildTool == "gradle") {
                                gradle(verifyBranchName()) 
                            } else {
                                maven(verifyBranchName()) 
                            }
                        }    
                    }
                }
            }
        }
	}

post {

    success {
        slackSend color: 'good', message: "Se ejecuta Build [${BUILD_ID}] por [${env.USER}] en Job/Branch [${env.JOB_NAME}] opción: [${params.buildTool}] - Ejecución exitosa."
    }

    failure {
        slackSend color: 'danger', message: "Se ejecuta Build [${BUILD_ID}] por [${env.USER}] en Job/Branch [${env.JOB_NAME}] opción: [${params.buildTool}] - Ejecución fallida en stage: ${STAGE}."
        error "Ejecución fallida en stage: ${STAGE}"
    }
}
}

}

def verifyBranchName(){
    if(env.GIT_BRANCH.contains('develop') || env.GIT_BRANCH.contains('feature-')){
        return 'CI'
    } 
    else {
        return 'CD'
    }

}	

return this;
