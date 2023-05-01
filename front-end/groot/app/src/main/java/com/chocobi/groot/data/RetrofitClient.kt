package com.chocobi.groot.data

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {





        // Request Header 추가
        val request = chain.request()
        request.newBuilder().addHeader("content-type", "charset=UTF-8").build()
        request.newBuilder().addHeader("Authorization", "data").build()

        // Response 값 처리
        val response = chain.proceed(request)

        // 데이터의 정상 여부에 따라 결과를 변환하여 처리
        return response
    }
}