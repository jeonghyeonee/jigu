package com.app.veggie.calPage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.veggie.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_cal_page.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.tool_bar.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class calPage : Fragment() {
    // 파이어베이스 세팅 - 스토리지
    private val storage: FirebaseStorage =
        FirebaseStorage.getInstance("gs://mp-project-2023.appspot.com/")
    private val storageRef: StorageReference = storage.reference

    // 파이어베이스 세팅 - 파이어스토어
    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    var todaysDate = "00.00"

    lateinit var testText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todaysDate = todayDate()

        firestore.collection("TB_TEST").document("test_text").get()
            .addOnSuccessListener { document ->
                if (document["contents"] != null){
                    testText = document["contents"] as String
                    firebaseText.text = testText
                }else{
                    testText = "데이터 없음"
                    firebaseText.text = testText
                }

            }.addOnFailureListener {
                testText = "연결안됨"
                firebaseText.text = testText
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_cal_page, container, false)

        return view
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