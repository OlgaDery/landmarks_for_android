package com.google.albertasights.services

import com.google.albertasights.ConfigValues
import com.google.albertasights.models.Place
import com.google.albertasights.utils.parseJsonArray
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import org.json.JSONArray
import org.json.JSONObject


class RetrofitCalls: GetDataFromServer {

    private fun retrofitFactory(url: String = ConfigValues.BASE_URL): Retrofit {
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

    override fun getAllTerritoryPoints(callback: (List<Place>?) -> Unit)  {
        if (!ConfigValues.isJsonObject) {
            val call: Call<List<Any>?> = retrofitService.getPointOfDistrict(ConfigValues.setHeaders(), ConfigValues.setParams())

            call.enqueue(object : Callback<List<Any>?> {
                override fun onFailure(call: Call<List<Any>?>, t: Throwable) {
                    callback(null)
                }

                override fun onResponse(call: Call<List<Any>?>, response: Response<List<Any>?>) {
                    val mJsonArray = JSONArray(response.body())
                    val listToReturn = mJsonArray.parseJsonArray()
                    callback(listToReturn)
                }

            })
        } else {
            //Call the method returning
            val call: Call<String?> = retrofitService.getPointOfDistrictFromJsonObject(ConfigValues.setHeaders(), ConfigValues.setParams())

            call.enqueue(object : Callback<String?> {
                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback(null)
                }

                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    val mJsonObject = JSONObject(response.body())
                    val mJsonArray = mJsonObject.getJSONArray(ConfigValues.parameterNameOfJsonObject)
                    val listToReturn = mJsonArray.parseJsonArray()
                    callback(listToReturn)
                }

            })
        }
    }

    interface GetPointsApi {
        @GET(ConfigValues.RELATIVE_URL)
        fun getPointOfDistrict(@HeaderMap headers: MutableMap<String, String>,
                               @QueryMap params: MutableMap<String, String>): Call<List<Any>?>

        @GET(ConfigValues.RELATIVE_URL)
        fun getPointOfDistrictFromJsonObject(@HeaderMap headers: MutableMap<String, String>,
                               @QueryMap params: MutableMap<String, String>): Call<String?>
    }
}
