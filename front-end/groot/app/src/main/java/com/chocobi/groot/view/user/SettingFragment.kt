package com.chocobi.groot.view.user

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chocobi.groot.view.intro.IntroActivity
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.user.model.NotiStatusRequest
import com.chocobi.groot.view.user.model.UserService
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingFragment : Fragment() {

    private lateinit var isSocialLogined: String
    private var isNoti1: Boolean = false
    private var isNoti2: Boolean = false
    private var isNoti3: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSocialLogined = UserData.getIsSocialLogined()

        arguments?.let {

        }
        val notificationList = UserData.getIsNotificationAllowed()
        isNoti1 = notificationList[0]
        isNoti2 = notificationList[1]
        isNoti3 = notificationList[2]
        Log.d("SettingFragment", "onCreate() 처음 알람 ${isNoti1} ${isNoti2} $isNoti3")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        // Inflate the layout for this fragment
        val mActivity = activity as MainActivity

        val categoryNameTextView = rootView.findViewById<TextView>(R.id.categoryName)
        val categoryIcon = rootView.findViewById<ImageView>(R.id.categoryIcon)
        categoryNameTextView.text = "설정"
        categoryIcon.setImageResource(R.drawable.ic_setting)

//        ================================================================
//        뒤로 가기 버튼
        val backBtn = rootView.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
//        ================================================================


        val infoText = rootView.findViewById<ConstraintLayout>(R.id.infoText)
        val infoSection = rootView.findViewById<LinearLayout>(R.id.infoSection)
        infoText.setOnClickListener {
            if (infoSection.visibility == View.GONE) {
                infoSection.visibility = View.VISIBLE
            } else {
                infoSection.visibility = View.GONE
            }
        }
        val editProfileText = rootView.findViewById<TextView>(R.id.editProfileText)
        val profileBottomSheet = ProfileBottomSheet(requireContext())
        editProfileText.setOnClickListener {
            profileBottomSheet.show(mActivity.supportFragmentManager, profileBottomSheet.tag)
        }

        val editPasswordText = rootView.findViewById<TextView>(R.id.editPasswordText)
        Log.d("SettingFragment", "onCreateView() 확인 ${isSocialLogined}")
        if (isSocialLogined != "") {
            editPasswordText.visibility = View.GONE
        } else {
            val bottomSheet = PasswordBottomSheet(requireContext())
            editPasswordText.setOnClickListener {
                bottomSheet.show(mActivity.supportFragmentManager, bottomSheet.tag)
            }
        }

//        로그아웃
        val logoutText = rootView.findViewById<TextView>(R.id.logoutText)
        logoutText.setOnClickListener {
            GlobalVariables.defaultAlertDialog(
                context = requireContext(),
                title = "로그아웃 알림",
                message = "접속중인 기기에서 로그아웃 하시겠습니까?",
                positiveFtn = ::logout,
                existNegativeBtn = true
            )
        }

//        회원탈퇴
        val deleteUserText = rootView.findViewById<TextView>(R.id.deleteUserText)
        deleteUserText.setOnClickListener {
            GlobalVariables.defaultAlertDialog(
                context = requireContext(),
                title = "회원탈퇴 알림",
                message = "계정을 삭제하면 모든 데이터가 디바이스에서 삭제됩니다.\n\n정말로 회원탈퇴를 진행하시겠습니까?",
                positiveFtn = ::deleteUser,
                existNegativeBtn = true
            )
        }

        controlNotification(rootView)
        return rootView
    }

    private fun controlNotification(view: View) {
        val mActivity = activity as MainActivity
        val notificationManager =
            requireContext().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val onOff1Btn = view.findViewById<LinearLayout>(R.id.onOff1Btn)
        val onOff2Btn = view.findViewById<LinearLayout>(R.id.onOff2Btn)
        val onOff3Btn = view.findViewById<LinearLayout>(R.id.onOff3Btn)
        val onOff1Image = view.findViewById<ImageView>(R.id.onOff1Image)
        val onOff2Image = view.findViewById<ImageView>(R.id.onOff2Image)
        val onOff3Image = view.findViewById<ImageView>(R.id.onOff3Image)
        val onOff1Text = view.findViewById<TextView>(R.id.onOff1Text)
        val onOff2Text = view.findViewById<TextView>(R.id.onOff2Text)
        val onOff3Text = view.findViewById<TextView>(R.id.onOff3Text)

//        알림 설정 거부되어 있으면 모두 OFF
        if (!isNoti1) {
            switchOnOff(1, onOff1Image, onOff1Text, false)
        }
        if (!isNoti2) {
            switchOnOff(2, onOff2Image, onOff2Text, false)
        }
        if (!isNoti3) {
            switchOnOff(3, onOff3Image, onOff3Text, false)
        }

        onOff1Btn.setOnClickListener {
            // 알림 설정
            if (!notificationManager.areNotificationsEnabled()) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setMessage("원활한 식물 리마인더를 위해 알림 권한을 허용해주세요.")
                dialog.setPositiveButton("확인") { dialog, which ->
                    mActivity.openAppNotificationSettings()
                }
                dialog.setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                switchOnOff(1, onOff1Image, onOff1Text, !isNoti1)
                requestChangeNotiStatus()
            }
        }
        onOff2Btn.setOnClickListener {
            // 알림 설정
            if (!notificationManager.areNotificationsEnabled()) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setMessage("원활한 식물 리마인더를 위해 알림 권한을 허용해주세요.")
                dialog.setPositiveButton("확인") { dialog, which ->
                    mActivity.openAppNotificationSettings()
                }
                dialog.setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                switchOnOff(2, onOff2Image, onOff2Text, !isNoti2)
                requestChangeNotiStatus()
            }
        }
        onOff3Btn.setOnClickListener {
            // 알림 설정
            if (!notificationManager.areNotificationsEnabled()) {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setMessage("원활한 식물 리마인더를 위해 알림 권한을 허용해주세요.")
                dialog.setPositiveButton("확인") { dialog, which ->
                    mActivity.openAppNotificationSettings()
                }
                dialog.setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                switchOnOff(3, onOff3Image, onOff3Text, !isNoti3)
                requestChangeNotiStatus()
            }
        }
    }

    private fun switchOnOff(type: Int, icon: ImageView, text: TextView, option: Boolean) {
        if (option) {
            icon.setImageResource(R.drawable.ic_noti_on)
            text.setText("ON")
            UserData.setIsNotificationAllowed(type, true)
        } else {
            icon.setImageResource(R.drawable.ic_noti_off)
            text.setText("OFF")
            UserData.setIsNotificationAllowed(type, false)
            isNoti2 = option
        }
        when (type) {
            1 -> isNoti1 = option
            2 -> isNoti2 = option
            3 -> isNoti3 = option
        }
    }

    private fun requestChangeNotiStatus() {
        val retrofit = RetrofitClient.getClient()!!
        val userService = retrofit.create(UserService::class.java)
        Log.d(
            "SettingFragment",
            "requestChangeNotiStatus() 요청가는 값 ${isNoti1} ${isNoti2} ${isNoti3}"
        )

        userService.changeNotiStatus(NotiStatusRequest(isNoti1, isNoti2, isNoti3))
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        val body = response.body()
                        Log.d("SettingFragment", "onResponse() 성공222222222 ${body?.msg}")
                    } else {
                        val body = response.body()
                        Log.d("SettingFragment", "onResponse() 실패 ${body?.msg}")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                }

            })
    }

    private fun logout() {
//        retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!

//        service 객체 만들기
        var userService = retrofit.create(UserService::class.java)

//        요청 보내기
        val accessToken = GlobalVariables.prefs.getString("access_token", "")
        if (accessToken != "") {
            userService.logout().enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    val isSocialLogined = UserData.getIsSocialLogined()
                    if (isSocialLogined == "kakao") {
                        Log.d("SettingFragment", "onResponse() 카카오 로그아웃")
                        // 로그아웃
                        UserApiClient.instance.logout { error ->
                            if (error != null) {
                                Log.e("Logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                            } else {
                                Log.i("Logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                            }
                        }
                    } else if (isSocialLogined == "naver") {
                        Log.d("SettingFragment", "onResponse() 네이버 로그아웃")
                        NaverIdLoginSDK.logout()
                    }
                    Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    afterUserRequestSuccess()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "액세스 토큰 없음", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteUser() {
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!

//        service 객체 만들기
        var userService = retrofit.create(UserService::class.java)

//        요청 보내기
        val accessToken = GlobalVariables.prefs.getString("access_token", "")
        if (accessToken != "") {
            userService.deleteUser().enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
//                    회원 탈퇴 성공
                    if (response.code() == 200) {
                        val isSocialLogined = UserData.getIsSocialLogined()
                        if (isSocialLogined == "kakao") {
                            Log.d("SettingFragment", "onResponse() 카카오 회원 탈퇴")
                            // 연결 끊기
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    Log.e("Logout", "연결 끊기 실패", error)
                                } else {
                                    Log.i("Logout", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                                }
                            }
                        } else if (isSocialLogined == "naver") {
                            Log.d("SettingFragment", "onResponse() 네이버 회원 탈퇴")
                            context?.let {
                                NidOAuthLogin().callDeleteTokenApi(it, object :
                                    OAuthLoginCallback {
                                    override fun onSuccess() {
                                        //서버에서 토큰 삭제에 성공한 상태입니다.
                                    }

                                    override fun onFailure(httpStatus: Int, message: String) {
                                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                                    }

                                    override fun onError(errorCode: Int, message: String) {
                                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                                        onFailure(errorCode, message)
                                    }
                                })
                            }
                        }
                        GlobalVariables.defaultAlertDialog(
                            context = requireContext(),
                            title = "회원탈퇴 알림",
                            message = "성공적으로 회원탈퇴처리 되었습니다. \n\n그동안 Groot를 이용해주셔서 감사합니다.\n더 좋은 서비스를 제공하기 위해서 열심히 노력하겠습니다.",
                            positiveFtn = ::afterUserRequestSuccess
                        )
                    }
//                    회원 탈퇴 실패 (없는 아이디 or 없는 토큰)
                    else {
                        GlobalVariables.defaultAlertDialog(
                            context = requireContext(),
                            title = "회원탈퇴 알림",
                            message = "회원탈퇴 처리에 실패했습니다.",
                            positiveFtn = ::afterUserRequestSuccess
                        )
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(
                        context = requireContext(), title = "회원탈퇴 알림", message = "회원탈퇴 처리에 실패했습니다.",
                        positiveFtn = ::afterUserRequestSuccess
                    )
                }
            })
        } else {
            Toast.makeText(requireContext(), "액세스 토큰 없음", Toast.LENGTH_SHORT).show()
        }
    }

    private fun afterUserRequestSuccess() {

        initializeAccessToken()
        goToIntro()
    }

    //    토큰 초기화
    private fun initializeAccessToken() {
        val shared = requireContext().getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("access_token", "")
        editor.putString("refresh_token", "")
        editor.commit()
    }

    //    인트로 페이지 이동
    private fun goToIntro() {
        val intent = Intent(requireContext(), IntroActivity::class.java)
        startActivity(intent)
    }
}