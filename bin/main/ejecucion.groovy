pipeline {

	agent any

  environment {
    STAGE = ""
  }

  parameters {
    choice choices: ['gradle', 'maven'],
    description: 'Indicar herramienta de construcción',
    name: 'buildTool'
  }

  tools {
    maven "3.8.4"
  }

	stages{
		stage('Pipeline'){
			steps{
				script{
					println "Pipeline"
          if (params.buildTool == "gradle"){
            gradle() /* archivo gradle.groovy */
          } else {
            def ejecucion = load "maven.groovy"
            maven() /* archivo maven.groovy */
          }
				}
			}
		}
	}

  post {
    success {
      slackSend color: "good", message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución exitosa."
    }

    failure {
      slackSend color: "danger", message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage '${STAGE}' build número ${env.BUILD_NUMBER}."
      error "Ejecución fallida en stage ${STAGE}"
    }
  }
}