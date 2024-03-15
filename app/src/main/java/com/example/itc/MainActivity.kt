package com.example.itc

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import api.WasteApi
import api.WasteInstance.wasteApi
import com.example.itc.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import android.content.res.Resources
import api.PatchApi
import api.PatchInstance.patchApi
import org.xmlpull.v1.XmlPullParser

/*
private const val NUM_PAGES = 2

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var infoAdapter: InfoAdapter
    val datas = mutableListOf<InfoData>()
    private lateinit var viewPager: ViewPager2
    var wasteinfo: WasteApi.WasteInfo? = null
    var certiList: List<WasteApi.WasteInfo>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sintent = intent
        val email = sintent.getStringExtra("Email")
        val type = sintent.getStringExtra("Type")
        val call = wasteApi.getWasteInfo(email.toString(), type.toString())

        call.enqueue(object : Callback<List<WasteApi.WasteInfo>> {
            override fun onResponse(
                call: Call<List<WasteApi.WasteInfo>>,
                response: Response<List<WasteApi.WasteInfo>>
            ) {
                if (response.isSuccessful) {
                    val wasteInfoList = response.body()

                    // 가져온 데이터를 사용하여 원하는 작업 수행
                    // 예: 리스트를 RecyclerView에 바인딩하거나, 다른 UI 작업 수행
                } else {
                    // 실패한 경우의 처리
                    Log.e("API", "Failed to get data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<WasteApi.WasteInfo>>, t: Throwable) {
                // 실패한 경우의 처리
                Log.e("API", "Error: ${t.message}")
            }
        })

        //val intent = Intent()
    }

    private fun getAddress(latitude: Double, longitude: Double) {
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
                    val reg = address[0].getAddressLine(0).toString()
                    Log.d("Address","address reg: ${reg}")
                }
            }
        } else { // API 레벨이 33 미만인 경우
            @Suppress("DEPRECATION") val addresses = geocoder.getFromLocation(latitude, longitude, 3)
            if (addresses != null) {
                val reg = addresses[0].getAddressLine(0).toString()
                val addressList = reg.split(" ")
                val threeElement = addressList[3]
                Log.d("Address","address reg: ${reg}")
                val reg1 = addresses[0].adminArea.toString()
            }
        }
    }

    private fun initRecycler() {
        infoAdapter = InfoAdapter(this)
        binding.rvProfile.adapter = infoAdapter

        datas.apply {
            add(InfoData(img = R.drawable.itc, name = "mary", age = 24))
            add(InfoData(img = R.drawable.itc2, name = "jenny", age = 26))
            add(InfoData(img = R.drawable.itcfinal, name = "jhon", age = 27))
            //add(InfoData(img = R.drawable.profile5, name = "ruby", age = 21))
            //add(InfoData(img = R.drawable.profile4, name = "yuna", age = 23))

            infoAdapter.datas = datas
            infoAdapter.notifyDataSetChanged()

        }
    }}*/

private const val NUM_PAGES = 2

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var infoAdapter: InfoAdapter
    val datas = mutableListOf<InfoData>()
    private lateinit var viewPager: ViewPager2
    var wasteinfo: WasteApi.WasteInfo? = null
    var patchinfo: PatchApi.PatchResponse? = null
    var patchInfoList: List<PatchApi.PatchResponse>? = null
    var wasteInfoList: List<WasteApi.WasteInfo>? = null
    var locationLatitude : Double = 0.0
    var locationLongitude : Double = 0.0
    var queryDate : String = ""
    var imageUrl : String = ""
    var reg : String = ""
    var wasteCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 리사이클러뷰 초기화
        initRecycler()

        val sintent = intent
        val email = sintent.getStringExtra("Email")
        val type = sintent.getStringExtra("Type")
        Log.d("Intent","Email Price : ${email.toString()}")
        Log.d("Intent","Type Price : ${type.toString()}")
        //val pat = patchApi.patchInfo()
        val call = wasteApi.getWasteInfo(email.toString(), type.toString())
