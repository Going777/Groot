package com.chocobi.groot

import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_CAMERA
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.chat.ChatFragment
import com.chocobi.groot.view.chat.ChatUserListFragment
import com.chocobi.groot.view.community.CommunityFragment
import com.chocobi.groot.view.community.CommunityPostFragment
import com.chocobi.groot.view.community.CommunityShareFragment
import com.chocobi.groot.view.community.model.CommunityService
import com.chocobi.groot.view.community.model.PopularTagResponse
import com.chocobi.groot.view.intro.IntroActivity
import com.chocobi.groot.view.intro.IntroDataService
import com.chocobi.groot.view.intro.PlantNamesResponse
import com.chocobi.groot.view.intro.RegionNameResponse
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.pot.PotDetailFragment
import com.chocobi.groot.view.pot.PotDiaryBottomSheet
import com.chocobi.groot.view.pot.PotDiaryCreateFragment
import com.chocobi.groot.view.pot.PotDiaryFragment
import com.chocobi.groot.view.pot.PotFragment
import com.chocobi.groot.view.search.SearchCameraActivity
import com.chocobi.groot.view.search.SearchDetailFragment
import com.chocobi.groot.view.search.SearchFragment
import com.chocobi.groot.view.user.SettingFragment
import com.chocobi.groot.view.user.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    fun setPotId(id: Int) {
        potId = id
        Log.d("potDiary", "$potId")
    }

    fun setPotName(name: String) {
        potName = name
    }

    fun setPotPlant(plant: String) {
        potPlant = plant
    }

    fun setPotCharImg(plant: String) {
        potCharImg = plant
    }

    //    알림 요청
    val notificationPermissionRequestCode = 1001

    // 알림 권한을 요청하는 메서드
    private fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(intent)
    }

    //        fragment 조작
    fun changeFragment(index: String) {
        var fragment: Fragment? = null



        when (index) {
            "pot" -> {
                bnv_main.run { selectedItemId = R.id.potFragment }
            }

            "pot_diary" -> {
                val bundle = Bundle()
                bundle.putInt("detailPotId", potId)
                Log.d("potDiary change page", "$potId")
                fragment = PotDiaryFragment()
                fragment.arguments = bundle
            }

            "pot_diary_create" -> {
                val bundle = Bundle()
                bundle.putInt("potId", potId)
                bundle.putString("potName", potName)
                bundle.putString("potPlant", potPlant)
                bundle.putString("potCharImg", potCharImg)

                fragment = PotDiaryCreateFragment()
                fragment.arguments = bundle
            }

            "pot_detail" -> {
                val bundle = Bundle()
                bundle.putInt("potId", potId)
                bundle.putString("potName", potName)
                bundle.putString("potPlant", potPlant)

                fragment = PotDetailFragment()
                fragment.arguments = bundle
            }

            "search" -> {
                fragment = SearchFragment()
            }

            "search_detail" -> {
                val bundle = Bundle()
                bundle.putString(
                    "plant_id", intent.getStringExtra("plant_id")
                )
                fragment = SearchDetailFragment()
                fragment.arguments = bundle
            }

            "community_share" -> {
                fragment = CommunityShareFragment()
            }

            "community_post" -> {
                fragment = CommunityPostFragment("자유")
            }

            "community_qna" -> {
                fragment = CommunityPostFragment("QnA")
            }

            "community_tip" -> {
                fragment = CommunityPostFragment("Tip")
            }

            "setting" -> {
                fragment = SettingFragment()
            }

            "chat_user_list" -> {
                fragment = ChatUserListFragment()
            }
        }
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fl_container, fragment, index)
                .addToBackStack(index)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss()
        }
    }


    private fun checkPotDetailFragmentInBackStack(): Boolean {
        val fragmentManager = supportFragmentManager
        val fragmentCount = fragmentManager.backStackEntryCount

        if (fragmentCount > 0) {
            val topFragment = fragmentManager.getBackStackEntryAt(fragmentCount - 1)
            if (topFragment.name == PotDetailFragment::class.java.name) {
                Log.d("MainActivity", "PotDetailFragment is at the top of the backstack")
                return true
            } else {
                return false
            }
        }
        return false

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
            var isAllPermissionsGranted = false
            if (requestCode == PERMISSION_GALLERY) {
                isAllPermissionsGranted =
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES).all {
                        checkSelfPermission(
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    } || arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE).all {
                        checkSelfPermission(
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }
            } else {

                isAllPermissionsGranted =
                    permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            }
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
                "카메라 권한을 승인해야 해당 기능을 사용할 수 있습니다.",
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

        if (requestCode == notificationPermissionRequestCode) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.isNotificationPolicyAccessGranted) {
                // 권한이 부여된 경우 처리할 작업 수행
                // 예: 알림 사용 코드 작성
                Toast.makeText(this, "알림이 허용 되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림이 거부 되었습니다.", Toast.LENGTH_SHORT).show()
                // 권한이 거부된 경우 처리할 작업 수행
            }
        }


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
                        if (galleryStatus == "pot_diary_edit") {
                            val potDiaryBottomSheet =
                                supportFragmentManager.findFragmentByTag("PotDiaryBottomSheet") as PotDiaryBottomSheet?
                            if (potDiaryBottomSheet != null) {
                                potDiaryBottomSheet.attachPhoto(uri)
                            }

                        } else {
                            val potDiaryCreateFragment =
                                supportFragmentManager.findFragmentById(R.id.fl_container) as PotDiaryCreateFragment?
                            if (potDiaryCreateFragment != null) {
                                potDiaryCreateFragment.attachPhoto(uri)
                            }
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        알림 설정
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            var dialog = AlertDialog.Builder(this)
            dialog.setMessage("원활한 식물 리마인더를 위해 알림 권한을 허용해주세요.")
            dialog.setPositiveButton("확인") { dialog, which ->
                openAppNotificationSettings()
            }
            dialog.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            dialog.show()

        }


