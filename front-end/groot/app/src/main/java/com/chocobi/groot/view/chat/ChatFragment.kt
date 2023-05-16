package com.chocobi.groot.view.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chocobi.groot.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String

    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        Log.d("받아온", arguments.toString())

        val userPK = arguments?.getString("userPK")
        val nickName = arguments?.getString("nickName")
        val profile = arguments?.getString("profile")
        val roomId = arguments?.getString("roomId")

        Log.d("받아온 데이터", userPK.toString())
        Log.d("받아온 데이터", nickName.toString())
        Log.d("받아온 데이터", profile.toString())
        Log.d("받아온 데이터", roomId.toString())


        mDbRef = Firebase.database.reference








        return view
    }

}