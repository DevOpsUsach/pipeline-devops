package helpers

def diff(String ramaOrigen, String ramaDestino) {
    println "Este método realiza un diff de ${ramaOrigen} y ${ramaDestino}"
    checkout(ramaOrigen)
    checkout(ramaDestino)
    if (ejecucion.checkOs() == 'Windows') {
        bat "git diff ${ramaOrigen} ${ramaDestino}"
    }else {
        sh "git diff ${ramaOrigen} ${ramaDestino}"
    }
}

def createRelease(String ramaOrigen, String ramaDestino) {
    println "Este método crea una rama ${ramaDestino} basada en ${ramaOrigen}"
    withCredentials([gitUsernamePassword(credentialsId: 'token_github_jenkins', gitToolName: 'Default')]) {
        if (ejecucion.checkOs() == 'Windows') {
            bat 'git fetch --all'
            checkout(ramaOrigen)
            bat """
            git branch -D ${ramaDestino} || true
            git checkout -b ${ramaDestino}
            git push origin ${ramaDestino}
        """
        }
        else {
            sh 'git fetch --all'
            checkout(ramaOrigen)
            sh """
            git branch -D ${ramaDestino} || true
            git checkout -b ${ramaDestino}
            git push origin ${ramaDestino}
        """
        }
    }
}

def merge(String ramaOrigen, String ramaDestino) {
    println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"
    withCredentials([gitUsernamePassword(credentialsId: 'token_github_jenkins', gitToolName: 'Default')]) {
        if (ejecucion.checkOs() == 'Windows') {
            bat 'git fetch --all'
            checkout(ramaOrigen)
            checkout(ramaDestino)
            bat """
            git merge ${ramaOrigen}
            git push origin ${ramaDestino}
        """
        }else {
            sh 'git fetch --all'
            checkout(ramaOrigen)
            checkout(ramaDestino)
            sh """
            git merge ${ramaOrigen}
            git push origin ${ramaDestino}
        """
        }
    }
}

def tag(String ramaOrigen, String ramaDestino) {
    println "Este método realiza un tag en master de ${ramaOrigen}"
    if (ramaOrigen.contains('release-v')) {
        withCredentials([gitUsernamePassword(credentialsId: 'token_github_jenkins', gitToolName: 'Default')]) {
                checkout(ramaDestino)
                def tagValue = ramaOrigen.split('release-')[1]

                if (ejecucion.checkOs() == 'Windows') {
                bat """
                    git tag ${tagValue}
                    git push origin ${tagValue}
                """
                }else {
                sh """
                    git tag ${tagValue}
                    git push origin ${tagValue}
                """
                }
        }
    } else {
        error "La rama ${ramaOrigen} no cumple con nomenclatura definida para rama release (release-v(major)-(minor)-(patch))."
    }
}

def checkout(String rama) {
    if (ejecucion.checkOs() == 'Windows') {
        //bat "git reset --hard HEAD"
        bat "git checkout ${rama}"
        bat "git pull --no-rebase origin ${rama}"
    }else {
        sh 'git reset --hard HEAD'
        sh "git checkout ${rama}"
        sh "git pull --no-rebase origin ${rama}"
    }
}

return this
