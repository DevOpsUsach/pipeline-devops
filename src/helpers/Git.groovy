package helpers

def diff(String ramaOrigen, String ramaDestino){
	println "Este método realiza un diff de ${ramaOrigen} y ${ramaDestino}"
	checkout(ramaOrigen)
	checkout(ramaDestino)
    sh "git diff ${ramaOrigen} ${ramaDestino}"
}

def merge(String ramaOrigen, String ramaDestino){
	println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"
	withCredentials([gitUsernamePassword(credentialsId: 'token_github_jenkins', gitToolName: 'Default')]){

		sh "git fetch --all"
        checkout(ramaOrigen)
        checkout(ramaDestino)
		sh """
            git merge ${ramaOrigen}
            git push origin ${ramaDestino}
        """
	}
}

def tag(String ramaOrigen, String ramaDestino){
	println "Este método realiza un tag en master de ${ramaOrigen}"
	if (ramaOrigen.contains('release-v')){
        withCredentials([gitUsernamePassword(credentialsId: 'token_github_jenkins', gitToolName: 'Default')]) {

				checkout(ramaDestino)
				def tagValue = ramaOrigen.split('release-')[1]

                sh """
					git tag ${tagValue}
					git push origin ${tagValue}
				"""
        }

	} else {
		error "La rama ${ramaOrigen} no cumple con nomenclatura definida para rama release (release-v(major)-(minor)-(patch))."
	}
}

def checkout(String rama){
	sh "git reset --hard HEAD"
    sh "git checkout ${rama}"
	sh "git pull --no-rebase origin ${rama}"
}


return this;