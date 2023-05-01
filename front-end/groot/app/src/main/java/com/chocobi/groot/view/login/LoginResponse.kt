package com.chocobi.groot.view.login

// output을 만든다 : response

data class LoginResponse(
    var accessToken : String,
    var refreshToken : String,
    var result : String,
    var msg : String,
//    var user: User
)

//data class User (
//    var pk :Number,
//    var username:String,
//    var email:String,
//    var first_name:String,
//    var last_name:String
//)