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
                                                slackSend color: 'danger', message: "[${env.BUILD_USER}][${env.USUARIO}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage ${env.PSTAGE}"
                                                error "Ejecución fallida en stage ${env.PSTAGE}"
                                        } else {
                                                slackSend color: 'good', message: "[${env.BUILD_USER}][${env.USUARIO}][${env.JOB_NAME}][${params.buildTool}] Ejecución Exitosa!"
                                        }
                                }

                        }

                        failure {
                                script {
                                        if (env.STAGE != 'NULL' || env.PSTAGE == 'ALL') {
                                                slackSend color: 'danger', message: "[${env.BUILD_USER}][${env.USUARIO}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage ${env.STAGE}"
                                                error "Ejecución fallida en stage ${env.STAGE}"
                                        }
                                }
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