//        requestSubscribe()

        //        화분 정보 받아왔는지 체크
        val isExistPlantData = GlobalVariables.prefs.getString("plant_names", "")
        if (isExistPlantData == "") {
//            Toast.makeText(this, "지역 / 식물 다 받아올 거임", Toast.LENGTH_SHORT).show()
//        화분 이름 받아오기
            getPlantNameList()
//            지역 받아오기
            getRegionNameList()
        } else {
//            GlobalVariables.prefs.setString("plant_names", "")
//            Toast.makeText(this, "지역 / 식물 다 받아옴", Toast.LENGTH_SHORT).show()
        }

//        인기태그 가져오기
        getPopularTag()

        potId = intent.getIntExtra("potId", 0)
        potName = intent.getStringExtra("potName").toString()
        potPlant = intent.getStringExtra("potPlant").toString()
        var refreshToken = GlobalVariables.prefs.getString("refresh_token", "")
        if (refreshToken == "" || refreshToken == null) {
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
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.fl_container, homeFragment, "pot")
                            .addToBackStack("pot")
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitAllowingStateLoss()
//                            .commit()
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
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.fl_container, boardFragment, "search")
                            .addToBackStack("search")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitAllowingStateLoss()
//                            .commit()
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 40
//                        params.rightMargin = 40
//                        frameLayout.layoutParams = params
                    }

                    R.id.communityFragment -> {
                        val boardFragment = CommunityFragment()
                        supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.fl_container, boardFragment, "community")
                            .addToBackStack("community")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitAllowingStateLoss()
