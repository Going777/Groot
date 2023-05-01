package com.chocobi.groot.view.login

// output을 만든다 : response

data class LoginResponse(
    var accessToken : String,
    var refreshToken : String,
    var result : String,
    var msg : String,
)

