package com.kingdom.controledeestoque.database

import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.api.Context
import com.kingdom.controledeestoque.MainActivity
import com.kingdom.controledeestoque.SQLiteHelper
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import java.net.*
import kotlin.concurrent.thread


class Sync : AppCompatActivity(){

    fun testConnection(): String {


        var message: String
        val host = "192.168.1.11"
        val port = 8080


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {

            val address = InetSocketAddress(host, port);
            val clientSocket = Socket()
            clientSocket.connect(address, 5000)

            // do something with the successfully opened socket
            clientSocket.close()
        } catch (e: ConnectException) {
            // host and port combination not valid
            message = "Falha"
            e.printStackTrace()
            return message
        } catch (e: Exception) { //sem conexão
            message = "Sem Conexão"
            e.printStackTrace()
            return message
        }
        message = "Sucesso"
        return message
    }


    fun sync(cod: Int, ctxt: android.content.Context): String {

            /*0 zero Sincroniza tudo
            1 sincroniza apenas os inserts ao servidor
            9 sem conexão - não tenta sincronizar nada
            || = xor (ou lógico)
            && = e
             */

            var message: String

            var result = testConnection()

            if (result == "Sucesso") {
                if (cod == 0 || cod == 1) {
                    //queryExternalServerAE(ctxt)
                    //queryExternalServerAP(ctxt)
                    if (cod == 1) {
                        message = "Sucesso" //Sincronizado com sucesso
                        return message
                    }
                }
                if (cod == 0) {
                    queryProdutoExt(ctxt)
                    //queryMotivoExt(ctxt)

                    message = "Sucesso" //Sincronizado com sucesso
                    return message
                }
            }
            message = "Falha"
            return message

    }

    fun syncNoReturn(cod: Int, ctxt: android.content.Context) {

        /*0 zero Sincroniza tudo
        1 sincroniza apenas os inserts ao servidor
        9 sem conexão - não tenta sincronizar nada
        || = xor (ou lógico)
        && = e
         */

        var message: String

        var result = testConnection()

        if (result == "Sucesso") {
            if (cod == 0 || cod == 1) {
                //queryExternalServerAE(ctxt)
                //queryExternalServerAP(ctxt)
            }
            if (cod == 0) {
                queryProdutoExt(ctxt)
                //queryMotivoExt(ctxt)

            }
        }

    }

}

class LoadContent: AppCompatActivity(){
    fun loadContent(ctxt: android.content.Context): String{


        var message = Sync().sync(0, this)

        return message
    }
}
