package com.melihilhan.weatherapp.service

import com.melihilhan.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/2.5/weather?&units=metric&APPID=4958e410e55ef05c9c6bdec091512f82")
    fun getData(
        @Query("q") cityName: String
    ): Single<WeatherModel>

}