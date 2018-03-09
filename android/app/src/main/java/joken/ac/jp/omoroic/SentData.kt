package joken.ac.jp.omoroic

import com.google.gson.annotations.SerializedName



/**
 * Created by jelly on 3/9/2018.
 */
class SentData {
    @SerializedName("company")
    var company: Int = 0

    @SerializedName("LineName")
    var LineName: String? = null

    @SerializedName("StatineName")
    var StatineName: String? = null

    @SerializedName("Memo")
    var Memo: String? = null
}
