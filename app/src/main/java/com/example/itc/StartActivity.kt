package com.example.itc

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.itc.databinding.ActivityStartBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.*
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import androidx.viewpager2.widget.ViewPager2
import api.ApiService
import api.LoginApi
import api.LoginInstance
import api.RetrofitInstance
import com.example.itc.databinding.ActivityMainBinding
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call


class StartActivity : AppCompatActivity() {
    var type: String? = null
    var email: String?  = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this,CameraActivity::class.java)

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}")
                val email = user.kakaoAccount?.email.toString()
                intent.putExtra("Email",email)
                intent.putExtra("Type","kakao")
                startActivity(intent)
            }
        }
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                type = "naver"
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                val tvAccessToken = NaverIdLoginSDK.getAccessToken()
                val tvRefreshToken  = NaverIdLoginSDK.getRefreshToken()
                val tvExpires  = NaverIdLoginSDK.getExpiresAt().toString()
                val tvType = NaverIdLoginSDK.getTokenType()
                val tvState = NaverIdLoginSDK.getState().toString()
                val loginType = tvAccessToken.toString()
                val intent = Intent(this@StartActivity , CameraActivity::class.java)
                //naverch(loginType)
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        email = result.profile?.email.toString()
                        Log.e(TAG, "네이버 로그인한 유저 정보 - 이메일 : $email")
                        intent.putExtra("Email",email)
                        intent.putExtra("Type",type.toString())
                        sendApiRequest(email!!,"naver",loginType)
                        startActivity(intent)
                    }

                    override fun onError(errorCode: Int, message: String) {
                        TODO("Not yet implemented")
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@StartActivity,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }  //
        }


        binding.KakaoButton.setOnClickListener {
            KaKaoLogin()
        }
        binding.NaverButton.setOnClickListener{
            naverLogin()
        }
    }

    private fun KaKaoLogin() {
        type = "kakao"
        val intent = Intent(this,CameraActivity::class.java)


        fun kakaoch(accessToken : String){
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                }
                else if (user != null) {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}")
                    email = user.kakaoAccount?.email.toString()
                    intent.putExtra("Email",email)
                    intent.putExtra("Type",type)
                    sendApiRequest(email!!,type!!,accessToken)
                    startActivity(intent)
                }
            }
        }
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Log.e(TAG, "토큰 정보 보기 실패", error)
                    finish()
                } else if (tokenInfo != null) {
                    Log.i(
                        TAG, "토큰 정보 보기 성공" +
                                "\n회원번호: ${tokenInfo.id}" +
                                "\n만료시간: ${tokenInfo.expiresIn} 초"
                    )
                    kakaoch(accessToken.toString())
                }
            }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->

            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                val toke = token.accessToken.toString()
                kakaoch(toke)
            }
        }


            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    kakaoch(accessToken.toString())
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
        /*
                UserApiClient.instance.loginWithKakaoAccount(
                    this,
                    prompts = listOf(Prompt.SELECT_ACCOUNT)
                ) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "로그인 실패", error)
                    } else if (token != null) {
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        /*val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))*/
                    }
                }*/

/*
        val properties = mapOf("CUSTOM_PROPERTY_KEY" to "CUSTOM_PROPERTY_VALUE")

        UserApiClient.instance.updateProfile(properties) { error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 저장 실패", error)
            } else {
                Log.i(TAG, "사용자 정보 저장 성공")
            }
        }

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i(
                    TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}"
                )
                var login_type = "kakao"
                var email = user.kakaoAccount?.email
                var nickname = user.kakaoAccount?.profile?.nickname

            }
        }*/
    }
    private fun sendApiRequest(userEmail : String,logintype : String , accessToken : String) {
        // Retrofit을 사용하여 서버에 API 요청 보내기
        // (이 부분은 이미 작성된 코드를 사용)
        // RetrofitInstance를 사용하여 ApiService 인터페이스의 구현체 생성
        val apiServices = LoginInstance.apiServices
        val intent =  Intent(this,CameraActivity::class.java)


        // mapUser 메서드 호출
        val call = apiServices.LoginUser(
            LoginApi.PostResults(
                userEmail = userEmail,
                loginType = logintype,
                accessToken =  accessToken
            )
        )


        call.enqueue(object : retrofit2.Callback<LoginApi.LoginResponse> {
            override fun onResponse(
                call: Call<LoginApi.LoginResponse>,
                response: retrofit2.Response<LoginApi.LoginResponse>
            ) {
                val statusCode = response.code() // 응답 코드를 가져옵니다.

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    // 서버 응답을 처리합니다.
                } else {
                    // 서버 응답이 실패한 경우
                    Log.d("log", "Fail with status code: $statusCode")
                }
            }

            override fun onFailure(call: Call<LoginApi.LoginResponse>, t: Throwable) {
                // 네트워크 요청이 실패한 경우
                Log.d("log", "Fail: ${t.message}")
            }
        })
    }
    private fun naverLogin(){
        val intent = Intent(this@StartActivity , CameraActivity::class.java)
        fun naverch(accessToken: String){
            val type = "naver"
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(result: NidProfileResponse) {
                    val email = result.profile?.email.toString()
                    Log.e(TAG, "네이버 로그인한 유저 정보 - 이메일 : $email")
                    intent.putExtra("Email",email)
                    intent.putExtra("Type",type.toString())
                    sendApiRequest(email,type,accessToken)
                    startActivity(intent)
                }

                override fun onError(errorCode: Int, message: String) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    TODO("Not yet implemented")
                }
            })

        }


        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                val tvAccessToken = NaverIdLoginSDK.getAccessToken()
                val tvRefreshToken  = NaverIdLoginSDK.getRefreshToken()
                val tvExpires  = NaverIdLoginSDK.getExpiresAt().toString()
                val tvType = NaverIdLoginSDK.getTokenType()
                val tvState = NaverIdLoginSDK.getState().toString()
                val loginType = tvAccessToken.toString()
                val intent = Intent(this@StartActivity , CameraActivity::class.java)
                naverch(loginType)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@StartActivity,"errorCode:$errorCode, errorDesc:$errorDescription",Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }  //
        }


        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }
}