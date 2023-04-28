package com.chocobi.groot.view.signup

// output을 만든다 : response

data class SignupResponse(
    var result : String,
    var msg : String,
    var accessToken : String,
    var refreshToken : String
)

data class DupIdResponse(
    var result : String,
    var msg : String,
)

data class DupNameResponse(
    var result : String,
    var msg : String,
)
