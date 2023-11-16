package com.app.veggie.foodPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.app.veggie.R
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_food_page.btnTextBarcode
import kotlinx.android.synthetic.main.fragment_food_page.btnUploadBarcode
import kotlinx.android.synthetic.main.fragment_food_page.editTextBarcode
import kotlinx.android.synthetic.main.fragment_food_page.scannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [foodPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class foodPage : Fragment(), ZXingScannerView.ResultHandler {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val REQUEST_IMAGE_PICK = 100
    lateinit var frameLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_food_page, container, false)
        frameLayout = rootView.findViewById(R.id.foodPage) // replace with your actual FrameLayout ID
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ZXingScannerView 초기화
        // scannerView = ZXingScannerView(requireContext())

        // 기존에 추가된 뷰가 있다면 제거
        frameLayout.removeAllViews()

        // 스캐너 뷰를 프레임 레이아웃에 추가
        frameLayout.addView(scannerView)

        // 자동으로 스캔 결과 처리
        scannerView.setAutoFocus(true)
        scannerView.setResultHandler(this)

        // 스캐너 뷰 시작
        scannerView.startCamera()

        // 이미지 업로드 버튼 클릭 시
        btnUploadBarcode.setOnClickListener {
            openGallery()
        }

        // 텍스트 입력 버튼 클릭 시
        btnTextBarcode.setOnClickListener {
            val textInput = editTextBarcode.text.toString()
            showToast("Text Barcode: $textInput")
        }
    }


    // 스캔 결과 처리
    override fun handleResult(result: Result) {
        // 스캔된 바코드 결과
        val scannedBarcode = result.text
        showToast("Scanned Barcode: $scannedBarcode")

        // 여기에서 필요한 추가 동작을 수행할 수 있습니다.

        // 스캐너 뷰 다시 시작
        scannerView.resumeCameraPreview(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트가 종료될 때 카메라 릴리스
        scannerView.stopCamera()
    }


    // 갤러리에서 이미지를 선택하는 함수
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // 액티비티 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 바코드 스캔 결과 처리
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                showToast("Scan canceled")
            } else {
                val scannedBarcode = result.contents
                showToast("Scanned Barcode: $scannedBarcode")
                editTextBarcode.setText(scannedBarcode)
            }
        }

/*        // 갤러리에서 이미지 선택 결과 처리
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // 이미지 처리 코드 (여기서는 토스트 메시지만 출력)
            showToast("Image Barcode: Placeholder for Image Barcode Info")
        }*/
    }

    // 토스트 메시지를 표시하는 함수
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment foodPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            foodPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



