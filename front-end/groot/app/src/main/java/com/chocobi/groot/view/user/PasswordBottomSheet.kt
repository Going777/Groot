package com.chocobi.groot.view.user


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.user.model.PasswordRequest
import com.chocobi.groot.view.user.model.UserService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordBottomSheet(context: Context) : BottomSheetDialogFragment() {

    private var msg: String? = "비밀번호를 다시 확인해주세요."
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_password, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.PwSubmitBtn)?.setOnClickListener {
            var pwText = view?.findViewById<EditText>(R.id.pwText)?.text.toString()
            var newPwText = view?.findViewById<EditText>(R.id.newPwText)?.text.toString()
            var newPwText2 = view?.findViewById<EditText>(R.id.newPwText2)?.text.toString()

            if (pwText != "" && newPwText != "" && pwText != newPwText && newPwText == newPwText2) {
                changePassword(pwText, newPwText)
            } else {
                msg = "비밀번호를 다시 확인해주세요."
            }
            var dialog = AlertDialog.Builder(
                requireContext(),
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            )
            dialog.setTitle("비밀번호 변경")
            dialog.setMessage(msg)
            dialog.setPositiveButton(
                "확인",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()

            if (msg == "비밀번호를 변경하였습니다.") {
                dismiss()
            }

        }
    }


    private fun changePassword(pw: String, newPw: String) {
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!

//        service 객체 만들기
        var userService = retrofit.create(UserService::class.java)

        userService.changePassword(PasswordRequest(UserData.getUserPK(), pw, newPw))
            .enqueue(object :
                Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    var pwMsg = response.body()?.msg
                    if (pwMsg == null) {
                        try {
                            JSONObject(response.errorBody()?.string()).let { json ->
                                pwMsg = json.getString("msg")
                            }
                        } catch (e: JSONException) {
                            // 예외 처리: msg 속성이 존재하지 않는 경우
                            pwMsg = "비밀번호를 다시 확인해주세요."
                            e.printStackTrace()
                        }
                    }
                    msg = pwMsg
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }
}