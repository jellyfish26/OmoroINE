package joken.ac.jp.omoroic

import retrofit2.http.Body
import retrofit2.http.POST
import io.reactivex.Observable

/**
 * Api Rules
 */
interface SendApi {

    @POST("/station")
    fun postICData(@Body icData: icCardData):Observable<StationData>

    @POST("/register")
    fun postRating(@Body ratingData: RatingData):Observable<Void>

}