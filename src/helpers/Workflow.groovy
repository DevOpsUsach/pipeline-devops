package helpers

def creacionRelease(){

    crearRelease = true                
    timeoutMillis = 10000
    
    try {
        timeout(time: timeoutMillis, unit: 'MILLISECONDS') {
        input '¿Desea crear un nuevo release?'
        }
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
        crearRelease = false
    }
    
    if (crearRelease) {
        //currentBuild.result = 'SUCCESS'
        
        nombreReleaseIngresado = true
        nombreRelease = ""
        timeoutMillis = 60000
        
        try {
        timeout(time: timeoutMillis, unit: 'MILLISECONDS') {
            def releaseSemVer = input(
                id: 'userInput', 
                message: '¿Nombre del Release?', 
                ok: 'Crear Release',
                parameters: [
                    string(
                        name: 'nombreRelease',
                        defaultValue: 'release-v', 
                        description: 'Nombre de la rama release a crear. Usar el formato release-v(major).(minor).(patch)',
                        trim: true
                    )
                ])
            nombreRelease = releaseSemVer
        }
        } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
            nombreReleaseIngresado = false
        }

        if (nombreReleaseIngresado){
            //echo ("La versión ingresada es: "+nombreRelease)                        
            if (nombreRelease ==~ /release-v[0-9]+-[0-9]+-[0-9]+/){
                def git = new helpers.Git()
                git.createRelease("${env.GIT_LOCAL_BRANCH}", nombreRelease)
            }else{
                error("El nombre del release no cumple con el patrón requerido. Nombre ingresado: " + nombreRelease)
            }
        }else{
            println "No hubo respuesta del usuario para el nombre del release. Continuando el pipeline."
        }
    } else {
        //currentBuild.result = 'ABORTED'
        println "No hubo respuesta del usuario para crear un release. Continuando el pipeline."
    }


}

return this;