import helpers.*
def call(String pipelineType){

figlet pipelineType
figlet 'Maven'  

if (pipelineType == 'CI'){
    figlet 'Integración Continua'
    stage('compile') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {      
        figlet 'Compile'
        STAGE = env.STAGE_NAME
        sh './mvnw clean compile -e'
      }
    }
    stage('unitTest') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
        figlet 'Test'  
        STAGE = env.STAGE_NAME
        sh "./mvnw clean test -e"
      }
    }
    stage('jar') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {      
        figlet 'Package'  
        STAGE = env.STAGE_NAME        
        sh "./mvnw clean package -e"
      }
    }
    stage('sonar') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
        figlet 'Sonarqube Analisis'
        STAGE = env.STAGE_NAME        
        def scannerHome = tool 'sonar-scanner';
        withSonarQubeEnv('sonarqube-server') { 
          sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=pipeline-devops-labm3-maven -Dsonar.sources=src -Dsonar.java.binaries=."
        }
      }
    }
    /*stage('run') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
        figlet 'Run Jar'
        STAGE = env.STAGE_NAME        
        sh "JENKINS_NODE_COOKIE=dontKillMe nohup bash mvnw spring-boot:run &"
        sleep 10
      }
    }
    stage('test'){
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
        figlet 'Test'
        STAGE = env.STAGE_NAME        
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
      }
    }*/
    stage('nexusUpload') {
      if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
        figlet 'NexusCI'    
        STAGE = env.STAGE_NAME        
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
                        version: '0.0.1'
                        ]
                    ]
                ]
      }
    }

    if ("${env.GIT_BRANCH}" == "develop"){
        stage('gitCreateRelease') {
            if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {         
                figlet env.STAGE_NAME           
                STAGE = env.STAGE_NAME
                def workflow = new helpers.Workflow()
                workflow.creacionRelease()
            }
        }
    }


} else {
  figlet 'Delivery Continuo'
  stage('gitDiff'){
        
        if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
            env.STAGE = env.STAGE_NAME
            figlet env.STAGE_NAME
            def git = new helpers.Git()
            git.diff('main', "${env.GIT_LOCAL_BRANCH}")
        }
  }
  stage('nexusDownload'){
    if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
      figlet 'Download Nexus'  
      STAGE = env.STAGE_NAME
      sh "curl -X GET -u 'admin:koba' http://localhost:8082/repository/pipeline-devops-labm3/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
      sh "echo ${env.WORKSPACE}"      
      sh "ls -ltr"
    }
  }
  stage('run'){
    if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
      figlet 'Run Downloaded Jar'   
      STAGE = env.STAGE_NAME      
      sh 'JENKINS_NODE_COOKIE=dontKillMe nohup bash mvnw spring-boot:run &'
      sleep 10
    }
  }
  stage('test'){
    if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
      figlet 'Rest'  
      STAGE = env.STAGE_NAME
      sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
  }
  stage('gitMergeAndTag') {
        
        STAGE = env.STAGE_NAME
        figlet "Stage: ${env.STAGE_NAME}"        
        def workflow = new helpers.Workflow()
        workflow.mergeAndTag("${env.GIT_LOCAL_BRANCH}")

  }

  
  /*stage('nexuscd') {
    if (env.PSTAGE == env.STAGE_NAME || env.PSTAGE == 'ALL') {
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
  }*/
}

}

return this;