package com.chocobi.groot

// output을 만든다
//data class Login(
//    var result : String,
//    var msg: String
//)

//test output : response
data class Login(
    var access_token : String,
    var refresh_token : String,
    var user: User
)

data class User (
    var pk :Number,
    var username:String,
    var email:String,
    var first_name:String,
    var last_name:String
)