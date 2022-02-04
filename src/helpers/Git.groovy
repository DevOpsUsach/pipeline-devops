package helpers

def merge(String ramaOrigen, String ramaDestino){
	println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"
	
	checkout(ramaOrigen)
	checkout(ramaDestino)

	bat """
		git merge ${ramaOrigen}
		git push origin ${ramaDestino}
	"""

}

def tag(String ramaOrigen){
	println "Este método realiza un tag ${ramaOrigen}"

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
	println "Checkout: ${rama}"

	//bat "git reset --hard HEAD"
	bat "git checkout ${rama}"
	bat "git pull origin ${rama}"
}

return this;
