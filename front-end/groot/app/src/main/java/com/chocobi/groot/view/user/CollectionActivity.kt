package com.chocobi.groot.view.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Scene
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.main.Character
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.user.adapter.CollectionRVAdapter
import com.chocobi.groot.view.user.model.CharacterGlbResponse
import com.chocobi.groot.view.user.model.CollectionResponse
import com.chocobi.groot.view.user.model.UserService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class CollectionActivity : AppCompatActivity() {
    private var characters = mutableListOf<Character>()
    private var positions = mutableListOf<Int>()
    private var targetGlbPath: String? = null
    private var targetGrwType: String? = null
    private var modelNode: ModelNode? = null

    private lateinit var collectionRV: RecyclerView
    private lateinit var rvAdapter: CollectionRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)


        val categoryNameTextView = findViewById<TextView>(R.id.categoryName)
        val categoryIcon = findViewById<ImageView>(R.id.categoryIcon)
        categoryNameTextView.text = "컬렉션북"
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
        findView()
        getCollectionData()
    }

    private fun findView() {
        collectionRV = findViewById(R.id.collectionRV)
        rvAdapter = CollectionRVAdapter(characters, positions)
        rvAdapter.itemClick = object : CollectionRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int, grwType: String, level: Int, isContain: Boolean) {
                // 클릭 이벤트 처리 로직 작성
                if (isContain) {
                    getCharacterGlbPath(position, grwType, level)
                } else {
                    Toast.makeText(this@CollectionActivity, "보유하지 않은 캐릭터입니다.", Toast.LENGTH_SHORT).show()
                }
                Log.d("CollectionActivity","onClick() 캐릭터 $position")
            }
        }
        collectionRV.layoutManager = GridLayoutManager(this, 3)
        collectionRV.adapter = rvAdapter
    }

    private fun getCharacterData() {
        val charactersString = GlobalVariables.prefs.getString("characters", "")
        val jsonArray = JSONArray()
        val regex = Regex("Character\\(grwType=(.*?), level=(.*?), pngPath=(.*?), greyPath=(.*?)\\)")
        val matches = regex.findAll(charactersString)
        for (match in matches) {
            val grwType = match.groupValues[1]
            val level = match.groupValues[2].toInt()
            val pngPath = match.groupValues[3]
            val greyPath = match.groupValues[4]

            val characterObject = JSONObject()
            characterObject.put("grwType", grwType)
            characterObject.put("level", level)
            characterObject.put("pngPath", pngPath)
            characterObject.put("greyPath", greyPath)
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
                val greyPath = characterObject.getString("greyPath")
                val character = Character(grwType, level, pngPath, greyPath)
                characters.add(character)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getCollectionData() {
        val retrofit = RetrofitClient.getClient()!!
        val userService = retrofit.create(UserService::class.java)
        userService.getCollections().enqueue(object : retrofit2.Callback<CollectionResponse> {
            override fun onResponse(
                call: Call<CollectionResponse>,
                response: Response<CollectionResponse>
            ) {
                if (response.code() == 200) {
                    val body = response.body()
                    if (body != null) {
                        positions = body.positions
                        rvAdapter.updatePositions(positions)
                        rvAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<CollectionResponse>, t: Throwable) {
                // 오류 처리
            }
        })
    }

    private fun getCharacterGlbPath(position: Int, grwType: String, level: Int) {
        val retrofit = RetrofitClient.getClient()!!
        val userService = retrofit.create(UserService::class.java)
        userService.getCharacterGlbPath(position)
            .enqueue(object :retrofit2.Callback<CharacterGlbResponse> {
                override fun onResponse(
                    call: Call<CharacterGlbResponse>,
                    response: Response<CharacterGlbResponse>
                ) {
                    if(response.code() == 200) {
                        val body = response.body()
                        if (body != null) {
                            targetGlbPath = body.glbPath
                            targetGrwType = grwType

//                            val builder = AlertDialog.Builder(this@CollectionActivity)
//                            val dialogView = layoutInflater.inflate(R.layout.character_dialog, null)
//
//                            val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
//                            val characterSceneView = dialogView.findViewById<SceneView>(R.id.characterSceneView)
//                            dialogTitle.text = targetGrwType
//
//                            builder.setView(dialogView)
//                                .setPositiveButton("확인") { dialogInterface, i ->
//                                    dialogInterface.dismiss()
//                                }
//                                .show()
//
//                            setCharacterSceneView(characterSceneView, targetGlbPath!!)
                            val characterCollectionBottomSheet = CharacterCollectionBottomSheet(this@CollectionActivity)
                            characterCollectionBottomSheet.setData(targetGrwType!!, targetGlbPath!!, level)
                            characterCollectionBottomSheet.show(
                                supportFragmentManager,
                                characterCollectionBottomSheet.tag
                            )
                            Log.d("CollectionActivity","onResponse() 캐릭터 ${targetGlbPath} ${targetGrwType}")
                        }
                    }
                }

                override fun onFailure(call: Call<CharacterGlbResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun setCharacterSceneView(characterSceneView: SceneView, glbPath: String) {
        if (modelNode != null) {
            characterSceneView.removeChild(modelNode!!)
        }

        Log.d("CollectionActivity", "setCharacterSceneView() 캐릭터 $glbPath")

        characterSceneView.backgroundColor = Color(255.0f, 255.0f, 255.0f, 0.0f)

        modelNode = ModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = glbPath,
                autoAnimate = false,
                scaleToUnits = 1f,
                centerOrigin = Position(x = 0f, y = 0f, z = 0f),
            ) {
                isScaleEditable = false
                isRotationEditable = false

                // 로드 완료 후 SceneView에 캐릭터 추가
                if (modelNode != null) {
                    Log.d("CollectionActivity", "setCharacterSceneView() 캐릭터 $modelNode")
                    characterSceneView.addChild(modelNode!!)
                }
            }
        }
    }
}

