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
                gradle(verifyBranchName()) /* archivo gradle.groovy */
              } else {
                maven(verifyBranchName()) /* archivo maven.groovy */
              }
            }
          }
        }
    }

    post {
      success {
        slackSend color: 'good', iconEmoji: "beer", message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución de build número '${env.BUILD_NUMBER}' exitosa."
      }

      failure {
        slackSend color: 'danger', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] La ejecución del build número ${env.BUILD_NUMBER} falló."
        error "Ejecución del build número ${env.BUILD_NUMBER} fallida."
      }
    }
  }
}

def verifyBranchName(){
  if(env.GIT_BRANCH.contains('feature-') || env.GIT_BRANCH.contains('develop') ){
    return 'CI'
  }
  return 'CD'
}

return this;