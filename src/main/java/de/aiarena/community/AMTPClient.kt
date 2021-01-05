package de.aiarena.community

import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap

class AMTPClient(host: String, port: Int, secret: String, private val broadcastCallback: (MessageFromServer) -> Unit): Closeable, Runnable{
    private val socket = Socket(host,port)
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = PrintWriter(socket.getOutputStream())

    init{
        Thread(this).start()
    }

    override fun run() {
        var currentMessage: MessageFromServer? = null

        while(true){
            try{
                val line = reader.readLine()
                        ?: break

                if(line == ""){
                    currentMessage?.let{
                        onMessageCompletion(it)
                    }
                    currentMessage = null
                    continue
                }

                when(currentMessage){
                    null -> {
                        currentMessage = MessageFromServer(line.toInt())
                    }
                    else -> {
                        currentMessage.headers[line.substring(0,line.indexOf(":"))] = line.substring(line.indexOf(":")+1,line.length)
                    }
                }

            }catch(ex : Exception){
                System.err.println("Exception: $ex")
                ex.printStackTrace()
            }
        }
    }

    private fun onMessageCompletion(msg: MessageFromServer){
        if(msg.code == 9){
            broadcastCallback(msg)
            return
        }
        msg.headers["Identifier"]?.let{
            id ->
            pendingCallbacks[id]
                    ?.let{
                        cb -> cb(msg)
                        pendingCallbacks.remove(id)
                        return
                    }
        }

        println("Warning: Ignored message:")
        println(msg)
    }

    private val pendingCallbacks = HashMap<String,(MessageFromServer) -> Unit>()
    private fun send(message: MessageToServer, callback: ((MessageFromServer) -> Unit)? = null){
        val key = UUID.randomUUID().toString()
        callback?.let{
            message.headers["Identifier"] = key
            pendingCallbacks[key] = it
        }
        writer.write(message.toString())
        writer.flush()
    }

    override fun close() {
        reader.close()
        writer.close()
        socket.close()
    }
}