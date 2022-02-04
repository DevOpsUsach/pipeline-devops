/*
        forma de invocación de método call:
        def ejecucion = load 'script.groovy'
        ejecucion.call()
*/

def call(){

        pipeline {
        agent any

                parameters {
                        choice choices: ['gradle', 'maven'], description: 'Indicar Herramienta de Construcción', name: 'buildTool'
                        string(name: 'stage', defaultValue: '')
                }

                stages {
                        stage('PipeLine') {
                                steps {
                                        script {
						println checkOs()
                                                if (params.stage.length() == 0) {
                                                        println "ALL"
                                                        env.STAGE = "NULL"
                                                        env.PSTAGE = "ALL"
                                                        if (params.buildTool == "gradle") { gradle(verifyBranchName()) } else { maven(verifyBranchName()) }
                                                } else {
                                                        println "Selectivo"
                                                        def stages = params.stage.split(";")
                                                        for (i=0; i < stages.size(); i++) {
                                                                env.STAGE = "NULL"
                                                                env.PSTAGE = stages[i]
                                                                if (params.buildTool == "gradle") { gradle(verifyBranchName()) } else { maven(verifyBranchName()) }
                                                                if (env.STAGE == 'NULL') { break }
                                                        }
                                                }
                                        }
                                }
                        }
                }

                post {
                        success {
                                script {
                                        if (env.STAGE == 'NULL' && env.PSTAGE != 'ALL') {
                                                slackSend color: 'danger', message: "[Grupo 3][Pipeline: ${env.JOB_NAME}][Branch: ${env.GIT_LOCAL_BRANCH}][Tools: ${params.buildTool}][Stage: ${env.PSTAGE}][Resultado: NOK]"
                                                error "Ejecución fallida en stage ${env.PSTAGE}"
                                        } else {
                                                slackSend color: 'good', message: "[Grupo 3][${env.JOB_NAME}][Branch: ${env.GIT_LOCAL_BRANCH}][${params.buildTool}][Resultado: OK]"
                                        }
                                }

                        }

                        failure {
                                script {
                                        if (env.STAGE != 'NULL' || env.PSTAGE == 'ALL') {
                                                slackSend color: 'danger', message: "[Grupo 3][Pipeline: ${env.JOB_NAME}][Branch: ${env.GIT_LOCAL_BRANCH}][Tools: ${params.buildTool}][Stage: ${env.STAGE}][Resultado: NOK]"
                                                error "Ejecución fallida en stage ${env.STAGE}"
                                        }
                                }
                        }
                }
        }

}

def verifyBranchName(){
    if (env.GIT_BRANCH.contains('develop') || env.GIT_BRANCH.contains('feature-')) {
        return 'CI'
    } else {
        return 'CD'
    }
}

def checkOs(){
    if (isUnix()) {
        def uname = sh script: 'uname', returnStdout: true
        if (uname.startsWith("Darwin")) {
            return "Macos"
        }
        // Optionally add 'else if' for other Unix OS
        else {
            return "Linux"
        }
    }
    else {
        return "Windows"
    }
}

return this;
