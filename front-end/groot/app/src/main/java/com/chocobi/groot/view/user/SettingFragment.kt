package com.chocobi.groot.view.user

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.chocobi.groot.IntroActivity
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
            logout()
        }

//        회원탈퇴
        val deleteUserText = rootView.findViewById<TextView>(R.id.deleteUserText)
        deleteUserText.setOnClickListener {
            deleteUser()
        }

        return rootView
    }

    private fun changePassword() {
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!

//        service 객체 만들기
        var userService = retrofit.create(UserService::class.java)

//            userService.changePassword(PasswordRequest()).enqueue(object : Callback<BasicResponse> {
//                override fun onResponse(
//                    call: Call<BasicResponse>,
//                    response: Response<BasicResponse>
//                ) {
//
//                    Toast.makeText(requireContext(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
////                    토큰 초기화
//                    initializeAccessToken()
////                    인트로 페이지로 이동
//                    goToIntro()
//                }
//
//                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                    Toast.makeText(requireContext(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
//                }
//            })

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
//                    토큰 초기화
                    initializeAccessToken()
//                    인트로 페이지로 이동
                    goToIntro()
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
            userService.deleteUser(accessToken!!).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    Toast.makeText(requireContext(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
//                    토큰 초기화
                    initializeAccessToken()
//                    인트로 페이지로 이동
                    goToIntro()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "액세스 토큰 없음", Toast.LENGTH_SHORT).show()
        }
    }

//    토큰 초기화
    private fun initializeAccessToken() {
        val shared = requireContext().getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("access_token", "")
        editor.commit()
    }

//    인트로 페이지 이동
    private fun goToIntro() {
        val intent = Intent(requireContext(), IntroActivity::class.java)
        startActivity(intent)
    }
}