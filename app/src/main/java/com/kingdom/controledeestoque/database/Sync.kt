package com.kingdom.controledeestoque.database

import android.app.Application
import android.os.Bundle
import android.os.PersistableBundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.api.Context
import com.kingdom.controledeestoque.MainActivity
import com.kingdom.controledeestoque.SQLiteHelper
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.net.*
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext


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


    suspend fun sync(cod: Int, ctxt: android.content.Context): String? {

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
                        //queryExternalServerAE(ctxt)
                        //queryExternalServerAP(ctxt)
                        if (cod == 2) {
                            message = "Sucesso" //Sincronizado com sucesso
                            return@withContext message
                        }
                    }
                    if (cod == 0 || cod == 1) {

                        movimentoToServer(ctxt)
                        //queryExternalServerAE(ctxt)
                        //queryExternalServerAP(ctxt)
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
        return result

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
    /*@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun appInResumeState() {
        Toast.makeText(this,"In Foreground", Toast.LENGTH_LONG).show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appInStopState() {
        Toast.makeText(this,"Stopped", Toast.LENGTH_LONG).show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun appInPauseState() {
        Toast.makeText(this,"In Background", Toast.LENGTH_LONG).show();
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        TODO("Not yet implemented")
    }*/

}


/*class LoadContent: AppCompatActivity(){
    suspend fun loadContent(ctxt: android.content.Context): String{


        var message : String = CoroutineScope(Dispatchers.Unconfined).run {
            var str = Sync().sync(0, ctxt)
        }


        return message
    }
}*/