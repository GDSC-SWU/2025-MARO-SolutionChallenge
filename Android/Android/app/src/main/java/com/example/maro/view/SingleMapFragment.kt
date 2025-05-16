package com.example.maro.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.maro.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// map detail activity map
class SingleMapFragment : Fragment(), OnMapReadyCallback {

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble("lat")
            longitude = it.getDouble("lng")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapViewFragment)
            as? SupportMapFragment ?: SupportMapFragment.newInstance().also {
            childFragmentManager.beginTransaction()
                .replace(R.id.mapViewFragment, it)
                .commit()
        }
        mapFragment.getMapAsync(this)
        return inflater.inflate(R.layout.fragment_single_map, container, false)
    }

    override fun onMapReady(map: GoogleMap) {
        val lat = latitude ?: return
        val lng = longitude ?: return
        val location = LatLng(lat, lng)

        // custom marker
        val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker)
        val smallMarker = Bitmap.createScaledBitmap(markerBitmap, 85, 100, false)

        map.addMarker(
            MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)) // 커스텀 아이콘 적용
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))

        // map setting
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))


        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
        }
    }

    companion object {
        fun newInstance(lat: Double, lng: Double): SingleMapFragment {
            val fragment = SingleMapFragment()
            fragment.arguments = Bundle().apply {
                putDouble("lat", lat)
                putDouble("lng", lng)
            }
            return fragment
        }
    }
}
