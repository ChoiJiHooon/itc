package api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WasteApi {
        @GET("/api/v1/wastes-info/{user-email}/{login-type}")
        fun getWasteInfo(
            @Path("user-email") userEmail: String,
            @Path("login-type") loginType: String
        ): Call<List<WasteInfo>>

    data class WasteInfo(
        @SerializedName("wasteId") val wasteId: Int,
        @SerializedName("wasteCode") val wasteCode: String,
        @SerializedName("locationLatitude") val locationLatitude: Double,
        @SerializedName("locationLongitude") val locationLongitude: Double,
        @SerializedName("queryDate") val queryDate: String,
        @SerializedName("imageUrl") val imageUrl : String,
        @SerializedName("isQueued") val isQueued: Boolean,
        @SerializedName("isAccepted") val isAccepted: Boolean,
        @SerializedName("isPut") val isPut: Boolean,
        @SerializedName("isTaken") val isTaken: Boolean
    )
}