/*
        pat.enqueue(object : Callback<List<PatchApi.PatchResponse>> {
            override fun onResponse(
                call: Call<List<PatchApi.PatchResponse>>,
                response: Response<List<PatchApi.PatchResponse>>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Success !! ")
                    patchInfoList = response.body()
                    processPatchInfoList(patchInfoList)


                } else {
                    // 실패한 경우의 처리
                    Log.e("API", "Failed to get data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PatchApi.PatchResponse>>, t: Throwable) {
                // 실패한 경우의 처리
                Log.e("API", "Error: ${t.message}")
            }
        })*/

        call.enqueue(object : Callback<List<WasteApi.WasteInfo>> {
            override fun onResponse(
                call: Call<List<WasteApi.WasteInfo>>,
                response: Response<List<WasteApi.WasteInfo>>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Success !! ")
                    wasteInfoList = response.body()
                    processWasteInfoList(wasteInfoList)
                } else {
                    // 실패한 경우의 처리
                    Log.e("API", "Failed to get data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<WasteApi.WasteInfo>>, t: Throwable) {
                // 실패한 경우의 처리
                Log.e("API", "Error: ${t.message}")
            }
        })

        binding.imageButton.setOnClickListener() {
            val sintent = Intent(this,ManageActivity::class.java)
            sintent.putExtra("Email",email)
            sintent.putExtra("Type",type)
            startActivity(sintent)
        }
    }

    private fun initRecycler() {
        infoAdapter = InfoAdapter(this)
        binding.rvProfile.adapter = infoAdapter
    }
    private fun processPatchInfoList(patchInfoList: List<PatchApi.PatchResponse>?) {
        var pt : String = ""
        if (patchInfoList != null) {
            // 각각의 WasteInfo에 대한 처리
            patchInfoList?.forEach { patchInfo ->
                val wasteid: Int = patchInfo.wasteId
                val isqueued : Boolean = patchInfo.isQueued
                val isaccepted : Boolean = patchInfo.isAccepted
                val isput : Boolean = patchInfo.isPut
                val istaken : Boolean = patchInfo.isTaken

                if(isqueued == true && isaccepted == false) { pt = "신청승인" }
                else if (isaccepted == true && isput == false){pt = "신청승인"}
                else if (isput == true && istaken == false) {pt = "배치완료"}
                else if (istaken == true) {pt = "수거완료"}
            }
        }
    }
    private fun processWasteInfoList(wasteInfoList: List<WasteApi.WasteInfo>?) {
        var tf : String = ""
        // 데이터를 받아온 후 RecyclerView 초기화
        if (wasteInfoList != null) {
            // 각각의 WasteInfo에 대한 처리
            wasteInfoList?.forEach { wasteInfo ->
                val wasteId : Int = wasteInfo.wasteId
                wasteCode = wasteInfo.wasteCode
                Log.d("Mypage:", "wastecode: ${wasteCode}")
                locationLatitude = wasteInfo.locationLatitude
                locationLongitude = wasteInfo.locationLongitude
                queryDate = wasteInfo.queryDate
                Log.d("Mypage:", "QueryDate: ${queryDate}")
                imageUrl = wasteInfo.imageUrl
                val isQueued: Boolean = wasteInfo.isQueued
                val isAccepted: Boolean = wasteInfo.isAccepted
                val isPut: Boolean = wasteInfo.isPut
                val isTaken: Boolean = wasteInfo.isTaken
                if(isQueued == true && isAccepted == false) { tf = "신청대기"}
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
        infoAdapter = InfoAdapter(this)
        binding.rvProfile.adapter = infoAdapter

        // imageUrl이 null이 아니면 데이터를 추가
        if (imageUrl != null) {
            datas.add(InfoData(img = image, name = address, age = situation, place = day,situate = situ))
            infoAdapter.datas = datas
            infoAdapter.notifyDataSetChanged()
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
                                Log.d(TAG, "Key : ${currentCode}")
                                currentValue = parts[1].trim()
                                Log.d(TAG, "Value : ${currentValue}")
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


/*
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

    }



    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: MainActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = MypageFragment()
    }
}*/