package com.chocobi.groot.data

import android.app.Application

class UserData : Application() {
    companion object {
        private var userPK = 0
        private var userId = "User Id"
        private var nickName = "User Nickname"
        private var profile : String? = null
        private var registerDate = 0
        private var firebaseToken = ""
        private var socialLoginCategory: String = ""
        private var isNoti1: Boolean = true
        private var isNoti2: Boolean = true
        private var isNoti3: Boolean = true

        fun getUserFirebase() : String {
            return firebaseToken
        }

        fun setUserFirebase(token: String) {
            firebaseToken = token
        }

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

        fun getIsSocialLogined(): String {
            return socialLoginCategory
        }

        fun setIsSocialLogined(category: String) {
            socialLoginCategory = category
        }

        fun getIsNotificationAllowed(): ArrayList<Boolean> {
            val notificationList = ArrayList<Boolean>()
            notificationList.add(isNoti1)
            notificationList.add(isNoti2)
            notificationList.add(isNoti3)
            return notificationList
        }

        fun setIsNotificationAllowed(type: Int, option: Boolean) {
            when (type) {
                1 -> isNoti1 = option
                2 -> isNoti2 = option
                3 -> isNoti3 = option
            }
        }
    }
}