package com.kingdom.controledeestoque.database

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket


class Sync : AppCompatActivity(), LifecycleEventObserver {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    suspend fun testConnection(): String? {


        //rtn = CoroutineScope(Dispatchers.IO).async(CoroutineName("testConnectionSync")) {
        return withContext(Dispatchers.IO){
            var message: String
            val host = "192.168.1.11"
            val port = 8080
            try {


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
                    return@withContext message
                } catch (e: Exception) { //sem conexão
                    message = "Sem Conexão"
                    e.printStackTrace()
                    return@withContext message
                }
                message = "Sucesso"
                return@withContext message
            }catch (e: Exception){
                return@withContext e.toString()
            }

        } as? String?
        //while (CoroutineScope(Dispatchers.IO).isActive = )
    }


    suspend fun sync(cod: Int, ctxt: android.content.Context): String {

        /*0 zero Sincroniza tudo
        1 sincroniza apenas os inserts ao servidor
        9 sem conexão - não tenta sincronizar nada
        || = xor (ou lógico)
        && = e
         */
        var message: String

        var result = testConnection()

        if (result == "Sucesso") {
            return withContext(Dispatchers.IO) {
                try {
                    if (cod == 0 || cod == 1 || cod == 2) {

                        getNotificacao(ctxt)
                        notificationRead(ctxt)

                        if (cod == 2) {
                            message = "Sucesso" //Sincronizado com sucesso
                            return@withContext message
                        }
                    }
                    if (cod == 0 || cod == 1) {

                        movimentoToServer(ctxt)
                        //uploadRequisicoes(ctxt)
                        uploadUpdRequisicoes(ctxt)

                        if (cod == 1) {
                            message = "Sucesso" //Sincronizado com sucesso
                            return@withContext message
                        }
                    }
                    if (cod == 0) {
                        getProdutoExt(ctxt)
                        getArmz(ctxt)
                        getSaldo(ctxt)
                        getSlLote(ctxt)
                        downloadRequisicoes(ctxt)

                        message = "Sucesso" //Sincronizado com sucesso
                        return@withContext message
                    } else {

                    }
                } catch (e: Exception){
                    Log.d("Sync Exception Error", e.toString())
                    return@withContext e
                }

            } as String
        }
        message = "Falha"
        //}.await() as String
        return message

    }

    suspend fun syncNoReturn(cod: Int, ctxt: android.content.Context) {

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
                getProdutoExt(ctxt)
                getArmz(ctxt)
                getSaldo(ctxt)
                //getSlLote(ctxt)

            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

    }


}


