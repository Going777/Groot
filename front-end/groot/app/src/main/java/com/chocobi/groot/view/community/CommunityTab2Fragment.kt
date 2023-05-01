package com.chocobi.groot.view.community

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
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.adapter.item.ItemBean

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityTab2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityTab2Fragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_tab2, container, false)
        findViews(view)
        setListeners()
        initList()
        reload()


        return view
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
        adapter = RecyclerViewAdapter()
        adapter.delegate = object : RecyclerViewAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun reload() {
        showProgress()

        // get data from server

        ThreadUtil.startThread {
            Log.d("???", "reload 10 items")
            val list = createDummyData(0, 10)
            ThreadUtil.startUIThread(1000) {
                adapter.reload(list)
                hideProgress()

            }
        }
    }

    private fun loadMore() {
        showProgress()

        // get data from server

        ThreadUtil.startThread {
            Log.d("???", "reload 10 items")

            val list = createDummyData(adapter.itemCount, 10)
            ThreadUtil.startUIThread(1000) {
                adapter.loadMore(list)
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

    private fun createDummyData(offset: Int, limit: Int): MutableList<ItemBean> {

        val list: MutableList<ItemBean> = mutableListOf()

        var itemBean: ItemBean
        for (i in offset until (offset + limit)) {
            itemBean = ItemBean()
            itemBean.title = "title $i"
            itemBean.content = "content, content, content, content, content, content, content, content, content, content, content, content"
            itemBean.imageUrl = "https://cdn.wallpapersafari.com/15/87/kp4wAJ.jpg"
            list.add(itemBean)
        }



        return list
    }
}
