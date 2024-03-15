package api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginInstance {

    private const val BASE_URL = "http://43.203.20.197:8080/"
    val gson : Gson =   GsonBuilder().setLenient().create();

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiServices: LoginApi = retrofit.create(LoginApi::class.java)
}