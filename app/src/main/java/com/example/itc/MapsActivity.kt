package com.example.itc

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import api.ApiService
import api.CodeInstance
import api.CodeInstance.codeapi
import api.ImageInstance
import api.RetrofitInstance
import com.example.itc.databinding.ActivityMapsBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.Locale

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var centerMarker: Marker
    private var isFirstLocationUpdate = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }


        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        centerMarker = Marker()
        centerMarker.position = LatLng(37.5665, 126.9780)

// 위치를 가져오는 비동기적인 작업이 완료되면 실행될 콜백
        mapFragment.getMapAsync(this)
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding.placeButton.setOnClickListener {
            val markerPosition = centerMarker.position
           getAddress(markerPosition.latitude, markerPosition.longitude)

        }


    }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            if (locationSource.onRequestPermissionsResult(
                    requestCode, permissions,
                    grantResults
                )
            ) {
                if (!locationSource.isActivated) { // 권한 거부됨
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                }
                return
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        override fun onMapReady(naverMap: NaverMap) {
            this.naverMap = naverMap
            naverMap.locationSource = locationSource
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
            naverMap.uiSettings.isLocationButtonEnabled = true

            // 더미 마커 추가
            centerMarker.map = naverMap

            // 현위치 버튼 클릭 리스너 설정
            naverMap.addOnLocationChangeListener { location ->
                /*Toast.makeText(
                    this, "${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()*/

                // 더미 마커 위치 업데이트
                if (isFirstLocationUpdate) {
                    // 처음 위치 업데이트
                    centerMarker.position = LatLng(location.latitude, location.longitude)
                    isFirstLocationUpdate = false
                }
            }

            // 지도의 카메라 이동 이벤트 리스너 등록
            naverMap.addOnCameraChangeListener { _, _ ->
                // 더미 마커 위치 업데이트
                centerMarker.position = naverMap.cameraPosition.target
            }

            // 스크롤 제스처가 비활성화된 경우에만 더미 마커 위치 업데이트
            if (!naverMap.uiSettings.isScrollGesturesEnabled) {
                naverMap.addOnCameraIdleListener {
                    // 더미 마커를 현재 중앙 위치로 이동
                    centerMarker.position = naverMap.cameraPosition.target
                }
            }
        }



    fun getRealPathFromURI(contentUri: Uri, context: Context): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
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
                    val reg1 = address[0].adminArea.toString()
                    Log.d("Address","address reg1: ${reg1}")
                    val reg2 = address[0].subLocality.toString()
                    Log.d("Address","address reg2: ${reg2}")
                    val reg3 = address[0].subThoroughfare.toString()
                    Log.d("Address","address reg3: ${reg3}")
                    sendApiRequest(latitude,longitude,reg1,reg2,reg3)
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
                Log.d("address","address reg1: ${reg1}")
                val reg2 = addresses[0].subLocality.toString()
                Log.d("address","address reg2: ${reg2}")
                val reg3 = threeElement.toString()
                Log.d("address","address reg3: ${reg3}")
                sendApiRequest(latitude,longitude,reg1,reg2,reg3)
            }
        }
    }
         private fun sendApiRequest(latitude: Double, longitude: Double, reg1 : String, reg2 : String , reg3 : String) {
             val sintent = intent
             val email = sintent.getStringExtra("Email")
             Log.d("test","This is email : ${email}")
             val type = sintent.getStringExtra("Type")
             val wastecode = sintent.getStringExtra("wasteCode")
             Log.d("test","This is login - type : ${type}")
             Log.d("test","This is login - type : ${wastecode}")
            // Retrofit을 사용하여 서버에 API 요청 보내기
            // (이 부분은 이미 작성된 코드를 사용)
            // RetrofitInstance를 사용하여 ApiService 인터페이스의 구현체 생성
            val apiService = RetrofitInstance.apiService
            // mapUser 메서드 호출
             val call = apiService.mapUser(
                 ApiService.PostResult(
                     wasteCode = wastecode,
                     userEmail = email,
                     loginType = type,
                     locationLatitude = latitude,
                     locationLongitude = longitude,
                     region1depthName = reg1,
                     region2depthName = reg2,
                     region3depthName = reg3
                 )
             )


            call.enqueue(object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: retrofit2.Response<Int>
                ) {
                    val statusCode = response.code() // 응답 코드를 가져옵니다.

                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        sendimage(apiResponse!!)
                        // 서버 응답을 처리합니다.
                    } else {
                        // 서버 응답이 실패한 경우
                        Log.d("log", "Fail with status code: $statusCode")
                    }
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    // 네트워크 요청이 실패한 경우
                    Log.d("log", "Fail: ${t.message}")
                }
            })
        }
    private fun sendimage(code : Int){
        val iuri = intent.data
        Log.d(TAG,iuri.toString())
        val codeUri = Uri.parse(iuri.toString())
        Log.d(TAG,"${getRealPathFromURI(iuri!!,this)}")
        val file = File("${getRealPathFromURI(iuri!!,this)}")
        val mediaType = "image/*".toMediaType()
        val requestFile = file.asRequestBody(mediaType)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val codeapi = CodeInstance.codeapi

        codeapi.CodeSend(code.toString(),body).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // 성공적으로 이미지를 전송한 경우의 처리
                    Toast.makeText(this@MapsActivity, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                    Log.d("test","이미지 전송 성공!!")
                    // 서버로부터의 응답 데이터 활용 예시
                    //val responseBody = response.body()
                        // 이미지 주소값을 받아서 뭐하는 거지 ? 내가 저장할 이유도 없자나 ;;
                    val sintent = intent
                    val email = sintent.getStringExtra("Email")
                    val type = sintent.getStringExtra("Type")
                    Log.d("Intent","Email Price : ${email.toString()}")
                    Log.d("Intent","Type Price : ${type.toString()}")
                    var bintent = Intent(this@MapsActivity , MainActivity::class.java)
                    bintent.putExtra("Email",email)
                    bintent.putExtra("Type",type)
                    startActivity(bintent)

                } else {
                    // 서버 응답이 실패한 경우의 처리
                    Toast.makeText(this@MapsActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"이미지 전송 실패")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 네트워크 요청 실패 시의 처리
                Log.e("testt", "네트워크 요청 실패: ${t.message}", t)
                Toast.makeText(this@MapsActivity, "네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }



        companion object {
            private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
            const val TAG = "Naver Map"
        }
}