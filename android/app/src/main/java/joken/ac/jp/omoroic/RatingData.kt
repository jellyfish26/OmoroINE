package joken.ac.jp.omoroic

import com.google.gson.annotations.SerializedName

/**
 * rating for "omoroi"
 */
data class RatingData(
        @SerializedName("line")
        val line: String,
        @SerializedName("station")
        val station: String,
        @SerializedName("value")
        val netaType: String,
        @SerializedName("value")
        val ratingCount: Int
)