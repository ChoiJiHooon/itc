package api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CodeApi {
    @Multipart
    @POST("/api/v1/wastes-info/image/{waste_id}")
    fun CodeSend(
        @Path("waste_id", encoded = true) waste_id: String,
        @Part imageFile : MultipartBody.Part
    ): Call<String>

    data class CodeResponse (
        @SerializedName("status")
        val status: Int
    )
}