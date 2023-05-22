package com.chocobi.groot.view.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.main.Character
import com.chocobi.groot.view.user.adapter.CollectionRVAdapter
import com.chocobi.groot.view.user.model.CollectionResponse
import com.chocobi.groot.view.user.model.UserService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class CollectionActivity : AppCompatActivity() {
    private var characters = mutableListOf<Character>()
    private var positions = mutableListOf<Int>()

    private lateinit var collectionRV: RecyclerView
    private lateinit var rvAdapter: CollectionRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)


        val categoryNameTextView = findViewById<TextView>(R.id.categoryName)
        val categoryIcon = findViewById<ImageView>(R.id.categoryIcon)
        categoryNameTextView.text = "콜렉션북"
        categoryIcon.setImageResource(R.drawable.ic_collection)

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            this.onBackPressed()
        }
//        ================================================================
//        ================================================================

        getCharacterData()
        getCollectionData()

        findView()

    }

    private fun findView() {
        collectionRV = findViewById(R.id.collectionRV)
        collectionRV.layoutManager = GridLayoutManager(this, 3)

    }

    private fun getCharacterData() {
        val charactersString = GlobalVariables.prefs.getString("characters", "")
        val jsonArray = JSONArray()
        val regex = Regex("Character\\(grwType=(.*?), level=(.*?), pngPath=(.*?)\\)")
        val matches = regex.findAll(charactersString)
        for (match in matches) {
            val grwType = match.groupValues[1]
            val level = match.groupValues[2].toInt()
            val pngPath = match.groupValues[3]

            val characterObject = JSONObject()
            characterObject.put("grwType", grwType)
            characterObject.put("level", level)
            characterObject.put("pngPath", pngPath)
            jsonArray.put(characterObject)
        }

        // JSON 배열을 다시 문자열로 변환
        val jsonString = jsonArray.toString()

        // jsonString을 파싱하여 사용
        try {
            val json = JSONArray(jsonString)

            for (i in 0 until json.length()) {
                val characterObject = json.getJSONObject(i)
                val grwType = characterObject.getString("grwType")
                val level = characterObject.getInt("level")
                val pngPath = characterObject.getString("pngPath")

                val character = Character(grwType, level, pngPath)
                characters.add(character)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getCollectionData() {
        val retrofit = RetrofitClient.getClient()!!
        val userService = retrofit.create(UserService::class.java)

        userService.getCollections().enqueue(object: retrofit2.Callback<CollectionResponse>{
            override fun onResponse(
                call: Call<CollectionResponse>,
                response: Response<CollectionResponse>
            ) {
                if (response.code() == 200) {
                    val body = response.body()
                    if(body != null) {
                        positions = body.positions
                        rvAdapter = CollectionRVAdapter(characters, positions)
                        collectionRV.adapter = rvAdapter
                    }
                }
            }

            override fun onFailure(call: Call<CollectionResponse>, t: Throwable) {
            }

        })
    }
}

