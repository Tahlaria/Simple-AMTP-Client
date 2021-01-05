package de.aiarena.community

data class MessageFromServer(val code: Int, val headers: HashMap<String,String> = HashMap()){
    override fun toString(): String {
        return "$code\n" + headers.entries.map{it.key+": "+it.value}.joinToString("\n")
    }
}
data class MessageToServer(val cmd: String, val protocolVersion: String, val headers: HashMap<String,String> = HashMap()){
    override fun toString(): String {
        return "$protocolVersion $cmd\n" + headers.entries.map{it.key+": "+it.value}.joinToString("\n")
    }
}
