package com.kingdom.controledeestoque

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.kingdom.controledeestoque.database.scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//import com.liderMinas.PCP.database.connectMSSQL

/*import android.database.sqlite.SQLiteQuery*/


class SQLiteHelper(context: Context?):

    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    //var dbConnectExt = queryProdutoExt()
    //var connector = connectMSSQL()
    private val db: SQLiteDatabase = this.writableDatabase
    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ce.db"


        private const val TBL_USUARIO = "Usuario"
        private const val ID_USUARIO = "idUsuario"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"

        /*========================================================================*/

        const val TBL_PRODUTO = "Produto"
        private const val ID_PRODUTO = "idProduto"
        private const val COD_PROD = "codProduto"
        private const val DESC_PROD = "descProduto"
        private const val TIPO_PROD = "tipoProduto"
        private const val UNID_PROD = "unidMedida"
        private const val RASTRO_PROD = "rastro"

        /*========================================================================*/

        private const val TBL_ARMZ = "Armazem"
        private const val ID_ARMZ = "idArmazem"
        private const val COD_ARMZ = "codArmazem"
        private const val DESC_ARMZ = "descArmazem"

        /*========================================================================*/

        private const val TBL_SALDO = "Saldo"
        private const val ID_SALDO = "idSaldo"
        private const val SALDO = "saldo"

        /*========================================================================*/

        private const val TBL_SALDOLOTE = "SaldoLote"
        private const val ID_SL = "idSaldoLote"
        private const val LOTE = "lote"
        private const val VALID_LOTE = "validLote"
        private const val SALDO_LOTE = "saldoLote"

        /*========================================================================*/

        private const val TBL_MOVIM = "Movimento"
        private const val ID_MOVIM = "idMovimento"
        private const val ARMZ_ORIG = "armazemOrigem"
        private const val QTD_MOVIM = "qtdMovimento"
        private const val ARMZ_DEST = "armazemDestino"
        private const val DATAHORA = "dataHora"
        private const val STATUS_SYNC = "statusSync"

        /*========================================================================*/

        private const val TBL_NOTIF = "Notificacao"
        private const val ID_NOTIF = "idNotificacao"
        private const val MENSG = "mensagem"
        private const val DADOS_LCM = "dadosLancamento"
        private const val LIDO = "lido"
        ///        private const val STATUS_SYNC = "statusSync"

        /*========================================================================*/


        private const val TBL_PROD_EST = "ProdutoEstoque"
        private const val ID_PROD_EST = "idProduto"
        private const val COD_PROD_EST = "codProduto"
        private const val DESC_PROD_EST = "descProduto"
        private const val TIPO_PROD_EST = "tipoProduto"
        private const val UNID_PROD_EST = "unidMedida"
        private const val RASTRO_PROD_EST = "rastro"

        /*========================================================================*/

        private const val TBL_REQUISICAO = "Requisicao"
        private const val ID_REQUISICAO = "idRequisicao"
        private const val COD_PROD_REQ = "codProduto"
        private const val QTD_REQ = "qtdRequisicao"
        private const val QTD_ATEND = "qtdAtendida"
        private const val QTD_CONF = "qtdConfirmacao"
        private const val USER_REQ = "userRequisicao"
        private const val USER_ATEND = "userAtendimento"
        private const val USER_CONF = "userConfirmacao"
        private const val DATA_REQ = "dataHoraRequisicao"
        private const val DATA_ATEND = "dataHoraAtendimento"
        private const val DATA_CONF = "dataHoraConfirmacao"
        /*private const val STATUS_SYNC = "statusSync"*/

        /*========================================================================*/

        val createTBLUSUARIO = (
                "CREATE TABLE " + TBL_USUARIO + " (" +
                        ID_USUARIO + " INTEGER NOT NULL PRIMARY KEY, " +
                        USERNAME + " VARCHAR(64) NOT NULL, " +
                        PASSWORD + " VARCHAR(64) NOT NULL " +
                        "); ")
        val createTBLPRODUTO = (
                "CREATE TABLE "+ TBL_PRODUTO + " (" +
                        ID_PRODUTO + " INTEGER NOT NULL PRIMARY KEY," +
                        COD_PROD + " VARCHAR(15)NOT NULL, " +
                        DESC_PROD + " VARCHAR(64)NOT NULL, " +
                        TIPO_PROD + " INTEGER NOT NULL, " +
                        UNID_PROD + " INTEGER NOT NULL, " +
                        RASTRO_PROD + " VARCHAR(1) NOT NULL" +
                        ");" )

        val createTBLARMAZEM = (
                "CREATE TABLE "+ TBL_ARMZ + " (" +
                        ID_ARMZ + " INTEGER NOT NULL PRIMARY KEY, " +
                        COD_ARMZ + " VARCHAR(2) NOT NULL, " +
                        DESC_ARMZ + " VARCHAR(255) NOT NULL" +
                        ");")
        val createTBLSALDO = (
                "CREATE TABLE "+ TBL_SALDO + " (" +
                        ID_SALDO + " INTEGER NOT NULL PRIMARY KEY, " +
                        COD_PROD + " VARCHAR(15) NOT NULL, " +
                        COD_ARMZ + " VARCHAR(2) NOT NULL, " +
                        SALDO + " FLOAT NOT NULL, " +
                        "FOREIGN KEY("+ COD_PROD +") REFERENCES "+ TBL_PRODUTO +" ("+ COD_PROD +"), " +
                        "FOREIGN KEY("+ COD_ARMZ +") REFERENCES "+ TBL_ARMZ +" ("+ COD_ARMZ +")" +
                        ");" )
        val createTBLSL = (
                "CREATE TABLE "+ TBL_SALDOLOTE +" (" +
                        ID_SL + " INTEGER NOT NULL PRIMARY KEY, " +
                        COD_PROD + " VARCHAR(15) NOT NULL, " +
                        COD_ARMZ + " VARCHAR(2) NOT NULL, " +
                        LOTE + " VARCHAR(10) NOT NULL, " +
                        VALID_LOTE +" VARCHAR(10) NOT NULL, " +
                        SALDO_LOTE +" FLOAT NOT NULL, " +
                        "FOREIGN KEY("+ COD_PROD +") REFERENCES "+TBL_PRODUTO+" ("+ COD_PROD +")," +
                        "FOREIGN KEY("+ COD_ARMZ +") REFERENCES "+ TBL_ARMZ+" ("+ COD_ARMZ +")" +
                        ");" )
        val createTBLMOVIM = (
                "CREATE TABLE "+ TBL_MOVIM +" (" +
                        ID_MOVIM +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        ARMZ_ORIG +" VARCHAR(2) NOT NULL, " +
                        COD_PROD + " VARCHAR(15) NOT NULL, " +
                        LOTE + " VARCHAR(10), " + //CAMPO OPCIONAL (SERÁ VAZIO SE O CAMPO RASTRO ESTIVER VAZIO)
                        QTD_MOVIM + " FLOAT NOT NULL, " +
                        ARMZ_DEST + " VARCHAR(2) NOT NULL, " +
                        DATAHORA + " VARCHAR(13) NOT NULL, " +
                        USERNAME + " VARCHAR(64) NOT NULL, " +
                        STATUS_SYNC + " INT " +
                        ");")

        val createTBLNOTIF = (
                "CREATE TABLE "+ TBL_NOTIF +" (" +
                        ID_NOTIF +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        MENSG +" VARCHAR(255) NOT NULL, " +
                        DATAHORA +" VARCHAR(13) NOT NULL, " +
                        DADOS_LCM + " VARCHAR(255) NOT NULL, " +
                        USERNAME + " VARCHAR(64), " +
                        LIDO + " VARCHAR(64), " +
                        STATUS_SYNC + " INT" +
                        ");")


        val createDBREQS = (
                "CREATE TABLE "+ TBL_REQUISICAO +" (" +
                        ID_REQUISICAO +" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        COD_PROD_REQ +" VARCHAR(15) NOT NULL, " +
                        QTD_REQ +" FLOAT NOT NULL, " +
                        QTD_ATEND + " FLOAT, " +
                        QTD_CONF +" FLOAT, " +
                        USER_REQ +" VARCHAR(64), " +
                        USER_ATEND +" VARCHAR(64), " +
                        USER_CONF +" VARCHAR(64), " +
                        DATA_REQ +" VARCHAR(13) NOT NULL, " +
                        DATA_ATEND +" VARCHAR(13), " +
                        DATA_CONF +" VARCHAR(13), " +
                        STATUS_SYNC +" INTEGER, " +
                        "FOREIGN KEY("+ COD_PROD +") REFERENCES "+ TBL_PRODUTO+" ("+ COD_PROD +")" +
                        ");" )

    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTBLUSUARIO)
        db?.execSQL(createTBLPRODUTO)
        db?.execSQL(createTBLARMAZEM)
        db?.execSQL(createTBLSALDO)
        db?.execSQL(createTBLSL)
        db?.execSQL(createTBLMOVIM)
        db?.execSQL(createTBLNOTIF)
        //db?.execSQL(createDBREQS)
        //db?.execSQL(createDBPE)
        //db?.execSQL("INSERT INTO $TBL_NOTIF ($MENSG, $DADOS_LCM, $USERNAME) VALUES ('kane','123'), ('gilberto','12345', 'Gilberto Gonçalves'), ('zack', 'zsjl', 'Zachary Snyder');")
        //db?.execSQL("INSERT INTO Usuario (username, password) VALUES ('kane','123'), ('gilberto','12345', 'Gilberto Gonçalves'), ('zack', 'zsjl', 'Zachary Snyder');")
        //db?.execSQL("INSERT INTO produto (descProduto, qeProduto, validProduto, tipoVProduto) VALUES ('Selecione o item','', '', '');")
        //db?.execSQL("INSERT INTO produto (descProduto, qeProduto, validProduto, tipoVProduto) VALUES ('Pão 5 15 D','5', '15', 'D'), ('Pão 13 3 M','13', '3', 'M'), ('Pão 1 13 S', '1', '13', 'S');")
        //
    }


    fun externalExecSQL(query: String){
        val db = this.writableDatabase
        db?.execSQL(query)
    }

    fun externalExecSQLSelect(username: String, pw: String): Boolean {
        var checked = false
        var result = db.query(
            "Usuario",
            arrayOf("username",
                "password",
            ),
            "username = '$username' AND password = '$pw'"/* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (result != null && result.getCount() > 0) {
            checked = true
            Log.d("Debug", "Result $result")
            var gg = result
            return checked
        }else{
            return checked
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        var dropDBPCP = ("DROP TABLE IF EXISTS $TBL_USUARIO; " +
                "DROP TABLE IF EXISTS $TBL_PRODUTO; " +
                "DROP TABLE IF EXISTS $TBL_ARMZ; " +
                "DROP TABLE IF EXISTS $TBL_MOVIM; " +
                "DROP TABLE IF EXISTS $TBL_SALDO; " +
                "DROP TABLE IF EXISTS $TBL_SALDOLOTE; ")
        db.execSQL(dropDBPCP)
        onCreate(db)
    }

    fun insertNotificacao(idNotificacao: Int, mensagem: String, dataHora: String,
                          dadosLancamento: String, username: String, lido: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idNotificacao", idNotificacao)
                put("mensagem", mensagem)
                put("dataHora", dataHora)
                put("dadosLancamento", dadosLancamento)
                put("username", username)
                put("lido", lido)
            }
            db.insert("Notificacao", null, values)
            Log.d("SQLiteNotf", "Query executada com sucesso: $idNotificacao")
        } catch (e: SQLiteException) {
            Log.e("SQLiteErrorCGP", "Erro ao inserir Notificacao: $idNotificacao", e)
        } finally {
            db.close()
        }
    }

    fun updateNotificacaoFinal(idNotificacao: Int, statusSync: Int?) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("statusSync", statusSync)
            }

            // Executa o update com cláusula WHERE
            val rowsUpdated = db.update(
                "Notificacao",                    // Nome da tabela
                values,                     // Valores a serem atualizados
                "idNotificacao = ?",              // Cláusula WHERE
                arrayOf(idNotificacao.toString()) // Argumentos da cláusula WHERE
            )

            if (rowsUpdated > 0) {
                Log.d("SQLiteNotificacao", "Registro atualizado com sucesso: idNotificacao = $idNotificacao")
            } else {
                Log.d("SQLiteNotificacao", "Nenhum registro encontrado para atualizar: idNotificacao = $idNotificacao")
            }
        } catch (e: SQLiteException) {
            Log.e("SQLiteError", "Erro ao atualizar carga: idNotificacao = $idNotificacao", e)
        } finally {
            db.close()
        }
    }

    fun insertProduto(idProduto: Int, codProduto: String, descProduto: String,
                      tipoProduto: String, unidMedida: String, rastro: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idProduto", idProduto)
                put("codProduto", codProduto)
                put("descProduto", descProduto)
                put("tipoProduto", tipoProduto)
                put("unidMedida", unidMedida)
                put("rastro", rastro)
            }
            db.insert("Produto", null, values)
            Log.d("SQLiteProduto", "Query executada com sucesso: $idProduto")
        } catch (e: SQLiteException) {
            Log.e("SQLiteErrorCGP", "Erro ao inserir Notificacao: $idProduto", e)
        } finally {
            db.close()
        }
    }
    fun insertArmz(idArmazem: Int, codArmazem: String, descArmazem: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idArmazem", idArmazem)
                put("codArmazem", codArmazem)
                put("descArmazem", descArmazem)

            }
            db.insert("Armazem", null, values)
            Log.d("SQLiteArmazem", "Query executada com sucesso: $idArmazem")
        } catch (e: SQLiteException) {
            Log.e("SQLiteErrorCGP", "Erro ao inserir Notificacao: $idArmazem", e)
        } finally {
            db.close()
        }
    }
    fun insertSaldo(idSaldo: Int, codProduto: String, codArmazem: String, saldo: Float) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idSaldo", idSaldo)
                put("codProduto", codProduto)
                put("codArmazem", codArmazem)
                put("saldo", saldo)

            }
            var table = "Saldo"
            db.insert("$table", null, values)
            Log.d("SQLite$table", "Query executada com sucesso: $idSaldo")
        } catch (e: SQLiteException) {
            Log.e("SQLiteErrorCGP", "Erro ao inserir Notificacao: $idSaldo", e)
        } finally {
            db.close()
        }
    }
    fun insertSaldoLote(idSaldoLote: Int, codProduto: String, codArmazem: String, lote: String,
                        validLote: String, saldoLote: Float) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put("idSaldoLote", idSaldoLote)
                put("codProduto", codProduto)
                put("codArmazem", codArmazem)
                put("lote", lote)
                put("validLote", validLote)
                put("saldoLote", saldoLote)

            }
            var table = "SaldoLote"
            db.insert("$table", null, values)
            Log.d("SQLite$table", "Query executada com sucesso: $idSaldoLote")
        } catch (e: SQLiteException) {
            Log.e("SQLiteErrorCGP", "Erro ao inserir Notificacao: $idSaldoLote", e)
        } finally {
            db.close()
        }
    }
 

    fun countNotf(username: String): Int {
        var result = db.query(
            TBL_NOTIF,
            arrayOf("$ID_NOTIF AS ${BaseColumns._ID}"
            ),
            "$LIDO = 'N' AND $USERNAME = '$username'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (result != null && result.getCount() > 0) {
            return result.count
        }else{
            return 0
        }

    }

    fun getArmz(): Cursor {
        return db.query(
            TBL_ARMZ,
            arrayOf("$ID_ARMZ AS ${BaseColumns._ID}",
                COD_ARMZ,
                DESC_ARMZ
            ),
            null /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
    }
    fun getCodArmz(idArmz: String): Cursor? {
        var cursor = db.query(
            TBL_ARMZ,
            arrayOf("$ID_ARMZ AS ${BaseColumns._ID}",
                COD_ARMZ,
                DESC_ARMZ
            ),
            "$ID_ARMZ = $idArmz" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun getPrd(): Cursor {
        return db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                COD_PROD,
                DESC_PROD,
                UNID_PROD
            ),
            null /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
    }
    fun getCodPrd(idPrd: String): Cursor? {
        var cursor = db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                COD_PROD
            ),
            "$ID_PRODUTO = $idPrd" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun getDetailProdutos(idPrd: Int): Cursor {
        return db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                COD_PROD,
                DESC_PROD,
                TIPO_PROD,
                UNID_PROD,
                RASTRO_PROD
            ),
            "idProduto = $idPrd" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null /* ORDER BY clause products will be shown alphabetically a->z*/
        )
    }
    fun getDescProdutosEst(idPrd: String): Cursor? {
        var cursor = db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                DESC_PROD,
                COD_PROD
            ),
            "codProduto = '$idPrd'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null /* ORDER BY clause products will be shown alphabetically a->z*/
        )

        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor

    }

    fun getDescProdutos(idPrd: Int): Cursor? {
        var cursor = db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                DESC_PROD,
            ),
            "idProduto = $idPrd" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null /* ORDER BY clause products will be shown alphabetically a->z*/
        )

        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun getInternalRequisicao(): Cursor? {
        var cursor = db.query(
            TBL_REQUISICAO,
            arrayOf(
                "$ID_REQUISICAO AS ${BaseColumns._ID}",
                COD_PROD_REQ,
                QTD_REQ,
                QTD_ATEND,
                QTD_CONF,
                USER_REQ,
                USER_ATEND,
                USER_CONF,
                DATA_REQ,
                DATA_ATEND,
                DATA_CONF,
                STATUS_SYNC
            ),
            USER_ATEND + " IS NULL" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            ID_REQUISICAO + " DESC"//ID_REQUISICAO + " DESC" //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun countReqs(username: String): Int {
        var result = db.query(
            TBL_REQUISICAO,
            arrayOf("$ID_REQUISICAO AS ${BaseColumns._ID}"
            ),
            null ,//"$LIDO = 'N' AND $USERNAME = '$username'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (result != null && result.getCount() > 0) {
            return result.count
        }else{
            return 0
        }

    }

    fun getCodRealProd(id: Int?): Cursor? {
        var cursor = db.query(
            TBL_PROD_EST,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                COD_PROD_EST,
            ),
            "idProduto = $id" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null /* ORDER BY clause products will be shown alphabetically a->z*/
        )

        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor

    }

    fun getLote(armzOrig: String, codProd: String): Cursor? {
        var query = """
            SELECT $ID_SL, $LOTE, $SALDO_LOTE
            FROM $TBL_SALDOLOTE
            WHERE $COD_ARMZ = '$armzOrig' AND $COD_PROD = '$codProd'
            ORDER BY 
                CASE 
                    WHEN $SALDO_LOTE > 0 THEN 1  -- Prioriza registros com saldo > 0
                    ELSE 2                       -- Depois registros com saldo <= 0
                END;

        """.trimIndent() //+
        //"UNION SELECT * FROM Carga WHERE codinomeCarga = '';"
        var cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return null
        }
        return cursor

    }

    fun getSaldo(armzOrig: String, codProd: String): Cursor? {
        var cursor = db.query(
            TBL_SALDO,
            arrayOf(
                "$ID_SALDO AS ${BaseColumns._ID}",
                SALDO
            ),
            "$COD_ARMZ = '$armzOrig' AND $COD_PROD = '$codProd'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun getRastro(codProd: String): Boolean {
        var cursor = db.query(
            TBL_PRODUTO,
            arrayOf(
                "$ID_PRODUTO AS ${BaseColumns._ID}",
                RASTRO_PROD
            ),
            "$COD_PROD = '$codProd'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return false
        } else if (cursor.getString(1) == "L") {
            return true
        }
        return false
    }

    fun getInternalMovimento(): Cursor? {
        var cursor = db.query(
            TBL_MOVIM,
            arrayOf(
                "$ID_MOVIM AS ${BaseColumns._ID}",
                ARMZ_ORIG,
                COD_PROD,
                LOTE,
                QTD_MOVIM,
                ARMZ_DEST,
                DATAHORA,
                USERNAME,
                STATUS_SYNC
            ),
            "$STATUS_SYNC = 0" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }

    fun getProdutos(): Cursor {
        return db.query(
            TBL_PRODUTO,
            arrayOf("$ID_PRODUTO AS ${BaseColumns._ID}",
                DESC_PROD,
                /*QE_PROD,
                VALID_PROD,
                TIPOV_PROD*/
            ),
            null /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            null //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
    }
        fun getInternalNotificacao(username: String): Cursor? {
            var cursor = db.query(
                TBL_NOTIF,
                arrayOf(
                    "$ID_NOTIF AS ${BaseColumns._ID}",
                    MENSG,
                    DATAHORA,
                    DADOS_LCM,
                    USERNAME,
                    LIDO
                ),
                "$USERNAME = '$username'" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
                null /* arguments to replace ? place holder in the WHERE clause, null if none */,
                null /* GROUP BY clause, null if no GROUP BY clause */,
                null /* HAVING CLAUSE, null if no HAVING clause */,
                ID_NOTIF + " DESC" //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
            )
            if (cursor == null || !cursor.moveToFirst()) {
                return null
            }
            return cursor
        }

    fun getLastNotificationId(): Int? {
        val cursor = db.query(
            TBL_NOTIF,
            arrayOf(ID_NOTIF),
            null, // Sem WHERE
            null,
            null,
            null,
            "$ID_NOTIF DESC", // Ordena pelo maior ID
            "1" // Limita a 1 resultado
        )

        return if (cursor != null && cursor.moveToFirst()) {
            cursor.getInt(0) // Retorna o ID do último registro
        } else {
            null
        }.also {
            cursor?.close() // Fecha o cursor para evitar leaks
        }
    }

    fun setNotificationRead(id: Int?) {
        var query: String
        if (id != null){
            query = "UPDATE $TBL_NOTIF " +
                    "SET $LIDO = 'S' AND $STATUS_SYNC = 0 " +
                    "WHERE $ID_NOTIF = $id"
            db.execSQL(query)
        }
    }
    fun getNotificationRead(): Cursor? {
        var cursor = db.query(
            TBL_NOTIF,
            arrayOf(
                "$ID_NOTIF AS ${BaseColumns._ID}",
            ),
            "$LIDO = 'S' AND $STATUS_SYNC = 0" /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            ID_NOTIF + " DESC" //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        return cursor
    }
    fun arrayIdNotf(): ArrayList<Int>? {
        var cursor = db.query(
            TBL_NOTIF,
            arrayOf(
                "$ID_NOTIF AS ${BaseColumns._ID}",
            ),
            null /* WHERE clause less the WHERE keyword, null = no WHERE clause */,
            null /* arguments to replace ? place holder in the WHERE clause, null if none */,
            null /* GROUP BY clause, null if no GROUP BY clause */,
            null /* HAVING CLAUSE, null if no HAVING clause */,
            ID_NOTIF //DESC_PROD + " ASC" /* ORDER BY clause products will be shown alphabetically a->z*/
        )
        if (cursor == null || !cursor.moveToFirst()) {
            return null
        }
        var cursorArray = ArrayList<Int>()
        cursor.moveToFirst()
        cursorArray.add(cursor.getInt(0))
        while (cursor.moveToNext()) {
            cursorArray.add(
                cursor.getInt(0)
            )
        }
        return cursorArray
    }

    fun getReq(): Cursor? {
        val selectQuery =
            "SELECT $ID_REQUISICAO, " +
                    "$COD_PROD_EST, " +
                    "$QTD_REQ, " +
                    "$QTD_ATEND, " +
                    "$QTD_CONF, " +
                    "$USER_REQ, " +
                    "$USER_ATEND, " +
                    "$USER_CONF, " +
                    "$DATA_REQ, " +
                    "$DATA_ATEND, " +
                    "$DATA_CONF," +
                    "$STATUS_SYNC " +
                    "FROM $TBL_REQUISICAO WHERE $STATUS_SYNC = 0;"
        val result = db.rawQuery(selectQuery, null)
        return result
    }
    fun getUpdReq(): Cursor? {
        val selectQuery =
            "SELECT $ID_REQUISICAO, " +
                    "$COD_PROD_EST, " +
                    "$QTD_REQ, " +
                    "$QTD_ATEND, " +
                    "$QTD_CONF, " +
                    "$USER_REQ, " +
                    "$USER_ATEND, " +
                    "$USER_CONF, " +
                    "$DATA_REQ, " +
                    "$DATA_ATEND, " +
                    "$DATA_CONF, " +
                    "$STATUS_SYNC " +
                    "FROM $TBL_REQUISICAO WHERE $STATUS_SYNC = 2;"
        val result = db.rawQuery(selectQuery, null)
        return result
    }

    /*fun insertDone(id: Int?) {
        var query: String
        if (id != null){
            query = "UPDATE $TBL_MOVIM " +
                    "SET statusSync = 1 " +
                    "WHERE $ID_MOVIM = $id"
            db.execSQL(query)
        }
    }*/

    fun insertDone(table: String, id: Int?) {
        var query: String
        var idName: String
        if (table == TBL_MOVIM){
            idName = ID_MOVIM
        }else /*if (table == TBL_REQUISICAO)*/{
            idName = ID_REQUISICAO
        }
        if (id != null){
            query = "UPDATE $table " +
                    "SET statusSync = 1 " +
                    "WHERE $idName = $id"
            db.execSQL(query)
        }
    }

    fun updateMovimento(idMovimento: Int, statusSync: Int?) {
        val db = this.writableDatabase
        scope.launch(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put("statusSync", statusSync)
                }

                // Executa o update com cláusula WHERE
                val rowsUpdated = db.update(
                    TBL_MOVIM,                    // Nome da tabela
                    values,                     // Valores a serem atualizados
                    "idMovimento = ?",              // Cláusula WHERE
                    arrayOf(idMovimento.toString()) // Argumentos da cláusula WHERE
                )

                if (rowsUpdated > 0) {
                    Log.d("SQLiteCarga", "Registro atualizado com sucesso: idCarga = $idMovimento")
                } else {
                    Log.d(
                        "SQLiteCarga",
                        "Nenhum registro encontrado para atualizar: idCarga = $idMovimento"
                    )
                }
            } catch (e: SQLiteException) {
                Log.e("SQLiteError", "Erro ao atualizar carga: idCarga = $idMovimento", e)
            } finally {
                db.close()
            }
        }
    }

}



