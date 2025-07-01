package com.kingdom.controledeestoque.database

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket


val scope = CoroutineScope(Job() + Dispatchers.Main)

class Sync : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var ctxt = this
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

    suspend fun testConnection(): String {
        var message: String
        val host = "192.168.1.11"
        val port = 8080
        var result = scope.async(Dispatchers.IO) {
            try {
                if (Looper.myLooper() == null) {
                    Looper.prepare()
                }

                // Captura o tempo inicial
                val startTime = System.currentTimeMillis()

                val address = InetSocketAddress(host, port)
                val clientSocket = Socket()
                clientSocket.connect(address, 8000)

                val endTime = System.currentTimeMillis()

                val timeTaken = endTime - startTime
                Log.d("Test Connection time","Tempo necessário para obter resposta: ${timeTaken}ms")

                //clientSocket.connect(address, 2000)
                //clientSocket.soTimeout = 5000

                // do something with the successfully opened socket
                clientSocket.close()
            } catch (e: ConnectException) {
                // host and port combination not valid
                Log.d("Test Connection Error", "Ao tentar conectar o seguinte erro aconteceu: $e /n Possivelmente: host and port combination not valid")
                message = "Falha"
                e.printStackTrace()
                return@async  message
            } catch (e: Exception) { //sem conexão
                message = "Sem Conexão"
                e.printStackTrace()
                Log.d("Test Connection Error", "Ao tentar conectar o seguinte erro aconteceu: $e")
                return@async  message
            }
            message = "Sucesso"
            Log.d("Test Connection Status", "Conectado com sucesso")
            return@async message
        }
        return result.await()
    }


    suspend fun sync(cod: Int, ctxt: android.content.Context): String? {
        /*if (Looper.myLooper() == null) {
            Looper.prepare()
        }*/
        /*0 zero Sincroniza tudo
        1 sincroniza apenas os inserts ao servidor
        9 sem conexão - não tenta sincronizar nada
        || = xor (ou lógico)
        && = e
         */
        var data = scope.async(Dispatchers.IO) {
            var message: String
            var result = testConnection()

            if (result == "Sucesso") {
                return@async withContext(Dispatchers.IO) {
                    if (cod == 0 || cod == 1 || cod == 2) {

                        getDBSRVNotificacao(ctxt)
                        sendDBSRVNotification(ctxt)

                        if (cod == 2) {
                            message = "Sucesso" //Sincronizado com sucesso
                            return@withContext message
                        }
                    }
                    if (cod == 0 || cod == 1) {

                        sendDBSRVMovimento(ctxt)
                        //uploadRequisicoes(ctxt)
                        //uploadUpdRequisicoes(ctxt)

                        if (cod == 1) {
                            message = "Sucesso" //Sincronizado com sucesso
                            return@withContext message
                        }
                    }
                    if (cod == 0) {
                        getProdutoExt(ctxt)
                        getArmz(ctxt)
                        getDBSRVSaldo(ctxt)
                        getDBSRVSlLote(ctxt)
                        //downloadRequisicoes(ctxt)

                        message = "Sucesso" //Sincronizado com sucesso
                        return@withContext message
                    } else {
                        //TODO
                    }

                } as? String
            }
            message = "Falha"
            return@async message
        }
        return data.await()
    }

}


