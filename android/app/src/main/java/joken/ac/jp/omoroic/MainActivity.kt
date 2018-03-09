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
import android.util.Log
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okio.ByteString
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

    private lateinit var subscriber : Disposable
    private val service = ApiBuild().create(SendApi::class.java)
    private var stationDataList : List<StationData> = mutableListOf()

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

        var NFCans: ByteArray = nfcReader.readTag(tag)!!
        var localcode = ""
        var linecode = ""
        var stationcode = ""

        for(a in 0..NFCans.size - 1)
        {
            Log.d("tagtag: " + a, Integer.toHexString(NFCans[a].toInt()))
        }

        for(a in 0..4) {
            if(Integer.toHexString(NFCans[1 + a * 15].toInt()) == "1") {
                if(NFCans[6 + a * 15].toInt() >= 0){
                    //Localcode 0
                    localcode = "0"
                }else {
                    if(NFCans[15 + a * 15].toInt() == -96){
                        //Localcode 2
                        localcode = "2"
                    }else{
                        //Localcode 1
                        localcode = "1"
                    }
                }
                if(Integer.toHexString(NFCans[6 + a * 15].toInt()).length  <= 2 ) {
                    // Integer.toHexString(NFCans[6 + a * 15].toInt()) is LineCode
                    linecode = Integer.toHexString(NFCans[6 + a * 15].toInt())
                }else{
                    // Integer.toHexString(NFCans[6 + a * 15].toInt()).substring(7,8) is LineCode
                    linecode = Integer.toHexString(NFCans[6 + a * 15].toInt()).substring(7,8)
                }
                if(Integer.toHexString(NFCans[7 + a * 15].toInt()).length  <= 2 ) {
                    // Integer.toHexString(NFCans[7 + a * 15].toInt()) is stationCode
                    stationcode = Integer.toHexString(NFCans[7 + a * 15].toInt())
                }else{
                    // Integer.toHexString(NFCans[7 + a * 15].toInt()).substring(7,8) is stationCode
                    stationcode = Integer.toHexString(NFCans[7 + a * 15].toInt()).substring(7,8)
                }
            }
        }
        val icCardData = icCardData(localcode, linecode, stationcode)
        sendIcCardData(icCardData)
    }

    fun sendIcCardData(icCardData: icCardData){
        service.postICData(icCardData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    data -> stationDataList += data
                    if(stationDataList.size >= 4){
                        val i = Intent(this, SendActivity::class.java)
                        i.putExtra("stationData", stationDataList.toTypedArray())
                        startActivity(i)
                    }
                })
    }

    override fun onPause()
    {
        super.onPause()
        mAdapter?.disableForegroundDispatch(this)
    }
}
