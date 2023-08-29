package com.app.veggie.restSearch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.veggie.MainActivity
import com.app.veggie.R
import com.app.veggie.databinding.FragmentRestMapBinding
import com.app.veggie.restSearch.restMap.Companion.latitude
import com.app.veggie.restSearch.restMap.Companion.longitude
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource
import data.Rest
import kotlinx.android.synthetic.main.fragment_rest_map.*
import ted.gun0912.clustering.naver.TedNaverClustering
import java.lang.Math.*

class restMap : Fragment(), OnMapReadyCallback {
    var mainActivity: MainActivity? = null

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var binding : FragmentRestMapBinding

    private lateinit var mContext: Context

    private lateinit var restListView: RecyclerView
    private lateinit var restListAdapter: RestAdapter

    // 파이어베이스 세팅 - 스토리지
    private val storage: FirebaseStorage =
        FirebaseStorage.getInstance("gs://mp-project-2023.appspot.com/")
    private val storageRef: StorageReference = storage.reference

    // 파이어베이스 세팅 - 파이어스토어
    val firestore = Firebase.firestore
    val doc = firestore.collection("TB_REST")

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        var latitude = 37.631822
        var longitude = 127.077581
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

//        resetBtn.visibility = View.INVISIBLE

        // Toast.makeText(mContext, "${latitude}, ${longitude}", Toast.LENGTH_SHORT).show()

        // 지도 이동 중 멈춰도 좌표값 누락으로 인한 오류가 생기지 않게 함
        naverMap.addOnLocationChangeListener { location ->
            latitude = location.latitude
            longitude = location.longitude
        }
        var cameraChangeReason = 0
        // 지도 이동이 멈추면 해당 시점을 현위치로 저장함
        naverMap.addOnCameraChangeListener { reason, animated ->
            // Log.e("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
            cameraChangeReason = reason
        }

        naverMap.addOnCameraIdleListener {
            naverMap.removeOnLocationChangeListener { }
            latitude = naverMap.cameraPosition.target.latitude
            longitude = naverMap.cameraPosition.target.longitude

            //Log.e("CheckCurrentCoord", "현재 좌표 : $latitude, $longitude")

//            when(cameraChangeReason){
//                0, -3 -> null
//                else -> {
//                    resetBtn.visibility = View.VISIBLE
//
//                }
//            }
        }
        loadRestList(latitude, longitude, 1500, mapOf("isOption" to false))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map_fragment)
        mapView.onCreate(savedInstanceState)
    }

    var restList: ArrayList<Rest> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

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

        val layout = LinearLayoutManager(context)
        binding.slidingList.restListRecycler.layoutManager = layout
        binding.slidingList.restListRecycler.setHasFixedSize(true)
        binding.slidingList.restListRecycler.isVerticalScrollBarEnabled = true

        return view
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        btn_current_location.setOnClickListener{
            naverMap.locationSource = locationSource
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            binding.slidingList.currentLocation.textSize= 21F
            binding.slidingList.currentLocation.text = "현재 위치"
            binding.searchText.text = null

            loadRestList(latitude, longitude, 1500, mapOf("isOption" to false))
        }
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun loadRestList(latitude: Double, longitude: Double, radius:  Int, options: Map<String, Any>){
        val mAdapter = context?.let { RestAdapter(restList, mContext) }
        binding.slidingList.restListRecycler.adapter = mAdapter

        restList.clear()

        // 사용자 현재 위치
        val userLocation = GeoPoint(latitude, longitude) // 사용자의 위도와 경도 값

        // 3km 이내의 위치 쿼리 생성
        val centerLatitude = userLocation.latitude
        val centerLongitude = userLocation.longitude
        val radiusInKm = radius/1000 // 반경 km
        val field = "rest_geo" // 파이어스토어에 저장된 위치 정보 필드 이름

        // 위도(latitude) 범위 계산
        val latitudeDelta = radiusInKm / 111.0 // 1도의 위도 차이는 약 111km
        val minLatitude = centerLatitude - latitudeDelta
        val maxLatitude = centerLatitude + latitudeDelta

        // 경도(longitude) 범위 계산
        val longitudeDelta = radiusInKm / (111.0 * cos(toRadians(centerLatitude)))
        val minLongitude = centerLongitude - longitudeDelta
        val maxLongitude = centerLongitude + longitudeDelta

        val query = firestore.collection("TB_REST")
            .whereGreaterThanOrEqualTo(field, GeoPoint(minLatitude, minLongitude))
            .whereLessThanOrEqualTo(field, GeoPoint(maxLatitude, maxLongitude))

        query.get()
            .addOnSuccessListener { results ->
                val tempList = arrayListOf<Rest>()

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
                        REST_LNG = res["rest_lng"] as Double,
                        REST_IMG = res["rest_img"] as String
                    )
                    tempList.add(rest)
                }

                restList = tempList
                updateMarker(restList)

                binding.slidingList.restCountNum.text = "${restList.size}개"

                val mAdapter = RestAdapter(restList, mContext)
                binding.slidingList.restListRecycler.adapter = mAdapter

            }.addOnFailureListener {

            }
    }
}

class RestAdapter(private val restList: List<Rest>, private val context: Context) :
    RecyclerView.Adapter<RestAdapter.RestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rest_list_recycler, parent, false)
        return RestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestViewHolder, position: Int) {
        val rest = restList[position]
        holder.bind(rest, context)
    }

    override fun getItemCount(): Int {
        return restList.size
    }

    inner class RestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(rest: Rest, context: Context) {
            // 뷰홀더 내부의 뷰에 식당 정보 설정
            itemView.findViewById<TextView>(R.id.rest_name).text = rest.REST_NAME
            itemView.findViewById<TextView>(R.id.rest_type).text = rest.REST_CATEGORY
            itemView.findViewById<TextView>(R.id.rest_dist).text = calculateDistance(lat1 = latitude, lat2 = rest.REST_LAT, lon1 = longitude, lon2 = rest.REST_LNG)
            val rest_image = itemView.findViewById<ImageView>(R.id.restImg)

            if(rest.REST_IMG != ""){
                Glide.with(context)
                    .load(rest.REST_IMG)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.8f)
                    .centerCrop()
                    .into(rest_image)
            }else{
                rest_image.visibility = View.INVISIBLE
            }
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val R = 6371 // 지구의 반지름 (단위: km)
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c // 두 지점 사이의 거리 (단위: km)

        return if (distance < 1) {
            "${(distance * 1000).toInt()}m" // 1000m 미만인 경우 m로 반환
        } else {
            String.format("%.1fkm", distance) // 1000m 이상인 경우 1.0km 형태로 반환
        }
    }
}
