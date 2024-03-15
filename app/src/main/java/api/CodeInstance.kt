package api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object CodeInstance {
    private const val BASE_URL = "http://43.203.20.197:8080/"
    val gson : Gson =   GsonBuilder().setLenient().create();

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val codeapi : CodeApi = retrofit.create(CodeApi::class.java)
}