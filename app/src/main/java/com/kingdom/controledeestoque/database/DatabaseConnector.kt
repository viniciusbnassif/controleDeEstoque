package com.kingdom.controledeestoque.database


import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import android.util.Log
import com.kingdom.controledeestoque.SQLiteHelper
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.Closeable
import java.net.ConnectException
import java.net.Socket
import java.sql.*
import java.sql.Connection

//import java.sql.SQLException;

//import com.liderMinas.PCP.database

class Connection(private val coreConnection: java.sql.Connection) :
    java.sql.Connection by coreConnection, Closeable {

    var ip = "192.168.1.10:1433" //local server running MSSQL Server [Porta 1433]
    var dbExt = "APP_CE"
    var username = "APP_CE"
    var password = "app@ce"
    val Classes = "net.sourceforge.jtds.jdbc.Driver"
    var allProducts = mutableListOf<String>()
    var url = "jdbc:jtds:sqlserver://"+ip+"/"+dbExt

    lateinit var stmt: Statement
    //lateinit var connection: Connection

    lateinit var produtos: Array<String>



    override fun close() {
        coreConnection.close()
    }

    override fun createStatement(): Statement {
        return coreConnection.createStatement()
    }

    /*fun queryToServer(){
        var c = connectMSSQL();
        var con = c.startConn()
        var query = "INSERT INTO TableName(ColumnName) VALUES ('+text+') ";
        stmt = con.createStatement()
        var set: ResultSet = stmt.execute(query)

        while (set.next()){
            produtos.setText(set.getString(2))
        }
        con.close()

    }*/
}


fun connect(): java.sql.Connection? {
    var ip = "192.168.1.10:1433" //local server running MSSQL Server [Porta 1433]
    var dbExt = "APP_CE"
    var user = "APP_CE"
    var password = "app@ce"
    val Classes = "net.sourceforge.jtds.jdbc.Driver"
    var url = "jdbc:jtds:sqlserver://" + ip + "/" + dbExt

    //val c: Connection


    val policy = Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    Class.forName(Classes)
    lateinit var c : Connection
    try {
        c = DriverManager.getConnection(url, user, password)// <---------
        return Connection(c)
        Log.d("Debug", "Connected")
    } catch (e: ClassNotFoundException){
        e.printStackTrace();
        Log.d("Connection State:", "${e}, ERRO Class")
        return null
    }catch (e: SQLException) {
        e.printStackTrace();
        Log.d("Connection State:", "${e}, ERRO SQL")
        return null
    }
    return null
}

fun main() {
    //queryProdutoExt()
}

