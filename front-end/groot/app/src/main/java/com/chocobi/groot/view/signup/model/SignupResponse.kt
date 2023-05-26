package com.chocobi.groot.view.signup


data class SignupResponse(
    var result : String,
    var msg : String,
    var accessToken : String,
    var refreshToken : String,
    var userPK: String,
)

data class DupIdResponse(
    var result : String,
    var msg : String,
)

data class DupNameResponse(
    var result : String,
    var msg : String,
)
