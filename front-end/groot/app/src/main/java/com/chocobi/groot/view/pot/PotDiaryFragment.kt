package com.chocobi.groot.view.pot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.pot.adapter.PotCollectionRVAdapter
import com.chocobi.groot.view.pot.adapter.PotDiaryListRVAdapter
import com.chocobi.groot.view.pot.adapter.PotListRVAdapter
import com.chocobi.groot.view.pot.model.Date
import com.chocobi.groot.view.pot.model.DateTime
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.PotListResponse
import com.chocobi.groot.view.pot.model.PotService
import com.chocobi.groot.view.pot.model.Time
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PotDiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PotDiaryFragment : Fragment() {
    private val TAG = "PotDiaryFragment"
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var potListRV: RecyclerView
    private var potRvAdapter: PotListRVAdapter? = null
    private lateinit var adapter: PotDiaryListRVAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private var potList: MutableList<Pot>? = null
    private val firstItem: Pot = Pot(
        0,
        0,
        "",
        "",
        "",
        0,
        DateTime(Date(0, 0, 0), Time(0, 0, 0, 0)),
        null,
        null,
        null,
        false,
        0,
        "",
        "",
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pot_diary, container, false)
        val mActivity = activity as MainActivity
        findViews(rootView)
        setListeners()
        initList()
        reload()
        getPotList(mActivity)

        return rootView
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
        potListRV = view.findViewById(R.id.potListRecyclerView)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = PotDiaryListRVAdapter()
        adapter.delegate = object : PotDiaryListRVAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun reload() {
        showProgress()

        ThreadUtil.startThread {
            val list = createDummyData(0, 10)
            ThreadUtil.startUIThread(1000) {
                adapter.reload(list)
                hideProgress()
            }
        }
    }

    private fun loadMore() {
        showProgress()

        ThreadUtil.startThread {
            val list = createDummyData(adapter.itemCount, 10)
            ThreadUtil.startUIThread(1000) {
                adapter.reload(list)
                hideProgress()
            }
        }
    }

    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(offset: Int, limit: Int): MutableList<ModelDiary> {
        val list: MutableList<ModelDiary> = mutableListOf()

        for (i in offset until (offset + limit)) {
            val diaryItem = ModelDiary(
                id = 10,
                potId = 0,
                potName = "산세산세",
                image = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/PurpleFlowerWade.JPG/1920px-PurpleFlowerWade.JPG",
                content = getString(R.string.lorem_ipsum),
                water = true,
                nutrients = true,
                pruning = false,
                bug = true,
                sun = true,
                createDate = "2023-02-06T16:43:50.313224",
            )
            list.add(diaryItem)
        }

        return list
    }

    fun getPotList(mActivity: MainActivity) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.getPotList().enqueue(object :
            Callback<PotListResponse> {
            override fun onResponse(
                call: Call<PotListResponse>,
                response: Response<PotListResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "$body")
                    Log.d(TAG, "body: $body")
                    Log.d(TAG, "body: ${body.pots.size}")
                    potList = body.pots.toMutableList()
                    potList!!.add(0, firstItem)


                    setRecyclerView(potList!!, mActivity)


                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<PotListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    fun setRecyclerView(potList: List<Pot>, mActivity: MainActivity) {

        potRvAdapter = PotListRVAdapter(potList)
        potListRV.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        potListRV.adapter = potRvAdapter
        potRvAdapter!!.itemClick = object : PotListRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                clickDiaryPot(potList[position].potId)
            }
        }
    }

    private fun clickDiaryPot(potId: Int) {
        if (potId == 0) {
//            전체 다이어리 조회
            getAllDiary()
        } else {
//            화분 다이어리 조회
            getPotDiary(potId)
        }

    }

    private fun getAllDiary() {

    }

    private fun getPotDiary(potId: Int) {

    }
}