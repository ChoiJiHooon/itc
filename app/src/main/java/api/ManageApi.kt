package api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ManageApi {

    @GET("/api/v1/admin/{user-email}/{login-type} ")
    fun getManageInfo(
        @Path("user-email") userEmail: String,
        @Path("login-type") loginType: String
    ): Call<List<ManageInfo>>

    data class ManageInfo(
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