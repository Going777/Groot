package com.chocobi.groot.view.plant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.view.plant.adapter.PlantDiaryListRVAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantDiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantDiaryFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantDiaryListRVAdapter
    private lateinit var frameLayoutProgress: FrameLayout


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_plant_diary, container, false)
        findViews(rootView)
        setListeners()
        initList()
        reload()

        return rootView
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = PlantDiaryListRVAdapter()
        adapter.delegate = object : PlantDiaryListRVAdapter.RecyclerViewAdapterDelegate {
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
}