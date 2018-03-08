package joken.ac.jp.omoroic

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

class MainActivity : AppCompatActivity()
{

    private var intentFiltersArray: Array<IntentFilter>? = null
    private var techListsArray: Array<Array<String>>? = null
    private var mAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val nfcReader = NfcReader()

    private var KeyStoreM: AndroidKeyStoreManager? = null

    private var Stoken: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        val intent = Intent(this, LoginActivity::class.java)

        KeyStoreM = AndroidKeyStoreManager.getInstance(this)

        var pref: SharedPreferences = getSharedPreferences("pref",MODE_PRIVATE)

        if(pref.getString("token","") == "") {
            startActivity(intent)
        }

        //var token: ByteArray = KeyStoreM!!.decrypt(pref.getString("token","").toByteArray())

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)

        try
        {
            ndef.addDataType("text/plain")
        }
        catch (e: IntentFilter.MalformedMimeTypeException)
        {
            throw RuntimeException("fail", e)
        }

        intentFiltersArray = arrayOf(ndef)

        techListsArray = arrayOf(arrayOf(NfcF::class.java.name))

        // NfcAdapterを取得
        mAdapter = NfcAdapter.getDefaultAdapter(applicationContext)

    }

    override fun onResume()
    {
        super.onResume()
        // NFCの読み込みを有効化
        mAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onNewIntent(intent: Intent)
    {
        // IntentにTagの基本データが入ってくるので取得。
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        nfcReader.readTag(tag)
    }

    override fun onPause()
    {
        super.onPause()
        mAdapter?.disableForegroundDispatch(this)
    }
}
