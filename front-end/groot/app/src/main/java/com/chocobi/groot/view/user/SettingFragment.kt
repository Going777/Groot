package com.chocobi.groot.view.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.chocobi.groot.view.intro.IntroActivity
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.user.model.PasswordRequest
import com.chocobi.groot.view.user.model.UserService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        // Inflate the layout for this fragment
        val mActivity = activity as MainActivity
        val editProfileText = rootView.findViewById<TextView>(R.id.editProfileText)
        val profileBottomSheet = ProfileBottomSheet(requireContext())
        editProfileText.setOnClickListener {
            profileBottomSheet.show(mActivity.supportFragmentManager, profileBottomSheet.tag)
        }

        val editPasswordText = rootView.findViewById<TextView>(R.id.editPasswordText)
        val bottomSheet = PasswordBottomSheet(requireContext())
        editPasswordText.setOnClickListener {
            bottomSheet.show(mActivity.supportFragmentManager, bottomSheet.tag)
        }

//        로그아웃
        val logoutText = rootView.findViewById<TextView>(R.id.logoutText)
        logoutText.setOnClickListener {
            GlobalVariables.defaultAlertDialog(context = requireContext(), title="로그아웃 알림", message = "접속중인 기기에서 로그아웃 하시겠습니까?", positiveFtn = ::logout, existNegativeBtn = true)
        }

//        회원탈퇴
        val deleteUserText = rootView.findViewById<TextView>(R.id.deleteUserText)
        deleteUserText.setOnClickListener {
            GlobalVariables.defaultAlertDialog(context = requireContext(), title="회원탈퇴 알림", message = "계정을 삭제하면 모든 데이터가 디바이스에서 삭제됩니다.\n\n정말로 회원탈퇴를 진행하시겠습니까?", positiveFtn = ::deleteUser, existNegativeBtn = true)
        }

        return rootView
    }


    private fun logout() {
//        var retrofit = Retrofit.Builder()
//            .baseUrl(GlobalVariables.getBaseUrl())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()

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
//                    var m = response.code()
//                    var b = response.errorBody()?.string()
//
//                    Log.d("SettingFragment", m.toString())
//                    Log.d("SettingFragment", "$b")
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
                    if(response.code() == 200) {
                        GlobalVariables.defaultAlertDialog(context=requireContext(), title = "회원탈퇴 알림", message = "성공적으로 회원탈퇴처리 되었습니다. \n\n그동안 Groot를 이용해주셔서 감사합니다.\n더 좋은 서비스를 제공하기 위해서 열심히 노력하겠습니다.",
                        positiveFtn = ::afterUserRequestSuccess)
                    }
//                    회원 탈퇴 실패 (없는 아이디 or 없는 토큰)
                    else {
                        GlobalVariables.defaultAlertDialog(context=requireContext(), title = "회원탈퇴 알림", message = "회원탈퇴 처리에 실패했습니다.",
                            positiveFtn = ::afterUserRequestSuccess)
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(context=requireContext(), title = "회원탈퇴 알림", message = "회원탈퇴 처리에 실패했습니다.",
                        positiveFtn = ::afterUserRequestSuccess)
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