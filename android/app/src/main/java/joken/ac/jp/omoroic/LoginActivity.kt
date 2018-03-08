package joken.ac.jp.omoroic

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.beardedhen.androidbootstrap.BootstrapButton
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.auth.LineLoginApi
import kotterknife.bindView
import com.linecorp.linesdk.auth.LineLoginResult
import android.R.id.edit



private var KeyStoreM: AndroidKeyStoreManager? = null



/**
 * Created by jelly on 3/9/2018.
 */
class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1

    val login: BootstrapButton by bindView(R.id.button_login)

    val exit: BootstrapButton by bindView(R.id.quit_login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener{
            try {
                // App-to-app login
                var loginIntent: Intent = LineLoginApi.getLoginIntent(this, 1567094874.toString())
                startActivityForResult(loginIntent, REQUEST_CODE)
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }

        exit.setOnClickListener {
            this.finish()
            this.moveTaskToBack(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE) {
            Log.e("ERROR", "Unsupported Request")
            return
        }

        val result = LineLoginApi.getLoginResultFromIntent(data)

        when (result.responseCode) {

            LineApiResponseCode.SUCCESS -> {
                // Login successful

                val accessToken = result.lineCredential!!.accessToken.accessToken

                KeyStoreM = AndroidKeyStoreManager.getInstance(this)

                var pref: SharedPreferences = getSharedPreferences("pref",MODE_PRIVATE)


                val editor = pref.edit()
                editor.putString("token", KeyStoreM!!.encrypt(accessToken.toByteArray()).toString())
                editor.apply()


                val transitionIntent = Intent(this, MainActivity::class.java)
                startActivity(transitionIntent)
            }

            LineApiResponseCode.CANCEL ->
                // Login canceled by user
                Log.e("ERROR", "LINE Login Canceled by user!!")

            else -> {
                // Login canceled due to other error
                Log.e("ERROR", "Login FAILED!")
                Log.e("ERROR", result.errorData.toString())
            }
        }
    }


}