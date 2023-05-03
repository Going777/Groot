package com.chocobi.groot.data

import android.app.Application

class UserData : Application() {
    companion object {
        private var userPK = 0
        private var userId = "User Id"
        private var nickName = "User Nickname"
        private var profile : String? = null
        private var registerDate = 0

        fun getUserPK(): Int {
            return userPK
        }

        fun setUserPK(idNum: Int) {
            userPK = idNum
        }

        fun getUserId(): String {
            return userId
        }

        fun setUserId(userIdString: String) {
            userId = userIdString
        }

        fun getNickName(): String {
            return nickName
        }

        fun setNickName(userNickNameString: String) {
            nickName = userNickNameString
        }

        fun getProfile(): String? {
            return profile
        }

        fun setProfile(userProfileString: String?) {
            profile = userProfileString
        }

        fun getRegisterDate(): Int {
            return registerDate
        }

        fun setRegisterDate(registerDateNum: Int) {
            registerDate = registerDateNum
        }
    }
}