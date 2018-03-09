package joken.ac.jp.omoroic

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * response data of station
 */
data class StationData(
        @SerializedName("Company")
        val company: String,
        @SerializedName("LineName")
        val lineName: String,
        @SerializedName("StationName")
        val stationName: String,
        @SerializedName("Memo")
        val misc: String) : Serializable