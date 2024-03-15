package api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImageInstance {
    private const val BASE_URL = "http://110.13.206.189:5000/"
    val gson : Gson =   GsonBuilder().setLenient().create();

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val imageapi : ImageApi = retrofit.create(ImageApi::class.java)
}