package helpers

/*
def merge(String ramaOrigen, String ramaDestino){
	println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"

	checkout(ramaOrigen)
	checkout(ramaDestino)

	sh """
		git merge ${ramaOrigen}
		git push origin ${ramaDestino}
	"""
}

def tag(String ramaOrigen, String ramaDestino){
	println "Este método realiza un tag en master de ${ramaOrigen}"

	if (ramaOrigen.contains('release-v')){
		checkout(ramaDestino)
		def tagValue = ramaOrigen.split('release-')[1]
		sh """
			git tag ${tagValue}
			git push origin ${tagValue}
		"""

	} else {
		error "La rama ${ramaOrigen} no cumple con nomenclatura definida para rama release (release-v(major)-(minor)-(patch))."
	}
}

def checkout(String rama){
	sh "git reset --hard HEAD; git checkout ${rama}; git pull origin ${rama}"
}*/

def diff(String ramaOrigen, String ramaDestino){
	println "Este método realiza un diff de ${ramaOrigen} y ${ramaDestino}"
	checkout(ramaOrigen)
	checkout(ramaDestino)
    sh "git diff ${ramaOrigen} ${ramaDestino}"
}

def merge(String ramaOrigen, String ramaDestino){
	println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"

	checkout(ramaOrigen)
	checkout(ramaDestino)

    withCredentials([usernamePassword(
      
      credentialsId: 'token_github_jenkins',
      passwordVariable: 'TOKEN',
      usernameVariable: 'USER')]) {

        sh "git merge --verbose ${ramaOrigen}"
        sh 'git push --verbose ' +'https://' + '${TOKEN}'+ '@' + "${GIT_URL}".split('https://')[1] + " ${ramaDestino}"

    }

	
}

def tag(String ramaOrigen, String ramaDestino){
	println "Este método realiza un tag en master de ${ramaOrigen}"

	if (ramaOrigen.contains('release-v')){
		checkout(ramaDestino)
		def tagValue = ramaOrigen.split('release-')[1]

        withCredentials([usernamePassword(
      
            credentialsId: 'token_github_jenkins',
            passwordVariable: 'TOKEN',
            usernameVariable: 'USER')]) {

                sh "git tag ${tagValue}"
                sh 'git push --verbose ' +'https://' + '${TOKEN}'+ '@' + "${GIT_URL}".split('https://')[1] + " ${tagValue}"
        }

	} else {
		error "La rama ${ramaOrigen} no cumple con nomenclatura definida para rama release (release-v(major)-(minor)-(patch))."
	}
}

def checkout(String rama){
    withCredentials([usernamePassword(
      
      credentialsId: 'token_github_jenkins',
      passwordVariable: 'TOKEN',
      usernameVariable: 'USER')]) {

        sh "git reset --hard HEAD"
        sh "git checkout ${rama}"
        sh 'git pull --verbose --no-rebase ' +'https://' + '${TOKEN}'+ '@' + "${GIT_URL}".split('https://')[1] + " ${rama}"
    }
}

def fetchAllTags(){
	println "Este método realiza un fetch de todos los tags"
	withCredentials([usernamePassword(
      
      credentialsId: 'token_github_jenkins',
      passwordVariable: 'TOKEN',
      usernameVariable: 'USER')]) {

        sh "git fetch --all --tags"
    }
}

def getNextVersion(scope) {
	println "Este método retorna la siguiente versión del release."
    
	def latestVersion = sh(returnStdout: true, script: 'git describe --tags `git rev-list --tags --max-count=1` 2> /dev/null || echo 0.0.0').trim()
    def (major, minor, patch) = latestVersion.tokenize('.').collect { it.toInteger() }
    def nextVersion

	fetchAllTags()

    switch (scope) {
        case 'major':
            nextVersion = "${major + 1}.0.0"
            break
        case 'minor':
            nextVersion = "${major}.${minor + 1}.0"
            break
        case 'patch':
            nextVersion = "${major}.${minor}.${patch + 1}"
            break
    }
    return nextVersion
}

return this;