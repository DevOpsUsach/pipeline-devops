def call(){
  pipeline {
    agent any

    parameters {
      choice choices: ['gradle', 'maven'], description: 'Indicar herramienta de construcción', name: 'buildTool'
      string(name: 'stage', defaultValue: '', description: 'Nombre de stage a ejecutar, para multiples stage usar ;')
    }

    tools {
      maven '3.8.4'
    }

    stages {
        stage('Pipeline') {
          steps {
            script {
              println "${params.stage}"

              String[] stages;
              stages = params.stage.split(';');

              println "${stages}"

              if (params.buildTool == 'gradle') {
                gradle() /* archivo gradle.groovy */
              } else {
                maven(stages) /* archivo maven.groovy */
              }
            }
          }
        }
    }

    post {
      success {
        slackSend color: 'good', iconEmoji: "::beer::", message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución de build número '${env.BUILD_NUMBER}' exitosa."
      }

      failure {
        slackSend color: 'danger', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] La ejecución del build número ${env.BUILD_NUMBER}. falló"
        error "Ejecución del build número ${env.BUILD_NUMBER} fallida"
      }
    }
  }
}

return this;