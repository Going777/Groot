package com.chocobi.groot.data

import android.app.Application

class UserData : Application() {
    companion object {
        private var id = 0
        private var userId = "groot"
        private var nickName = "Groot"
        private var profile : String? = null
        private var registerDate = 1

        fun getId(): Int {
            return id
        }

        fun setId(idNum: Int) {
            id = idNum
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