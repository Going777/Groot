package com.chocobi.groot.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // Request Header 추가
        val request = chain.request()

        val accessToken = GlobalVariables.prefs.getString("accessToken", "") // ViewModel에서 지정한 key로 JWT 토큰을 가져온다.

        request.newBuilder().addHeader("content-type", "charset=UTF-8").build()
        request.newBuilder().addHeader("Authorization", "Bearer "+accessToken).build()

        // Response 값 처리
        val response = chain.proceed(request)

        // 데이터의 정상 여부에 따라 결과를 변환하여 처리
        return response
    }
}