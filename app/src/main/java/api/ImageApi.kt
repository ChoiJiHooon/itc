package api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApi {
    @Multipart
    @POST("infer")
    fun ImageSend(
        //@Path("waste_id") waste_id: String,
        @Part imageFile : MultipartBody.Part
    ): Call<Int>

    data class ImageResponse (
        @SerializedName("status")
        val status: Int
    )
}