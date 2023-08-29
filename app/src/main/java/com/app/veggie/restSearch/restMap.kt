package com.app.veggie.restSearch

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.veggie.MainActivity
import com.app.veggie.R
import com.app.veggie.databinding.FragmentRestMapBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import data.Rest
import kotlinx.android.synthetic.main.fragment_cal_page.*
import ted.gun0912.clustering.naver.TedNaverClustering

class restMap : Fragment(), OnMapReadyCallback {
    var mainActivity: MainActivity? = null

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var binding : FragmentRestMapBinding

    private lateinit var mContext: Context

    // 파이어베이스 세팅 - 스토리지
    private val storage: FirebaseStorage =
        FirebaseStorage.getInstance("gs://mp-project-2023.appspot.com/")
    private val storageRef: StorageReference = storage.reference

    // 파이어베이스 세팅 - 파이어스토어
    val firestore = Firebase.firestore
    val doc = firestore.collection("TB_REST")

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mainActivity = context as MainActivity

    }

    override fun onMapReady(naverMap: NaverMap) {
        val uiSettings = naverMap.uiSettings

        uiSettings.isZoomControlEnabled = false
        uiSettings.isCompassEnabled = false
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isScaleBarEnabled = false
        uiSettings.isLogoClickEnabled = false

        this.naverMap = naverMap

        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        updateMarker(restList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_fragment)
        mapView.onCreate(savedInstanceState)

    }

    var restList: List<Rest> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        firestore.collection("TB_REST").get()
            .addOnSuccessListener { results ->
                val tempList = mutableListOf<Rest>()

                for (res in results){
                    val rest = Rest(
                        REST_ID = res["rest_id"] as Long,
                        REST_NAME = res["rest_name"] as String,
                        REST_CATEGORY = res["rest_category"] as String,
                        REST_TEL = res["res_tel"] as String?,
                        REST_SI = res["res_si"] as String?,
                        REST_GU = res["rest_gu"] as String?,
                        REST_DONG = res["rest_dong"] as String?,
                        REST_LAT = res["rest_lat"] as Double,
                        REST_LNG = res["rest_lng"] as Double
                     )

                    tempList.add(rest)
                }

                restList = tempList

            }.addOnFailureListener {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestMapBinding.inflate(inflater, container, false)
        val view = binding.root

        mapView = binding.mapFragment
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun updateMarker(rows: List<Rest>) {

        // 마커 클러스터링
        TedNaverClustering.with<Rest>(mContext, naverMap)
            .items(rows)
            .make()
    }

}
