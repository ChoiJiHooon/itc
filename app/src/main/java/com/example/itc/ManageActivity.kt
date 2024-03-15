package com.example.itc

import android.content.ContentValues
import android.content.res.Resources
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import api.ManageApi
import api.ManageInstance.manageApi
import api.WasteApi
import api.WasteInstance
import com.example.itc.databinding.ActivityMainBinding
import com.example.itc.databinding.ActivityManageBinding
import org.xmlpull.v1.XmlPullParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ManageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityManageBinding
    lateinit var manageAdapter: ManageAdapter
    val datas = mutableListOf<ManageData>()
    var manageInfoList: List<ManageApi.ManageInfo>? = null
    var manageInfo : ManageApi.ManageInfo? = null
    var locationLatitude : Double = 0.0
    var locationLongitude : Double = 0.0
    var queryDate : String = ""
    var imageUrl : String = ""
    var reg : String = ""
    var wasteCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_manage)

        initRecycler()
        val sintent = intent

        val useremail = sintent.getStringExtra("Email")
        val logintype = sintent.getStringExtra("Type")

        val call = manageApi.getManageInfo(useremail!!,logintype!!)

        call.enqueue(object : Callback<List<ManageApi.ManageInfo>> {
            override fun onResponse(
                call: Call<List<ManageApi.ManageInfo>>,
                response: Response<List<ManageApi.ManageInfo>>
            ) {
                if (response.isSuccessful) {
                    Log.d(ContentValues.TAG, "Success !! ")

                    manageInfoList = response.body()
                    processManageInfoList(manageInfoList)
                } else {
                    // 실패한 경우의 처리
                    Log.e("API", "Failed to get data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ManageApi.ManageInfo>>, t: Throwable) {
                // 실패한 경우의 처리
                Log.e("API", "Error: ${t.message}")
            }
        })
    }

    private fun initRecycler() {
        manageAdapter = ManageAdapter(this)
        binding.rvInformation.adapter = manageAdapter
    }


    private fun processManageInfoList(manageInfoList: List<ManageApi.ManageInfo>?) {
        var tf : String = ""
        // 데이터를 받아온 후 RecyclerView 초기화
        if (manageInfoList != null) {
            // 각각의 WasteInfo에 대한 처리
            manageInfoList?.forEach { manageInfo ->
                val wasteId : Int = manageInfo.wasteId
                wasteCode = manageInfo.wasteCode
                Log.d("Mypage:", "wastecode: ${wasteCode}")
                locationLatitude = manageInfo.locationLatitude
                locationLongitude = manageInfo.locationLongitude
                queryDate = manageInfo.queryDate
                Log.d("Mypage:", "QueryDate: ${queryDate}")
                imageUrl = manageInfo.imageUrl
                val isQueued: Boolean = manageInfo.isQueued
                val isAccepted: Boolean = manageInfo.isAccepted
                val isPut: Boolean = manageInfo.isPut
                val isTaken: Boolean = manageInfo.isTaken

                if(isQueued == true && isAccepted == false) { tf = "신청대기" }
                else if (isAccepted == true && isPut == false){tf = "신청승인"}
                else if (isPut == true && isTaken == false) {tf = "배치완료"}
                else if (isTaken == true) {tf = "수거완료"}

                val codeManager = CodeManager(resources)
                val value = codeManager.getValueForCode(wasteCode)

                var place = getAddress(locationLatitude, locationLongitude)
                Recycler(imageUrl, value.toString() , place ,queryDate,tf)

            }
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        // Geocoder 선언
        val geocoder = Geocoder(applicationContext, Locale.KOREAN)
        // 안드로이드 API 레벨이 33 이상인 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude, longitude, 1
            ) { address ->
                if (address.size != 0) {
                    // 반환 값에서 전체 주소만 사용한다.
                    // getAddressLine(0)
                    reg = address[0].getAddressLine(0).toString()
                    Log.d("Address", "address reg: $reg")
                }
            }
        } else { // API 레벨이 33 미만인 경우
            @Suppress("DEPRECATION") val addresses =
                geocoder.getFromLocation(latitude, longitude, 3)
            if (addresses != null) {
                reg = addresses[0].getAddressLine(0).toString()
            }
        }
        return reg
    }
    private fun Recycler(image: String, address: String, situation: String, day: String, situ : String) {
        manageAdapter = ManageAdapter(this)
        binding.rvInformation.adapter = manageAdapter

        // imageUrl이 null이 아니면 데이터를 추가
        if (imageUrl != null) {
            datas.add(ManageData(img = image, name = address, age = situation, place = day,situate = situ))
            manageAdapter.datas = datas
            manageAdapter.notifyDataSetChanged()
        } else {
            // imageUrl이 null인 경우에 대한 처리
            Log.e("Recycler", "imageUrl is null")
            // 또는 imageUrl이 null인 경우 아무 동작을 하지 않도록 처리할 수 있습니다.
        }
    }

    class CodeManager(private val resources: Resources) {

        fun getValueForCode(code: String): String? {
            val parser = resources.getXml(R.xml.codes)

            var eventType = parser.eventType
            var currentCode = ""
            var currentValue = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "item") {
                            val parts = parser.nextText().split(",")
                            if (parts.size == 2) {
                                currentCode = parts[0].trim()
                                Log.d(ContentValues.TAG, "Key : ${currentCode}")
                                currentValue = parts[1].trim()
                                Log.d(ContentValues.TAG, "Value : ${currentValue}")
                                if (currentCode == code) { return currentValue}
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item" && currentCode == code) {
                            return currentValue
                        }
                    }
                }

                eventType = parser.next()
            }

            return currentValue
        }
    }
}