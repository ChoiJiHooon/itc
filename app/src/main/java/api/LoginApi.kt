package api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginApi {


    @Headers("accept: application/json","Content-Type: application/json")
    @POST("/api/v1/user/login")
    fun LoginUser(
        @Body PostResults : PostResults
    ): Call<LoginResponse>

    data class PostResults(
        @SerializedName("userEmail") val userEmail: String?,
        @SerializedName("loginType") val loginType: String?,
        @SerializedName("accessToken") val accessToken: String?,
    )

    data class LoginResponse (
        @SerializedName("status")
        val status: Int,

        @SerializedName("success")
        val success: Boolean,

        @SerializedName("message")
        val message: String?,
        @SerializedName("data")
        val data: Data? // 이 부분이 추가되었습니다.
    ) {
        data class Data(
            @SerializedName("userId")
            val userId: Int
        )
    }
}