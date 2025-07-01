package com.kingdom.controledeestoque.database


import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import android.util.Log
import androidx.core.database.getFloatOrNull
import androidx.core.database.getStringOrNull
import com.kingdom.controledeestoque.SQLiteHelper
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
    //var dbExt = "APP_CE_TESTE"
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

    Log.d("Conectado ao banco de dados", "$dbExt")


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

            /*var query = "INSERT INTO Produto (idProduto, codProduto, descProduto, tipoProduto, unidMedida, rastro) " +
                    "VALUES (${resultSet1.getInt("idProduto")}, '${resultSet1.getString("codProduto")}', '${resultSet1.getString("descProduto")}', " +
                    "'${resultSet1.getString("tipoProduto")}', '${resultSet1.getString("unidMedida")}', '${resultSet1.getString("rastro")}')"
            dbIntrn.externalExecSQL(query)*/

            dbIntrn.insertProduto(resultSet1.getInt("idProduto"), resultSet1.getString("codProduto"), resultSet1.getString("descProduto"),
                resultSet1.getString("tipoProduto"), resultSet1.getString("unidMedida"), resultSet1.getString("rastro"))

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
            /*var query = "INSERT INTO $tbl ($id, $cod, $desc) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$cod")}', '${resultSet1.getString("$desc")}') "
            dbIntrn.externalExecSQL(query)*/

            dbIntrn.insertArmz(resultSet1.getInt("$id"),resultSet1.getString("$cod"),resultSet1.getString("$desc"))

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

fun getDBSRVSaldo(context: Context?) {

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
            /*var query = "INSERT INTO $tbl ($id, $codP, $codArmz, $saldo) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$codP")}', '${resultSet1.getString("$codArmz")}'," +
                    "'${resultSet1.getFloat("$saldo")}') "
            dbIntrn.externalExecSQL(query)*/

            dbIntrn.insertSaldo(resultSet1.getInt("$id"), resultSet1.getString("$codP"), resultSet1.getString("$codArmz"), resultSet1.getFloat("$saldo"))

            Log.d("SQL Insert Saldo", "${resultSet1.getString("$codArmz")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getDBSRVSlLote(context: Context?) {

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
            /*var query = "INSERT INTO $tbl ($id, $codP, $codArmz, $lote, $vldLote, $saldo) " +
                    "VALUES (${resultSet1.getInt("$id")}, '${resultSet1.getString("$codP")}', '${resultSet1.getString("$codArmz")}'," +
                    "'${resultSet1.getString("$lote")}','${resultSet1.getString("$vldLote")}','${resultSet1.getFloat("$saldo")}') "
            dbIntrn.externalExecSQL(query)*/

            dbIntrn.insertSaldoLote(resultSet1.getInt("$id"), resultSet1.getString("$codP"), resultSet1.getString("$codArmz"),
                resultSet1.getString("$lote"),resultSet1.getString("$vldLote"),resultSet1.getFloat("$saldo"))

            Log.d("SQL Insert SlLote", "${resultSet1.getString("$lote")} inserido com sucesso (${resultSet1.getInt("$id")})")
        }

        resultSet1.close()
        st1.close()
        //dbIntrn.close()

    }
}

