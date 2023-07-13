package com.app.veggie.calPage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.veggie.R
import kotlinx.android.synthetic.main.fragment_cal_page.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.tool_bar.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class calPage : Fragment() {

    var todaysDate = "00.00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todaysDate = todayDate();
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cal_page, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle.text = "식단표 어쩌구"
        menuDate.text = "오늘의 식단 / $todaysDate"
    }

    override fun onResume() {
        super.onResume()

        addBtn.setOnClickListener{
            Toast.makeText(requireContext(), "식단표 추가 버튼입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun todayDate() : String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MM.dd")
        val dateString = today.format(formatter)

        return dateString
    }

}