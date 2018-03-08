package joken.ac.jp.omoroic

import android.nfc.Tag
import android.nfc.tech.NfcF
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Arrays

import android.content.ContentValues.TAG

class NfcReader {

    fun readTag(tag: Tag): Array<ByteArray>? {
        val nfc = NfcF.get(tag)
        try {
            nfc.connect()
            // System 1 System code -> 0x0003
            val targetSystemCode = byteArrayOf(0x00.toByte(), 0x03.toByte())

            // make polling command
            val polling = polling(targetSystemCode)

            // send command
            val pollingRes = nfc.transceive(polling)

            // System 0 のidｍを取得(1バイト目はデータサイズ、2バイト目はレスポンスコード、idmのサイズは8バイト)
            val targetIDm = Arrays.copyOfRange(pollingRes, 2, 10)

            val size = 4

            // 対象のサービスコード -> 0x090f
            val targetServiceCode = byteArrayOf(0x09.toByte(), 0x0f.toByte())

            // Read Without Encryption
            val req = readWithoutEncryption(targetIDm, size, targetServiceCode)

            // コマンドを送信して結果を取得
            val res = nfc.transceive(req)

            Log.d("ans", res.toString())

            nfc.close()

            // 結果をパースしてデータだけ取得
            return parse(res)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

        return null
    }

    /**
     * Pollingコマンドの取得。
     * @param systemCode byte[] 指定するシステムコード
     * @return Pollingコマンド
     * @throws IOException
     */
    private fun polling(systemCode: ByteArray): ByteArray {
        val bout = ByteArrayOutputStream(100)

        bout.write(0x00)           // データ長バイトのダミー
        bout.write(0x00)           // コマンドコード
        bout.write(systemCode[0].toInt())  // systemCode
        bout.write(systemCode[1].toInt())  // systemCode
        bout.write(0x01)           // リクエストコード
        bout.write(0x0f)           // タイムスロット

        val msg = bout.toByteArray()
        msg[0] = msg.size.toByte() //
        for (a in msg.indices) {
            Log.d("tag", msg[a].toString())
        }
        return msg
    }

    /**
     * Read Without Encryptionコマンドの取得。
     * @param idm 指定するシステムのID
     * @param size 取得するデータの数
     * @return Read Without Encryptionコマンド
     * @throws IOException
     */
    @Throws(IOException::class)

    private fun readWithoutEncryption(idm: ByteArray, size: Int, serviceCode: ByteArray): ByteArray {
        val bout = ByteArrayOutputStream(100)

        bout.write(0)              // データ長バイトのダミー
        bout.write(0x06)           // コマンドコード
        bout.write(idm)            // IDm 8byte
        bout.write(1)              // サービス数の長さ(以下２バイトがこの数分繰り返す)

        // サービスコードの指定はリトルエンディアンなので、下位バイトから指定します。
        bout.write(serviceCode[1].toInt()) // サービスコード下位バイト
        bout.write(serviceCode[0].toInt()) // サービスコード上位バイト
        bout.write(size)           // ブロック数

        // ブロック番号の指定
        for (i in 0 until size) {
            bout.write(0x80)       // ブロックエレメント上位バイト 「Felicaユーザマニュアル抜粋」の4.3項参照
            bout.write(i)          // ブロック番号
        }

        val msg = bout.toByteArray()
        msg[0] = msg.size.toByte() // 先頭１バイトはデータ長
        return msg
    }

    /**
     * Read Without Encryption応答の解析。
     * @param res byte[]
     * @return 文字列表現
     * @throws Exception
     */
    @Throws(Exception::class)

    private fun parse(res: ByteArray): Array<ByteArray> {
        // res[10] エラーコード。0x00の場合が正常
        for (a in res.indices) {
            Log.d("hex:", Integer.toHexString(res[a].toInt()))
        }
        if (res[10].toInt() != 0x00)
            throw RuntimeException("this code is " + res[10])

        // res[12] 応答ブロック数
        // res[13 + n * 16] 実データ 16(byte/ブロック)の繰り返し
        val size = res[12].toInt()
        val data = Array(size) { ByteArray(16) }
        for (i in 0 until size) {
            val tmp = ByteArray(16)
            val offset = 13 + i * 16
            for (j in 0..15) {
                tmp[j] = res[offset + j]
            }

            data[i] = tmp
        }
        return data
    }
}