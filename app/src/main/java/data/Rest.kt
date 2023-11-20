package data

import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng
import java.io.Serializable

data class Rest (
    val REST_ID: Long,
    val REST_NAME: String,
    val REST_CATEGORY: String,
    val REST_TEL: String?,
    val REST_SI: String?,
    val REST_GU: String?,
    val REST_DONG: String?,
    val REST_LAT: Double,
    val REST_LNG: Double,
    val REST_IMG: String
): Serializable, TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(REST_LAT, REST_LNG)
    }
}