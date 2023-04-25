package com.chocobi.groot

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.chocobi.groot.databinding.ActivityMainBinding
import com.chocobi.groot.view.community.CommunityFragment
import com.chocobi.groot.view.community.CommunityPostFragment
import com.chocobi.groot.view.community.CommunityShareFragment
import com.chocobi.groot.view.plant.PlantDiaryFragment
import com.chocobi.groot.view.plant.PlantFragment
import com.chocobi.groot.view.search.SearchCameraActivity
import com.chocobi.groot.view.search.SearchDetailFragment
import com.chocobi.groot.view.search.SearchFragment
import com.chocobi.groot.view.user.SettingFragment
import com.chocobi.groot.view.user.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    //    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_CAMERA = 0
    private val REQUEST_CAMERA = 1

    //        fragment 조작
    fun changeFragment(index: String) {
        when (index) {
            "plant_diary" -> {
                val plantDiaryFragment = PlantDiaryFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, plantDiaryFragment)
                    .commit()
            }

            "search" -> {
                val searchFragment = SearchFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, searchFragment)
                    .commit()
            }

            "search_detail" -> {
                Log.d("MainActivity", "search detail 호출")
                val searchDetailFragment = SearchDetailFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, searchDetailFragment)
                    .commit()
            }

            "community_share" -> {
                val communityShareFragment = CommunityShareFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, communityShareFragment)
                    .commit()
            }

            "community_post" -> {
                val communityPostFragment = CommunityPostFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, communityPostFragment)
                    .commit()
            }

            "setting" -> {
                val settingFragment = SettingFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, settingFragment)
                    .commit()
            }
        }
    }

    //    camera 조작
    /**자식 액티비티에서 권한 요청 시 직접 호출하는 메서드
     * @param permissions 권한 처리를 할 권한 목록
     * @param requestCode 권한을 요청한 주체가 어떤 것인지 구분하기 위함.
     * */
    private var realUri: Uri? = null
    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        Log.d("MainActivity", "권한 요청")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionGranted(requestCode)
        } else {
            // isAllPermissionsGranted : 권한이 모두 승인 되었는지 여부 저장
            // all 메서드를 사용하면 배열 속에 들어 있는 모든 값을 체크할 수 있다.
            val isAllPermissionsGranted =
                permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            if (isAllPermissionsGranted) {
                permissionGranted(requestCode)
            } else {
                // 사용자에 권한 승인 요청
                ActivityCompat.requestPermissions(this, permissions, requestCode)
            }
        }
    }


    /** 사용자가 권한을 승인하거나 거부한 다음에 호출되는 메서드
     * @param requestCode 요청한 주체를 확인하는 코드
     * @param permissions 요청한 권한 목록
     * @param grantResults 권한 목록에 대한 승인/미승인 값, 권한 목록의 개수와 같은 수의 결괏값이 전달된다.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
        }
    }

    private fun permissionGranted(requestCode: Int) {

        when (requestCode) {
            PERMISSION_CAMERA -> openCamera()
        }
    }

    private fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            PERMISSION_CAMERA -> Toast.makeText(
                this,
                "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        createImageUri(newFileName(), "image/jpg")?.let { uri: Uri ->
            Log.d("MainActivity", uri.toString())
            realUri = uri
            // MediaStore.EXTRA_OUTPUT을 Key로 하여 Uri를 넘겨주면
            // 일반적인 Camera App은 이를 받아 내가 지정한 경로에 사진을 찍어서 저장시킨다.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    private fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        Log.d("MainActivity", "newFileName")
        return "$filename.jpg"
    }

    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        Log.d("MainActivity", "createImageUri")
        return this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    /** 카메라 및 앨범 Intent 결과
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            Log.d("MainActivity", "onActivityResult")
            when (requestCode) {
                REQUEST_CAMERA -> {
                    realUri?.let { uri ->
                        val intent = Intent(this, SearchCameraActivity::class.java)
                        intent.putExtra("imageUri", uri.toString())
                        Log.d("MainActivity", "uri:" + uri.toString())
                        startActivity(intent)
                    }
                }
            }
        }
    }

//    ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        네비게이션 바 조작
        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        var bnv_main = findViewById(R.id.bottom_navigation) as BottomNavigationView

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.plantFragment -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val homeFragment = PlantFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, homeFragment).commit()
                    }

                    R.id.searchFragment -> {
                        val boardFragment = SearchFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
                    }

                    R.id.communityFragment -> {
                        val boardFragment = CommunityFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
                    }

                    R.id.userFragment -> {
                        val boardFragment = UserFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
                    }
                }
                true
            }
//            selectedItemId = R.id.plantFragment
//            1차 릴리즈 : search를 메인으로
            selectedItemId = R.id.searchFragment
        }

        //        특정 프레그먼트로 이동
        var toPage = intent.getStringExtra("toPage")
        Log.d("MainActivity", "onCreate")
        if (toPage == "search_detail") {
            Log.d("MainActivity", "toPage" + toPage)
            bnv_main.run { selectedItemId = R.id.searchFragment }
            changeFragment(toPage)
        }
    }


}