fun getDBSRVNotificacao(context: Context?) {

    var count = 0

    val tbl = "Notificacao"
    val id = "idNotificacao"
    val msg = "mensagem"
    val data = "dataHora"
    val dados = "dadosLancamento"
    val user = "username"
    val lido = "lido"

    val dbIntrn: SQLiteHelper = SQLiteHelper(context)

    var lastID = dbIntrn.getLastNotificationId()

    connect().use {
        val st1 = it?.createStatement()!!

        val resultSet1: ResultSet
        if (lastID == null) {

            resultSet1 = st1.executeQuery(
                """
            SELECT *
              FROM $tbl
             ORDER BY $id
            """.trimIndent()
            )
        } else {
            resultSet1 = st1.executeQuery(
                """
             SELECT * 
             FROM $tbl 
             WHERE $id > $lastID 
             ORDER BY $id
            """.trimIndent()
            )
        }

        var array = dbIntrn.arrayIdNotf()
        //dbIntrn.externalExecSQL("DELETE FROM $tbl")
        while (resultSet1.next()){

            if (array != null) {
                if (!array.contains(resultSet1.getInt("$id"))){
                    dbIntrn.insertNotificacao(resultSet1.getInt("$id"), resultSet1.getString("$msg"), resultSet1.getString("$data"),
                        resultSet1.getString("$dados"), resultSet1.getString("$user"), resultSet1.getString("$lido"))
                    Log.d("SQL Download Notificacao", "${resultSet1.getString("$msg")} inserido (${resultSet1.getInt("$id")})")
                }
            } else {
                dbIntrn.insertNotificacao(resultSet1.getInt("$id"), resultSet1.getString("$msg"), resultSet1.getString("$data"),
                    resultSet1.getString("$dados"), resultSet1.getString("$user"), resultSet1.getString("$lido"))
                Log.d("SQL Download Notificacao", "${resultSet1.getString("$msg")} inserido com sucesso (${resultSet1.getInt("$id")})")
            }
        }
        //dbIntrn.close()

        resultSet1.close()
        st1.close()

    }
    connect()?.close()
}

