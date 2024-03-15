package com.example.itc

import android.app.Application
import android.os.Bundle
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class socialApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.initialize(this, "uiSZHhgKCsMAUexpoDfY", "wCqsDtq_Cm", "ITC")
        KakaoSdk.init(this, "ddbb22dc00845a1d661212c964be0a81")
    }


}