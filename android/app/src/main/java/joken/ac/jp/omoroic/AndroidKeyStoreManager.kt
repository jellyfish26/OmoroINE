package joken.ac.jp.omoroic

import android.content.Context
import android.security.KeyPairGeneratorSpec

import java.io.IOException
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.InvalidAlgorithmParameterException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Calendar

import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

/** Android Keystore Sample for API Level 18  */
class AndroidKeyStoreManager private constructor(private var mContext: Context?) {
    private var mKeyStore: KeyStore? = null


    val publicKey: PublicKey
        get() {
            try {
                return if (mKeyStore!!.containsAlias(KEY_STORE_ALIAS)) {
                    mKeyStore!!.getCertificate(KEY_STORE_ALIAS).publicKey
                } else {
                    createKeyPair()!!.public
                }
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
                throw RuntimeException("Unable Public Key.")
            }

        }

    val privateKey: PrivateKey
        get() {
            try {
                return if (mKeyStore!!.containsAlias(KEY_STORE_ALIAS)) {
                    mKeyStore!!.getKey(KEY_STORE_ALIAS, null) as PrivateKey
                } else {
                    createKeyPair()!!.private
                }
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
                throw RuntimeException("Unable Private Key.")
            }

        }

    init {
        try {
            mKeyStore = KeyStore.getInstance(KEY_PROVIDER)
            mKeyStore!!.load(null)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("I/O Exception")
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            throw RuntimeException("Security Exception")
        }

    }

    private fun createKeyPair(): KeyPair? {
        var kpg: KeyPairGenerator? = null

        try {
            kpg = KeyPairGenerator.getInstance(KEY_STORE_ALGORITHM, KEY_PROVIDER)
            kpg!!.initialize(createKeyPairGeneratorSpec())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
            return null
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
            return null
        }

        return kpg.generateKeyPair()
    }

    private fun createKeyPairGeneratorSpec(): KeyPairGeneratorSpec {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 100)

        return KeyPairGeneratorSpec.Builder(mContext!!)
                .setAlias(KEY_STORE_ALIAS)
                .setSubject(X500Principal(String.format("CN=%s", KEY_STORE_ALIAS)))
                .setSerialNumber(BigInteger.valueOf(100000))
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
    }

    fun encrypt(bytes: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            return cipher.doFinal(bytes)

        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
            throw RuntimeException("Encryption failed.")
        }

    }

    fun decrypt(bytes: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            return cipher.doFinal(bytes)

        } catch (e: GeneralSecurityException) {
            e.printStackTrace()

            throw RuntimeException("Decryption failed.")
        }

    }

    companion object {
        private var sInstance: AndroidKeyStoreManager? = null
        private val KEY_STORE_ALIAS = "sample_alias" // Change me

        private val KEY_STORE_ALGORITHM = "RSA"
        private val KEY_PROVIDER = "AndroidKeyStore"
        private val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"

        fun getInstance(context: Context): AndroidKeyStoreManager {
            if (sInstance == null) {
                sInstance = AndroidKeyStoreManager(context)
            } else {
                sInstance!!.mContext = context
            }
            return sInstance as AndroidKeyStoreManager
        }
    }

}