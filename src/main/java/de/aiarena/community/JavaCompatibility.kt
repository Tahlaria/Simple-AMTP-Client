package de.aiarena.community

interface JavaCompatibleBroadcastCallback{
    fun onMessage(msg: MessageFromServer, myTurn: Boolean)
}
interface JavaCompatibleMessageCallback{
    fun onResponse(msg: MessageFromServer)
}