//                            .commit()
//                        val params = frameLayout.layoutParams as ViewGroup.MarginLayoutParams
//                        params.leftMargin = 40
//                        params.rightMargin = 40
//                        frameLayout.layoutParams = params
                    }

                    R.id.userFragment -> {
                        val boardFragment = UserFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.fl_container, boardFragment, "user")
                            .addToBackStack("user")
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitAllowingStateLoss()
//                            .commit()
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
        val plantId = intent.getStringExtra("plant_id")
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


    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("pot")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("search")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("community")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("user")
        Log.d(TAG, "${tag1} ${tag2} ${tag3} ${tag4}")

        if (tag1 != null && tag1.isVisible()) navigation.getMenu().findItem(R.id.potFragment)
            .setChecked(true)
        else if (tag2 != null && tag2.isVisible()) navigation.getMenu()
            .findItem(R.id.searchFragment).setChecked(true)
        else if (tag3 != null && tag3.isVisible()) navigation.getMenu()
            .findItem(R.id.communityFragment).setChecked(true)
        else if (tag4 != null && tag4.isVisible()) navigation.getMenu().findItem(R.id.userFragment)
            .setChecked(true)
        else {

//            스택 아무것도 없을 때 인트로로
            val tag5: Fragment? = supportFragmentManager.findFragmentByTag("pot_diary")
            val tag6: Fragment? = supportFragmentManager.findFragmentByTag("pot_diary_create")
            val tag7: Fragment? = supportFragmentManager.findFragmentByTag("pot_detail")
            val tag8: Fragment? = supportFragmentManager.findFragmentByTag("search_detail")
            val tag9: Fragment? = supportFragmentManager.findFragmentByTag("community_share")
            val tag10: Fragment? = supportFragmentManager.findFragmentByTag("community_post")
            val tag11: Fragment? = supportFragmentManager.findFragmentByTag("setting")

            if (tag5 == null && tag6 == null && tag7 == null && tag8 == null && tag9 == null && tag10 == null && tag11 == null) {
                var intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val bnv = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        updateBottomMenu(bnv)
    }


    private fun getPlantNameList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val IntroDataService = retrofit.create(IntroDataService::class.java)

//        요청 보내기
        IntroDataService.requestPlantNames().enqueue(object : Callback<PlantNamesResponse> {
            //            요청 성공
            override fun onResponse(
                call: Call<PlantNamesResponse>,
                response: Response<PlantNamesResponse>
            ) {
                if (response.code() == 200) {
                    val plantNameBody = response.body()

                    Log.d("IntroActivity", "onResponse(), 식물 $plantNameBody")
//                    전역 변수에 식물 이름 리스트 저장
                    if (plantNameBody != null) {
                        val plantNames = plantNameBody.nameList.joinToString()
                        GlobalVariables.prefs.setString("plant_names", plantNames)
                    }
                }
            }

            //            요청 실패
            override fun onFailure(call: Call<PlantNamesResponse>, t: Throwable) {
                Log.d(TAG, "onFailure() 식물 이름 가져오기")
            }
        })
    }

    private fun getRegionNameList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val IntroDataService = retrofit.create(IntroDataService::class.java)

//        요청 보내기
        IntroDataService.requestRegionNames().enqueue(object : Callback<RegionNameResponse> {
            //            요청 성공
            override fun onResponse(
                call: Call<RegionNameResponse>,
                response: Response<RegionNameResponse>
            ) {
                if (response.code() == 200) {
                    val regionNameBody = response.body()

                    Log.d("IntroActivity", "지역 onResponse() / $regionNameBody")
//                    전역 변수에 지역 리스트 저장
                    if (regionNameBody != null) {
                        val plantNames = regionNameBody.regions.joinToString()
                        GlobalVariables.prefs.setString("region_names", plantNames)
                    }
                }
            }

            //            요청 실패
            override fun onFailure(call: Call<RegionNameResponse>, t: Throwable) {
                Log.d("IntroActivity", "onFailure() 지역 가져오기")
            }
        })
    }

    private fun getPopularTag() {
        val retrofit = RetrofitClient.basicClient()!!
        val communityService = retrofit.create(CommunityService::class.java)
        communityService.requestPopularTags()
            .enqueue(object : retrofit2.Callback<PopularTagResponse> {
                override fun onResponse(
                    call: Call<PopularTagResponse>,
                    response: Response<PopularTagResponse>
                ) {
                    if (response.code() == 200) {
                        val body = response.body()
                        if (body != null) {
                            val popularTags = body.tags
                            val popularTagsList = ArrayList<String>()
                            for (tag in popularTags) {
                                popularTagsList.add(tag.tag)
                            }
                            GlobalVariables.prefs.setString(
                                "popular_tags",
                                popularTagsList.joinToString()
                            )
                            Log.d("CommunityFragment", "onResponse() 조회 성공 $popularTags")
                        }
                    } else {
                        Log.d("CommunityFragment", "onFailure() 인기태그 조회 실패1")

                    }
                }

                override fun onFailure(call: Call<PopularTagResponse>, t: Throwable) {
                    Log.d("CommunityFragment", "onFailure() 인기태그 조회 실패2")
                }

            })
    }
}


