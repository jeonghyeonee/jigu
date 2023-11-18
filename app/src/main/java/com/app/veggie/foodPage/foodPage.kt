package com.app.veggie.foodPage

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import com.app.veggie.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.fragment_food_page.btnBarcodeInput
import kotlinx.android.synthetic.main.fragment_food_page.etBarcodeInput
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [foodPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class foodPage : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeView: DecoratedBarcodeView

    private var isContinuousScanningEnabled = true



    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private val IMAGE_PICK_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food_page, container, false)

        barcodeView = view.findViewById(R.id.barcodeScannerView)
        captureManager = CaptureManager(requireActivity(), barcodeView)
        captureManager.initializeFromIntent(requireActivity().intent, savedInstanceState)
        val btnBarcodeInput = view.findViewById<Button>(R.id.btnBarcodeInput)
        val btnUploadImg = view.findViewById<Button>(R.id.btnUploadImg)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startScanner()
        } else {
            requestCameraPermission()
        }

        btnUploadImg.setOnClickListener {
            // Start an image picker activity
            isContinuousScanningEnabled = false

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)

            // 가져온 바코드 값을 Toast로 표시

            isContinuousScanningEnabled = true

        }

        btnBarcodeInput.setOnClickListener {
            // 중복 실행을 방지하기 위해 버튼 클릭 이벤트가 실행 중인 동안 스캐너 정지
            isContinuousScanningEnabled = false

            // 바코드 값을 가져오는 로직
            val barcodeValue = getBarcodeValue()

            // 가져온 바코드 값을 Toast로 표시
            showBarcodeToast(barcodeValue)

            // 스캐너 재시작
            isContinuousScanningEnabled = true
        }

        return view
    }

    private fun showBarcodeToast(barcode: String) {
        Toast.makeText(requireActivity(), "Barcode: $barcode", Toast.LENGTH_SHORT).show()
    }

    private fun startScanner() {
        barcodeView.decodeContinuous { result: BarcodeResult? ->
            // 중복 실행을 방지하기 위해 isContinuousScanningEnabled 체크
            if (isContinuousScanningEnabled) {
                result?.let {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "바코드: ${it.text}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanner()
                } else {
                    // 권한이 거부됨
                    // 필요한 처리를 수행하세요.
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }



    // 바코드 값을 가져오는 로직을 작성 (예를 들어, EditText에서 바코드 값을 가져오는 등)
    private fun getBarcodeValue(): String {
        // 실제로는 바코드 값을 어디서 가져오는지에 따라 구현이 달라집니다.
        // 예시로 EditText의 값을 가져오는 것으로 가정합니다.
        return etBarcodeInput.text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                processBarcodeFromImage(selectedImageUri)
            }
        }
    }

    private fun processBarcodeFromImage(imageUri: Uri) {
        try {
            // Use ContentResolver to get the bitmap from the URI
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // 이미지를 90도 회전
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)

            // Decode the barcode using ZXing's MultiFormatReader
            val result = MultiFormatReader().decode(
                BinaryBitmap(HybridBinarizer(RGBLuminanceSource(rotatedBitmap.width, rotatedBitmap.height, IntArray(rotatedBitmap.width * rotatedBitmap.height).apply {
                    rotatedBitmap.getPixels(this, 0, rotatedBitmap.width, 0, 0, rotatedBitmap.width, rotatedBitmap.height)
                })))
            )

            // Handle the barcode result (e.g., display in a Toast)
            result?.let {
                showBarcodeToast(it.text)
            }
        } catch (e: ReaderException) {
            // Handle exceptions that may occur during barcode decoding
            showBarcodeToast("Barcode not found or could not be decoded. ${e.message}")
        } catch (e: IOException) {
            // Handle IOException
            showBarcodeToast("Error reading the image. ${e.message}")
        }
    }



}

