package joken.ac.jp.omoroic

import com.google.gson.annotations.SerializedName



/**
 * ic card data
 */
data class icCardData (
    @SerializedName("LocalCode")
    val areaCode:String,
    @SerializedName("LineCode")
    val lineCode: String,
    @SerializedName("CodeStation")
    val stationCode: String
)
