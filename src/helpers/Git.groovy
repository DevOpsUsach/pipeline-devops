package helpers

def merge(String ramaOrigen, String ramaDestino){
	println "Este método realiza un merge ${ramaOrigen} y ${ramaDestino}"
	
	checkout(ramaOrigen)
	checkout(ramaDestino)

}

def tag(String ramaOrigen){
	println "Este método realiza un tag ${ramaOrigen}"

}

def checkout(String rama){
	bat "git reset --hard HEAD; git checkout ${rama}; git pull origin ${rama}"
}

return this;
