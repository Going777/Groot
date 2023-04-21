package com.chocobi.groot.view.login

// output을 만든다 : response

data class Login(
    var accessToken : String,
    var result : String,
    var msg : String,
//    var refreshToken : String,
//    var user: User
)

//data class User (
//    var pk :Number,
//    var username:String,
//    var email:String,
//    var first_name:String,
//    var last_name:String
//)