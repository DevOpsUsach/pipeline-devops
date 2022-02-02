/*

	forma de invocación de método call:

	def ejecucion = load 'script.groovy'
	ejecucion.call()

*/

def call(String pipelineType){

figlet pipelineType
figlet 'Maven'  

if (pipelineType == 'CI'){
    figlet 'Integración Continua'
    stage('Compile') {
    figlet 'Compile'
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"  
    sh './mvnw clean compile -e'
  }
    stage('Test') {
    figlet 'Test'  
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"    
    sh "./mvnw clean test -e"
  }
    stage('Package') {
    figlet 'Package'  
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"    
    sh "./mvnw clean package -e"
  }
  stage('Sonarqube Analisis') {
    figlet 'Sonarqube Analisis'
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"  
    def scannerHome = tool 'sonar-scanner';
    withSonarQubeEnv('sonarqube-server') { 
      sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=pipeline-devops-labm3-maven -Dsonar.sources=src -Dsonar.java.binaries=."
    }
  }
  stage('Run Jar') {
    figlet 'Run Jar'
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"    
    sh "JENKINS_NODE_COOKIE=dontKillMe nohup bash mvnw spring-boot:run &"
  }
    stage('Rest'){
    figlet 'Rest'
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"    
    sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
  }
    stage('NexusCI') {
    figlet 'NexusCI'    
    STAGE = env.STAGE_NAME
    sh 'env'
    println "Stage: ${env.STAGE_NAME}"    
    nexusPublisher nexusInstanceId: 'nexus',
    nexusRepositoryId: 'pipeline-devops-labm3',
    packages: [
                [
                    $class: 'MavenPackage',
                    mavenAssetList: [
                    [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/DevOpsUsach2020-0.0.1.jar"]
                    ],
                    mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '0.0.2'
                    ]
                ]
            ]
    }

} else {
  figlet 'Delivery Continuo'
  stage('downloadNexus'){
  figlet 'Download Nexus'  
  STAGE = env.STAGE_NAME
  sh 'env'
  println "Stage: ${env.STAGE_NAME}"    
  sh "curl -X GET -u 'admin:koba' http://localhost:8081/repository/pipeline-devops-labm3/com/devopsusach2020/DevOpsUsach2020/0.0.2/DevOpsUsach2020-0.0.2.jar -O"
  sh "echo ${env.WORKSPACE}"
  sh "mv DevOpsUsach2020-0.0.2.jar DevOpsUsach2020-1.0.2.jar"
  sh "ls -ltr"
  }
  stage('runDownloadedJar'){
  figlet 'Run Downloaded Jar'   
  STAGE = env.STAGE_NAME
  sh 'env'
  println "Stage: ${env.STAGE_NAME}"   
  sh 'JENKINS_NODE_COOKIE=dontKillMe nohup bash mvnw spring-boot:run &'
  }
  stage('Rest'){
  figlet 'Rest'  
  STAGE = env.STAGE_NAME
  sh 'env'
  println "Stage: ${env.STAGE_NAME}" 
  sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
  }
  stage('NexusCD') {
  figlet 'NexusCD'    
  STAGE = env.STAGE_NAME
  sh 'env'
  println "Stage: ${env.STAGE_NAME}"    
  nexusPublisher nexusInstanceId: 'nexus',
  nexusRepositoryId: 'pipeline-devops-labm3',
  packages: [
              [
                  $class: 'MavenPackage',
                  mavenAssetList: [
                  [classifier: '', extension: '', filePath: "${env.WORKSPACE}/DevOpsUsach2020-1.0.2.jar"]
                  ],
                  mavenCoordinate: [
                  artifactId: 'DevOpsUsach2020',
                  groupId: 'com.devopsusach2020',
                  packaging: 'jar',
                  version: '1.0.2'
                  ]
              ]
          ]
  }
}

}

return this;