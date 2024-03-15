// ApiService.kt
package api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface ApiService {

    @Headers("accept: application/json","Content-Type: application/json")
    @POST("/api/v1/wastes-info")
    fun mapUser(
        @Body PostResult : PostResult
    ): Call<Int>

    data class PostResult(
        @SerializedName("wasteCode") val wasteCode: String?,
        @SerializedName("userEmail") val userEmail: String?,
        @SerializedName("loginType") val loginType: String?,
        @SerializedName("locationLatitude") val locationLatitude: Double?,
        @SerializedName("locationLongitude") val locationLongitude: Double?,
        @SerializedName("region1depthName") val region1depthName: String?,
        @SerializedName("region2depthName") val region2depthName: String?,
        @SerializedName("region3depthName") val region3depthName: String?
    )

    data class JoinResponse(
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
