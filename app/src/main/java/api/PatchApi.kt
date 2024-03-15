package api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PatchApi {
    @PATCH("/api/v1/wastes-info")
    fun patchInfo(
        @Body PatchResponse : PatchApi.PatchResponse
        ): Call<Int>

    data class PatchResponse(
        @SerializedName("wasteId") val wasteId: Int,
        @SerializedName("isQueued") val isQueued: Boolean,
        @SerializedName("isAccepted") val isAccepted: Boolean,
        @SerializedName("isPut") val isPut: Boolean,
        @SerializedName("isTaken") val isTaken: Boolean
    )
}