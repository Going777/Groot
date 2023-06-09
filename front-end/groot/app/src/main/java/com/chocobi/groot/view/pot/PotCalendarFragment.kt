package com.chocobi.groot.view.pot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.databinding.CalendarDayBinding
import com.chocobi.groot.databinding.FragmentPotCalendarBinding
import com.chocobi.groot.view.pot.adapter.PotCalendarRVAdapter
import com.chocobi.groot.view.pot.model.DateDiaryResponse
import com.chocobi.groot.view.pot.model.Diary
import com.chocobi.groot.view.pot.model.PotService
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class PotCalendarFragment : PotCalendarBaseFragment(R.layout.fragment_pot_calendar), HasToolbar,
    HasBackButton {

    private val TAG = "PotCalendarFragment"

    private lateinit var mActivity: MainActivity
    private lateinit var rv: RecyclerView
    private var rvAdapter: PotCalendarRVAdapter? = null
    private lateinit var potFirstView: ConstraintLayout

    override val titleRes: Int = R.string.example_7_title

    override val toolbar: Toolbar
        get() = binding.exSevenToolbar

    private var selectedDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    private lateinit var binding: FragmentPotCalendarBinding
    private lateinit var monthText: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pot_calendar, container, false)
//        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        mActivity = activity as MainActivity
        potFirstView = rootView.findViewById(R.id.firstView)
        rv = rootView.findViewById(R.id.potCalendarRecyclerView)
        monthText = rootView.findViewById(R.id.monthText)

        val currentDate = LocalDate.now()
        getDateDiary(currentDate.toString())


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPotCalendarBinding.bind(view)

        class DayViewContainer(view: View) : ViewContainer(view) {

            val bind = CalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    Log.d(TAG, "${day.date}")
                    getDateDiary(day.date.toString())
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.exSevenCalendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
                    }
                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

                val colorRes = if (day.date == selectedDate) {
                    R.color.example_7_yellow
                } else R.color.example_7_white
                bind.exSevenDateText.setTextColor(view.context.getColorCompat(colorRes))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate
            }
        }

        binding.exSevenCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        binding.exSevenCalendar.weekScrollListener = { weekDays ->
//            binding.exSevenToolbar.title = getWeekPageTitle(weekDays)
            monthText.text = getWeekPageTitle(weekDays)
//            Log.d(TAG, getWeekPageTitle(weekDays))
        }

        val currentMonth = YearMonth.now()
        binding.exSevenCalendar.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )
        binding.exSevenCalendar.scrollToDate(LocalDate.now())
    }

    private fun getDateDiary(date: String) {
        val retrofit = RetrofitClient.getClient()!!
        val potService = retrofit.create(PotService::class.java)
        potService.getDateDiary(date).enqueue(object : Callback<DateDiaryResponse> {
            override fun onResponse(
                call: Call<DateDiaryResponse>,
                response: Response<DateDiaryResponse>
            ) {
                val body = response.body()
                if (body != null) {
                    Log.d(TAG, "$body")
                    setRecyclerView(body.diary)
                    hideFirstView()
                } else {

                    Log.d(TAG, "일일 다이어리 가져오기 body 없음")
                    showFirstView()

                }
            }

            override fun onFailure(call: Call<DateDiaryResponse>, t: Throwable) {
                Log.d(TAG, "일일 다이어리 가져오기 실패")
            }
        })

    }

    fun setRecyclerView(diaryList: List<Diary>) {
        rvAdapter = PotCalendarRVAdapter(requireContext(), diaryList)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = rvAdapter

        rvAdapter?.setItemClickListener(object : PotCalendarRVAdapter.ItemClickListener {
            override fun onPotImgClick(view: View, position: Int) {
                mActivity.setPotId(diaryList?.get(position)?.potId ?: 0)
                mActivity.changeFragment("pot_detail")
            }

            override fun onCheckClick(view: View, position: Int) {
//                postDiary(diaryList?.get(position)?.potId ?: 0, diaryList?.get(position)?.code!!)
            }
        })
    }



    private fun showFirstView() {
        potFirstView.visibility = View.VISIBLE
        rv.visibility = View.GONE
    }

    private fun hideFirstView() {
        potFirstView.visibility = View.GONE
        rv.visibility = View.VISIBLE
    }
}