package joken.ac.jp.omoroic

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Api Rules
 */
interface SendApi {

    @POST("/station")
    fun postICData(@Body icData: icCardData): Observable<StationData>

    @POST("/register")
    fun postRating(@Body ratingData: RatingData): Observable<Void>

}