def call(){
  pipeline {
    agent any

    parameters {
      choice choices: ['gradle', 'maven'], description: 'Indicar herramienta de construcción', name: 'buildTool'

    }

    tools {
      maven '3.8.4'
    }

    stages {
        stage('Pipeline') {
          steps {
            script {
              if (params.buildTool == 'gradle') {
            gradle() /* archivo gradle.groovy */
              } else {
            maven() /* archivo maven.groovy */
              }
            }
          }
        }
    }

    post {
      success {
        slackSend color: 'good', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución de stage '${env.STAGE_NAME}' exitosa."
      }

      failure {
        slackSend color: 'danger', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage '${env.STAGE_NAME}' build número ${env.BUILD_NUMBER}."
        error "Ejecución fallida en stage ${env.STAGE_NAME}"
      }
    }
  }
}