package com.google.albertasights.services

import com.google.albertasights.models.Place
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

class RetrofitCalls: GetDataServices {

    protected fun retrofitFactory(url: String = "https://albertasights.herokuapp.com/api/v1/"): Retrofit {
        val gson = GsonBuilder()
        gson.setLenient()

        val retrofitBuilder = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
        val clientBuilder = OkHttpClient.Builder()
        retrofitBuilder.client(clientBuilder.build())
        return retrofitBuilder.build()
    }

    private val retrofitService by lazy {
        retrofitFactory().create(GetPointsApi::class.java)
    }

    override fun getAllTerritoryPoints(district: String, callback: (List<Place>?) -> Unit)  {
       val call: Call<List<Place>?> = retrofitService.getPointOfDistrict("Calgary")

        call.enqueue(object : Callback<List<Place>?> {
            override fun onFailure(call: Call<List<Place>?>, t: Throwable) {
                System.out.println("api call failes!!!!!")
                callback(null)
            }

            override fun onResponse(call: Call<List<Place>?>, response: Response<List<Place>?>) {
                System.out.println("should make api call!!!!!!!!!!!!!!!!")
                callback(response.body()!!)
            }

        })
    }

    interface GetPointsApi {
        @GET("points_by_district")
        @Headers("X-Api-Key: 3.14")
        fun getPointOfDistrict(@Query("district") district: String): Call<List<Place>?>
    }
}