fun sendDBSRVMovimento(context: Context) {
    var dbIntrn: SQLiteHelper = SQLiteHelper(context)
    var result = dbIntrn.getInternalMovimento()
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
                        INSERT INTO Movimento (armazemOrigem, codProduto, lote, qtdMovimento, 
                        armazemDestino, dataHora, username) VALUES ('${localResult.getString(1)}', 
                        '${localResult.getString(2)}', '${localResult.getString(3)}', 
                                    ${localResult.getFloat(4)}, '${localResult.getString(5)}', '${localResult.getString(6)}', '${localResult.getString(7)}')
                        """.trimIndent())
                    Log.d("Debugggggg", insert)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()
                    //comm.connection.commit()

                    dbIntrn.updateMovimento(id, 1)
                    //dbIntrn.insertDone("Movimento", id)

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

fun sendDBSRVNotification(context: Context) {
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
                    //Log.d("Update notification as read", insert)

                    dbIntrn.updateNotificacaoFinal(localResult.getInt(0),1)

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

fun uploadRequisicoes(context: Context) {
    var dbIntrn: SQLiteHelper = SQLiteHelper(context)

    var result = dbIntrn.getReq()
    var resultUpd = dbIntrn.getUpdReq()
    var localResult = result
    var localResultUpd = resultUpd

    connect().use {

        var st1 = it?.createStatement()!!
        if (localResult != null && localResult.getCount() > 0) {
            localResult.moveToFirst()
            do {

                var id = localResult?.getInt(0)

                //var produto = dbIntrn.getDescProdutos(localResult.getInt(5))
                //var motivo = dbIntrn.getDescMotivo(localResult.getInt(6))
                //var produtoDesc = produto!!.getString(1)
                //var motivoDesc = motivo!!.getString(1)
                Log.d("upload Req", "$id")
                try {


                    lateinit var userAtend: String

                    fun contentOrNull(any: Any?): Any? {
                        if (any == null){
                            return null
                        } else /*if (any == String && any != null)*/{
                            var anyS = "'" + "$any" + "'"
                            Log.d("UpReq AnyS test", anyS)
                            return anyS
                        }
                    }

                    var insert = (
                            """
                            INSERT INTO Requisicao
                            (codProduto, qtdRequisicao, qtdAtendida, qtdConfirmacao, userRequisicao, 
                            userAtendimento, userConfirmacao, dataHoraRequisicao, dataHoraAtendimento, dataHoraConfirmacao)
                            VALUES
                            ('${localResult.getString(1)}', ${localResult.getFloat(2)}, 
                            ${localResult.getFloatOrNull(3)},
                            ${localResult.getFloatOrNull(4)},
                            '${localResult.getString(5)}',
                            ${contentOrNull(localResult.getStringOrNull(6))},
                            ${contentOrNull(localResult.getStringOrNull(7))},
                            ${contentOrNull(localResult.getStringOrNull(8))},
                            ${contentOrNull(localResult.getStringOrNull(9))},
                            ${contentOrNull(localResult.getStringOrNull(10))});
                            """.trimIndent())
                    Log.d("Upload Requisicao", insert)

                    var query = """
                        UPDATE Requisicao 
                        SET statusSync = 1
                        WHERE idRequisicao = ${localResult.getInt(0)}
                    """.trimIndent()
                    dbIntrn.externalExecSQL(query)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()


                    //comm.connection.commit()
                } catch (e: ClassNotFoundException){
                    Log.e("Error SQL CNFE", e.toString())
                }
                catch (se: SQLException){
                    Log.e("Error SQLE", se.toString())
                }
                dbIntrn.insertDone("Requisicao", id)

                //result.moveToNext()
            }while (localResult.moveToNext())


        } else {
            Log.d("uploadRequisicoes", "Erro")

        }
        dbIntrn.close()
        st1.close()
        connect()?.close()
    }
}

fun uploadUpdRequisicoes(context: Context) {
    var dbIntrn: SQLiteHelper = SQLiteHelper(context)

    var result = dbIntrn.getReq()
    var resultUpd = dbIntrn.getUpdReq()
    var localResult = result
    var localResultUpd = resultUpd

    connect().use {

        var st1 = it?.createStatement()!!
        if (localResultUpd != null && localResultUpd.getCount() > 0) {
            localResultUpd.moveToFirst()
            do {

                var id = localResultUpd?.getInt(0)

                //var produto = dbIntrn.getDescProdutos(localResult.getInt(5))
                //var motivo = dbIntrn.getDescMotivo(localResult.getInt(6))
                //var produtoDesc = produto!!.getString(1)
                //var motivoDesc = motivo!!.getString(1)
                Log.d("upload ReqUpd", "$id")
                try {


                    lateinit var userAtend: String

                    fun contentOrNull(any: Any?): Any? {
                        if (any == null){
                            return null
                        } else /*if (any == String && any != null)*/{
                            var anyS = "'" + "$any" + "'"
                            Log.d("UpReq AnyS test", anyS)
                            return anyS
                        }
                    }

                    var insert = (
                            """
                            UPDATE Requisicao 
                            SET codProduto = '${localResultUpd.getString(1)}', 
                            qtdRequisicao = ${localResultUpd.getFloat(2)},
                            qtdAtendida = ${localResultUpd.getFloatOrNull(3)}, 
                            qtdConfirmacao = ${localResultUpd.getFloatOrNull(4)}, 
                            userRequisicao = '${localResultUpd.getString(5)}', 
                            userAtendimento = ${contentOrNull(localResultUpd.getStringOrNull(6))}, 
                            userConfirmacao = ${contentOrNull(localResultUpd.getStringOrNull(7))}, 
                            dataHoraRequisicao = ${contentOrNull(localResultUpd.getStringOrNull(8))}, 
                            dataHoraAtendimento = ${contentOrNull(localResultUpd.getStringOrNull(9))}, 
                            dataHoraConfirmacao = ${contentOrNull(localResultUpd.getStringOrNull(10))}
                            WHERE codProduto = '${localResultUpd.getString(1)}' AND  
                            qtdRequisicao = ${localResultUpd.getFloat(2)} AND 
                            userRequisicao = '${localResultUpd.getString(5)}' AND
                            dataHoraRequisicao = ${contentOrNull(localResultUpd.getStringOrNull(8))};
                            
                            """.trimIndent())
                    Log.d("Upload RequisicaoUpd", insert)

                    var query = """
                        UPDATE Requisicao 
                        SET statusSync = 1
                        WHERE idRequisicao = ${localResultUpd.getInt(0)}
                    """.trimIndent()
                    dbIntrn.externalExecSQL(query)

                    var comm = st1.connection.prepareStatement(insert)
                    comm.executeUpdate()


                    //comm.connection.commit()
                } catch (e: ClassNotFoundException){
                    Log.e("Error SQL CNFE", e.toString())
                }
                catch (se: SQLException){
                    Log.e("Error SQLE", se.toString())
                }
                dbIntrn.insertDone("Requisicao", id)

                //result.moveToNext()
            }while (localResultUpd.moveToNext())


        } else {
            Log.d("uploadRequisicoes Upd ", "Erro")

        }
        dbIntrn.close()
        st1.close()
        connect()?.close()
    }
}



fun contentOrNullStr(any: Any): Any? {
    if (any == null) {
        Log.d("DwReq AnyS test", "null")
        return null
    } else {
        var anyS = "'${any.toString()}'"
        Log.d("DwReq AnyS test", anyS)
        return anyS
    }
}
fun contentOrNullFloat(any: Float?): Any? {
    if (any == null) {
        Log.d("DwReq AnyF test", "null")
        return null
    } else if (any?.toDouble() != 0.0) {
        var anyS = any
        Log.d("DwReq AnyF test", "$anyS")
        return anyS
    } else {
        return null
    }
}

var userAType: String? = null

fun downloadRequisicoes(context: Context?) {

    /*val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)*/

    val tbl = "Requisicao"
    val id = "idRequisicao"
    val cod = "codProduto"
    val qtdR = "qtdRequisicao"
    val qtdA = "qtdAtendida"
    val qtdC = "qtdConfirmacao"
    val userR = "userRequisicao"
    val userA = "userAtendimento"
    val userC = "userConfirmacao"
    val dataR = "dataHoraRequisicao"
    val dataA = "dataHoraAtendimento"
    val dataC = "dataHoraConfirmacao"
    //val lido = "lido"

    var dbIntrn: SQLiteHelper = SQLiteHelper(context)
    connect().use {
        var st1 = it?.createStatement()!!
        var resultSet1 = st1.executeQuery(
            """
            SELECT *
              FROM Requisicao
             ORDER BY idRequisicao
            """.trimIndent()
        )
        dbIntrn.externalExecSQL("DELETE FROM Requisicao")
        Log.d("Table $tbl", "Table $tbl deleted")
        while (resultSet1.next()){

            fun checkNullorString(origin: String?): Any? {
                if (resultSet1.getObject(origin) == null) {
                    userAType = null
                    return userAType
                } else {
                    userAType = resultSet1.getString(origin)
                    return userAType
                }
            }
            var query =
                "INSERT INTO $tbl ($id, $cod, $qtdR, $qtdA, $qtdC, $userR, $userA, $userC, $dataR, $dataA, $dataC) " +
                        "VALUES (${resultSet1.getInt("$id")}, " +
                        "'${resultSet1.getString("$cod")}', " +
                        "${contentOrNullFloat(resultSet1.getFloat("$qtdR"))}," +
                        "${contentOrNullFloat(resultSet1.getFloat("$qtdA"))}," +
                        "${contentOrNullFloat(resultSet1.getFloat("$qtdC"))}," +
                        "${checkNullorString(userR)?.let { it1 -> contentOrNullStr(it1) }}," +
                        "${checkNullorString(userA)?.let { it1 -> contentOrNullStr(it1) }}," +
                        "${checkNullorString(userC)?.let { it1 -> contentOrNullStr(it1) }}, " +
                        "${checkNullorString(dataR)?.let { it1 -> contentOrNullStr(it1) }}," +
                        "${checkNullorString(dataA)?.let { it1 -> contentOrNullStr(it1) }}," +
                        "${checkNullorString(dataC)?.let { it1 -> contentOrNullStr(it1) }}) "
            dbIntrn.externalExecSQL(query)
            Log.d(
                "SQL Download Requisicao",
                "${resultSet1.getString("$cod")} inserido com sucesso (${
                    resultSet1.getInt("$id")
                })"
            )
        }

        dbIntrn.close()
        st1.close()
        connect()?.close()

    }

}

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
        var message = "${response.body()}"
        rtn = response.code()
        Log.d("Debug UserConnection", "${response.body()?.string()}")
        Log.d("Debug UserConnection", "${response.code()}")


        //var body = response.body()?.string()
        //var token = JSONObject(body).getJSONObject("access_token")
        //rtn = if (token.length()>0) 1 else 0



        if (rtn == 202) {
            if (message != "" && message != null) {
                    return 202 //código que informa erro e que é obrigatório troca de senha

            }
        }

        return rtn


    } else {
        println("Request failed: ${response.code()}")
        Log.d("Request failed", "Request failed: ${response.code()}")
        rtn = response.code()
        return rtn
    }
}





    //==========================================================================






