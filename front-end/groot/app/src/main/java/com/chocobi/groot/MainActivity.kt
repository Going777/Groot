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
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_CAMERA
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.view.community.CommunityFragment
import com.chocobi.groot.view.community.CommunityPostFragment
import com.chocobi.groot.view.community.CommunityShareFragment
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.pot.PotDetailFragment
import com.chocobi.groot.view.pot.PotDiaryCreateFragment
import com.chocobi.groot.view.pot.PotDiaryFragment
import com.chocobi.groot.view.pot.PotFragment
import com.chocobi.groot.view.search.SearchCameraActivity
import com.chocobi.groot.view.search.SearchDetailFragment
import com.chocobi.groot.view.search.SearchFragment
import com.chocobi.groot.view.user.SettingFragment
import com.chocobi.groot.view.user.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    //    private lateinit var binding: ActivityMainBinding

    //    private var activityToolbar: androidx.appcompat.widget.Toolbar? = null
//
//    fun getToolbar(): androidx.appcompat.widget.Toolbar? {
//        return activityToolbar
//    }
    private val TAG = "MainActivity"
    private var photoImage: ImageView? = null
    private var potId: Int = 0
    private var potName: String = "화분 이름"
    private var potPlant: String = "화분 식물"
    private var potCharImg: String = "화분 이미지 URL"
    private lateinit var bnv_main: BottomNavigationView

    fun setPotId(id:Int) {
        potId = id
    }

    fun setPotName(name:String) {
        potName = name
    }

    fun setPotPlant(plant:String) {
        potPlant = plant
    }

    fun setPotCharImg(plant:String) {
        potCharImg = plant
    }


    //        fragment 조작
    fun changeFragment(index: String) {
        when (index) {
            "pot" -> {
                bnv_main.run { selectedItemId = R.id.potFragment }
            }

            "pot_diary" -> {
                val potDiaryFragment = PotDiaryFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, potDiaryFragment)
                    .commit()
            }

            "pot_diary_create" -> {
                val bundle = Bundle()
                bundle.putInt("potId", potId)
                bundle.putString("potName", potName)
                bundle.putString("potPlant", potPlant)
                bundle.putString("potCharImg", potCharImg)
                val potDiaryCreateFragment = PotDiaryCreateFragment()
                potDiaryCreateFragment.arguments = bundle

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, potDiaryCreateFragment)
                    .commit()
            }

            "pot_detail" -> {
                val bundle = Bundle()
                bundle.putInt("potId", potId)
                bundle.putString("potName", potName)
                bundle.putString("potPlant", potPlant)
                val potDetailFragment = PotDetailFragment()
                potDetailFragment.arguments = bundle

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fl_container, potDetailFragment)
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
                Log.d(TAG, "search detail 호출")
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
    private var cameraStatus: String? = null
    private var galleryStatus: String? = null

    fun setCameraStatus(status: String) {
        cameraStatus = status
    }

    fun setGalleryStatus(status: String) {
        galleryStatus = status
    }

    fun getRealUri(): Uri? {
        return realUri
    }

    fun requirePermissions(permissions: Array<String>, requestCode: Int) {
        Log.d(TAG, "권한 요청")
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
        Log.d(TAG, "onRequestPermissionsResult(), $grantResults")
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            permissionGranted(requestCode)
        } else {
            permissionDenied(requestCode)
        }
    }

    private fun permissionGranted(requestCode: Int) {

        when (requestCode) {
            PERMISSION_CAMERA -> openCamera()
            PERMISSION_GALLERY -> openGallery()
        }
    }

    private fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            PERMISSION_CAMERA -> Toast.makeText(
                this,
                "카메라 권한을 승인해야 카메라를 사용할 수 있습니다.",
                Toast.LENGTH_LONG
            ).show()

            PERMISSION_GALLERY -> Toast.makeText(
                this,
                "저장소 권한을 승인해야 앨범에서 이미지를 불러올 수 있습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//        uri 기반
        createImageUri(newFileName(), "image/jpg")?.let { uri: Uri ->
            Log.d(TAG, uri.toString())
            realUri = uri
            // MediaStore.EXTRA_OUTPUT을 Key로 하여 Uri를 넘겨주면
            // 일반적인 Camera App은 이를 받아 내가 지정한 경로에 사진을 찍어서 저장시킨다.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, realUri)
            startActivityForResult(intent, REQUEST_CAMERA)
        }


    }

    //    사진 하나만 첨부할 때 사용
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, REQUEST_STORAGE)
    }



    private fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "$filename.jpg"
    }

    //    갤러리에 이미지를 저장
    private fun createImageUri(filename: String, mimeType: String): Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Groot")

        return this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }


    /** 카메라 및 앨범 Intent 결과
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Unit {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
//                    uri 기반
                    realUri?.let { uri ->
                        val intent = Intent(this, SearchCameraActivity::class.java)
                        intent.putExtra("imageUri", uri.toString())
                        intent.putExtra("cameraStatus", cameraStatus)
                        Log.d(TAG, "uri:" + uri.toString())
                        Log.d(TAG, "cameraStatus:" + cameraStatus)
                        startActivity(intent)
                    }

                }

                REQUEST_STORAGE -> {
                    data?.data?.let { uri ->
                        val potDiaryCreateFragment =
                            supportFragmentManager.findFragmentById(R.id.fl_container) as PotDiaryCreateFragment?
                        if (potDiaryCreateFragment != null) {
                            potDiaryCreateFragment.attachPhoto(uri)
                        }
                    }
//                    var i = 0
//                    while (i < data?.clipData!!.itemCount) {
//                        Log.d(TAG, "test")
//                    }
                }
            }
        }

//        // Fragment에서 onActivityResult() 함수를 호출
//        val fragment = supportFragmentManager.findFragmentById(R.id.imageInput)
//        fragment?.onActivityResult(requestCode, resultCode, data)
    }

//    ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        potId = intent.getIntExtra("potId", 0)
        potName = intent.getStringExtra("potName").toString()
        potPlant = intent.getStringExtra("potPlant").toString()
        var refreshToken = GlobalVariables.prefs.getString("refresh_token", "")
        if (refreshToken == "") {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.imageInput, CommunityPostFragment())
//                .commit()
//        }




//      main에서만 날씨 fragment 보여주기
        var frameLayout = findViewById<FrameLayout>(R.id.fl_container)


//        네비게이션 바 조작
        // 하단 탭이 눌렸을 때 화면을 전환하기 위해선 이벤트 처리하기 위해 BottomNavigationView 객체 생성
        bnv_main = findViewById(R.id.bottom_navigation) as BottomNavigationView

        // OnNavigationItemSelectedListener를 통해 탭 아이템 선택 시 이벤트를 처리
        // navi_menu.xml 에서 설정했던 각 아이템들의 id를 통해 알맞은 프래그먼트로 변경하게 한다.
        bnv_main.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.potFragment -> {
                        // 다른 프래그먼트 화면으로 이동하는 기능
                        val homeFragment = PotFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, homeFragment).commit()
//                        // 프래그먼트가 변경되면서, 왼쪽 마진값을 0으로 변경
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 0
//                        params.rightMargin = 0
//                        params.topMargin = 0
//                        frameLayout.layoutParams = params
                    }

                    R.id.searchFragment -> {
                        val boardFragment = SearchFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 40
//                        params.rightMargin = 40
//                        frameLayout.layoutParams = params
                    }

                    R.id.communityFragment -> {
                        val boardFragment = CommunityFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 40
//                        params.rightMargin = 40
//                        frameLayout.layoutParams = params
                    }

                    R.id.userFragment -> {
                        val boardFragment = UserFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fl_container, boardFragment).commit()
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 20
//                        params.rightMargin = 20
//                        params.topMargin = 20
//                        frameLayout.layoutParams = params
                    }
                }
                true
            }
            selectedItemId = R.id.potFragment
//            1차 릴리즈 : search를 메인으로
//            selectedItemId = R.id.searchFragment
        }

        //        특정 프레그먼트로 이동
        var toPage = intent.getStringExtra("toPage")
        if (toPage != null) {

            Log.d(TAG, "toPage" + toPage)

            when (toPage) {
                "search_detail" -> {
                    bnv_main.run { selectedItemId = R.id.searchFragment }
                }
            }
            changeFragment(toPage)
        }
    }
}


