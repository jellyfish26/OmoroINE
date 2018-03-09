package joken.ac.jp.omoroic

import android.app.Service
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Util class
 */

fun ApiBuild(): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://trompot.mydns.jp/hack/api")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}