fun getProdutoExt(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    var count = 0

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM Produto
             ORDER BY idProduto
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM Produto")
        while (resultSet1.next()){

            var query = "INSERT INTO Produto (idProduto, codProduto, descProduto, tipoProduto, unidMedida, rastro) " +
                    "VALUES (${resultSet1.getInt("idProduto")}, '${resultSet1.getString("codProduto")}', '${resultSet1.getString("descProduto")}', " +
                    "'${resultSet1.getString("tipoProduto")}', '${resultSet1.getString("unidMedida")}', '${resultSet1.getString("rastro")}')"
            dbIntrn.externalExecSQL(query)
            Log.d("SQL Insert", "${resultSet1.getString("descProduto")} inserido com sucesso (${resultSet1.getInt("idProduto")})")
        }

        resultSet1.close()
        st1.close()

        //return arrayOf(arrayProdutoIdExt, arrayProdutoDescExt,arrayProdutoQeExt, arrayProdutoValExt, arrayProdutoTipoExt)

    }
}
fun getArmz(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    var count = 0

    var tbl = "Armazem"
    var id = "idArmazem"
    var cod = "codArmazem"
    var desc = "descArmazem"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM $tbl
             ORDER BY idArmazem
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){
            count += 1
            var query = "INSERT INTO $tbl ($id, $cod, $desc) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$cod")}', '${resultSet1.getString("$desc")}') "
            dbIntrn.externalExecSQL(query)
            Log.d("SQL Insert Armz", "${resultSet1.getString("$desc")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getArmzTest(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    var count = 0

    var tbl = "Armazem"
    var id = "idArmazem"
    var cod = "codArmazem"
    var desc = "descArmazem"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM $tbl
             ORDER BY idArmazem
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){
            count += 1
            var query = "INSERT INTO $tbl ($id, $cod, $desc) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$cod")}', '${resultSet1.getString("$desc")}') "
            dbIntrn.externalExecSQL(query)
            Log.d("SQL Insert Armz", "${resultSet1.getString("$desc")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getSaldo(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    var count = 0

    val tbl = "Saldo"
    val id = "idSaldo"
    val codP = "codProduto"
    val codArmz = "codArmazem"
    val saldo = "saldo"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM $tbl
             ORDER BY $id
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){
            count += 1
            var query = "INSERT INTO $tbl ($id, $codP, $codArmz, $saldo) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$codP")}', '${resultSet1.getString("$codArmz")}'," +
                    "'${resultSet1.getFloat("$saldo")}') "
            dbIntrn.externalExecSQL(query)
            Log.d("SQL Insert Saldo", "${resultSet1.getString("$codArmz")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getSlLote(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    var count = 0

    val tbl = "SaldoLote"
    val id = "idSaldoLote"
    val codP = "codProduto"
    val codArmz = "codArmazem"
    val lote = "lote"
    val vldLote = "validLote"
    val saldo = "saldoLote"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM $tbl
             ORDER BY $id
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){
            count += 1
            var query = "INSERT INTO $tbl ($id, $codP, $codArmz, $lote, $vldLote, $saldo) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$codP")}', '${resultSet1.getString("$codArmz")}'," +
                    "'${resultSet1.getString("$lote")}','${resultSet1.getString("$vldLote")}','${resultSet1.getFloat("$saldo")}') "
            dbIntrn.externalExecSQL(query)
            Log.d("SQL Insert SlLote", "${resultSet1.getString("$lote")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getNotificacao(context: Context?) {

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    var count = 0

    val tbl = "Notificacao"
    val id = "idNotificacao"
    val msg = "mensagem"
    val data = "dataHora"
    val dados = "dadosLancamento"
    val user = "username"
    val lido = "lido"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        val st1 = it?.createStatement()!!
        val resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM $tbl
             ORDER BY $id
            """.trimIndent()
        )

        var array = dbIntrn.arrayIdNotf()
        //dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){

            if (array != null) {
                if (!array.contains(resultSet1.getInt("$id"))){
                    var query = "INSERT INTO $tbl ($id, $msg, $data, $dados, $user, $lido) " +
                            "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$msg")}', '${resultSet1.getString("$data")}'," +
                            "'${resultSet1.getString("$dados")}','${resultSet1.getString("$user")}','${resultSet1.getString("$lido")}') "
                    dbIntrn.externalExecSQL(query)
                    Log.d("SQL Insert Notificacao", "${resultSet1.getString("$msg")} inserido com sucesso (${resultSet1.getInt("$id")})")

                }
            } else {
                var query = "INSERT INTO $tbl ($id, $msg, $data, $dados, $user, $lido) " +
                        "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$msg")}', '${resultSet1.getString("$data")}'," +
                        "'${resultSet1.getString("$dados")}','${resultSet1.getString("$user")}','${resultSet1.getString("$lido")}') "
                dbIntrn.externalExecSQL(query)
                Log.d("SQL Insert Notificacao", "${resultSet1.getString("$msg")} inserido com sucesso (${resultSet1.getInt("$id")})")
            }
        }
        //dbIntrn.close()

        resultSet1.close()
        st1.close()

    }
    connect()?.close()
}

fun movimentoToServer(context: Context) {
    var dbIntrn: SQLiteHelper = SQLiteHelper(context)

    var result = dbIntrn.getInternalMovimento()
    var localResult = result

    connect().use {

        var st1 = it?.createStatement()!!
        if (localResult != null && localResult.getCount() > 0) {
            localResult.moveToFirst()
            do {
                var id = localResult.getInt(0)

                //var produto = dbIntrn.getDescProdutos(localResult.getInt(9))
                //var produtoDesc = produto!!.getString(1)
                //Log.d("ProdDesc", "$produtoDesc")
                try {

                    var insert = (
                        """
                        INSERT INTO Movimento (armazemOrigem, codProduto, lote, qtdMovimento, armazemDestino, dataHora, username) VALUES ('${localResult.getString(1)}', '${localResult.getString(2)}', '${localResult.getString(3)}', 
                                    ${localResult.getFloat(4)}, '${localResult.getString(5)}', '${localResult.getString(6)}', '${localResult.getString(7)}')
                        """.trimIndent())
                    Log.d("Debugggggg", insert)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()
                    //comm.connection.commit()
                    dbIntrn.insertDone(id)

                } catch (e: ClassNotFoundException){
                    Log.e("Error SQL CNFE", e.toString())
                }
                catch (se: SQLException){
                    Log.e("Error SQLE", se.toString())
                }


                //result.moveToNext()
            }while (localResult.moveToNext())


        }
        if (result != null) {
            result.close()
        }
        if (localResult != null) {
            localResult.close()
        }
        st1.close()
        connect()?.close()
    }
}

fun notificationRead(context: Context) {
    var dbIntrn = SQLiteHelper(context)

    var result = dbIntrn.getNotificationRead()
    var localResult = result

    connect().use {

        var st1 = it?.createStatement()!!
        if (localResult != null && localResult.getCount() > 0) {
            localResult.moveToFirst()
            do {
                var id = localResult.getInt(0)
                try {
                    var insert = (
                        """
                        UPDATE Notificacao SET lido = 'S' WHERE idNotificacao = ${localResult.getInt(0)}
                        """.trimIndent())
                    Log.d("Update notification as read", insert)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()
                    comm.close()
                } catch (e: ClassNotFoundException){
                    Log.e("Error SQL CNFE", e.toString())
                }
                catch (se: SQLException){
                    Log.e("Error SQLE", se.toString())
                }
            }while (localResult.moveToNext())
        }
        if (result != null) {
            result.close()
        }
        if (localResult != null) {
            localResult.close()
        }
        st1.close()
        dbIntrn.close()
    }
    connect()?.close()
}

/*fun queryExternalServerAP(context: Context) {
    var dbIntrn: SQLiteHelper = SQLiteHelper(context)

    var result = dbIntrn.getAP()
    var localResult = result


    connect().use {

        var st1 = it?.createStatement()!!
        if (localResult != null && localResult.getCount() > 0) {
            localResult.moveToFirst()
            do {

                var id = localResult?.getInt(0)

                var produto = dbIntrn.getDescProdutos(localResult.getInt(5))
                var motivo = dbIntrn.getDescMotivo(localResult.getInt(6))
                var produtoDesc = produto!!.getString(1)
                var motivoDesc = motivo!!.getString(1)
                Log.d("ProdDesc", "$produtoDesc")
                try {

                    var insert = (
                            """
                            INSERT INTO ApontPerda 
                            (qtdPerda, unidPerda, dataHoraPerda, username, produto, motivo)
                            VALUES
                            (${localResult.getFloat(1)}, '${localResult.getString(2)}', '${localResult.getString(3)}', '${localResult.getString(4)}',
                             '${produtoDesc}', '${motivoDesc}');
                            """.trimIndent())
                    Log.d("Debugggggg", insert)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()


                    //comm.connection.commit()
                } catch (e: ClassNotFoundException){
                    Log.e("Error SQL CNFE", e.toString())
                }
                catch (se: SQLException){
                    Log.e("Error SQLE", se.toString())
                }
                dbIntrn.insertDone("ApontPerda", id)

                //result.moveToNext()
            }while (localResult.moveToNext())


        } else {
            Log.d("Debug", "Erro ;-;")

        }
        st1.close()
        connect()?.close()
    }
}*/

fun confirmUnPw(username: String, password: String): Int {
    // Create an OkHttpClient object


    val policy = Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)




    val client = OkHttpClient()

// Create a RequestBody object with your data
    val requestBody = FormBody.Builder()
        .add("grant_type", "password")
        .build()

// Create a Request object with your URL, headers and body
    val request = Request.Builder()
        .url("http://192.168.1.11:8080/rest/api/oauth2/v1/token")
        .addHeader("password", password)
        .addHeader("username", username)
        .post(requestBody)
        .build()

// Execute the request and get a Response object

    var rtn: Int


    val host = "192.168.1.11"
    val port = 8080

    try {
        val clientSocket = Socket(host, port)

        // do something with the successfully opened socket
        clientSocket.close()
    } catch (e: ConnectException) {
        rtn = 900
        // host and port combination not valid
        e.printStackTrace()
        return rtn
    } catch (e: Exception) { //sem conexão
        rtn = 901
        e.printStackTrace()
        return rtn
    }

    val response = client.newCall(request).execute()


// Check if the response was successful and print the body
    if (response.isSuccessful) {
        Log.d("Debug UserConnection", "${response.body()?.string()}")
        //var body = response.body()?.string()
        //var token = JSONObject(body).getJSONObject("access_token")
        //rtn = if (token.length()>0) 1 else 0
        rtn = response.code()
        return rtn
    } else {
        println("Request failed: ${response.code()}")
        Log.d("Request failed", "Request failed: ${response.code()}")
        rtn = response.code()
        return rtn
    }
}




    //==========================================================================






