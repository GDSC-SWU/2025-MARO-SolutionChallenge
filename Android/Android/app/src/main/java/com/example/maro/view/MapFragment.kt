package com.example.maro.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.api.HospitalDto
import com.example.maro.model.SharedPrefsData
import com.example.maro.model.VaccinationCenterData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Flag to track whether the map has moved to user's location once
    private var movedToUserLocationOnce = false

    // RecyclerView
    private lateinit var recyclerView: RecyclerView

    // ViewModel to store item data
    private val viewModel: MapViewModel by activityViewModels()

    // ViewModel location tracking variables
    private var lastLat: Double? = null
    private var lastLng: Double? = null

    // Register callback for location permission request
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) enableUserLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize map view and register callback
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Setup BottomSheet
        val bottomSheet = view.findViewById<View>(R.id.bottom_sheet_content)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 470
            isHideable = false
        }

        // Set dummy hospital data for RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCenters)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Check location permission and enable if granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Observe ViewModel
        observeHospitalList()

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCenters)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable blue dot for current location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }

        googleMap.uiSettings.isZoomControlsEnabled = true

        // Move to user location
        enableUserLocation()

        // Map is ready, manually update UI if data is available
        viewModel.hospitalList.value?.let {
            updateHospitalUI(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        (activity as? MainActivity)?.apply {
            chatbotButton.visibility = View.GONE
            shouldHideChatbotButton = true
        }

        // Optional: re-fetch hospital data
        lastLat?.let { lat ->
            lastLng?.let { lng ->
                val lang = SharedPrefsData.getLanguageCode(requireContext())
                viewModel.fetchNearbyHospitals(lat, lng, lang)
            }
        }

        // Manually update UI with cached data
        viewModel.hospitalList.value?.let { cachedList ->
            updateHospitalUI(cachedList)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    // Enable user location and move to location on first launch
    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ::googleMap.isInitialized) {
            googleMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (!movedToUserLocationOnce) {
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                        movedToUserLocationOnce = true

                        // Save current location
                        lastLat = it.latitude
                        lastLng = it.longitude

                        // Request nearby hospital data
                        val targetLang = SharedPrefsData.getLanguageCode(requireContext())
                        viewModel.fetchNearbyHospitals(it.latitude, it.longitude, targetLang)
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    if (!movedToUserLocationOnce) {
                        val updatedLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updatedLatLng, 15f))
                        movedToUserLocationOnce = true
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    // Calculate distance in meters
    fun calculateDistanceInMeters(
        userLat: Double,
        userLng: Double,
        targetLat: Double,
        targetLng: Double
    ): Float {
        val userLocation = Location("").apply {
            latitude = userLat
            longitude = userLng
        }

        val targetLocation = Location("").apply {
            latitude = targetLat
            longitude = targetLng
        }

        return userLocation.distanceTo(targetLocation)
    }

    // Observe hospital data list
    private fun observeHospitalList() {
        viewModel.hospitalList.observe(viewLifecycleOwner) { hospitalList ->
            updateHospitalUI(hospitalList)
        }
    }

    // Update UI after observing hospital list
    private fun updateHospitalUI(hospitalList: List<HospitalDto>) {
        // Wait for GoogleMap initialization
        if (!::googleMap.isInitialized) {
            Log.w("MapFragment", "❌ googleMap not initialized during updateHospitalUI → Ignored")
            return
        }
        val noInfoTextView = view?.findViewById<TextView>(R.id.noInformation)
        val noInfoText = context?.getString(R.string.label_no_info) ?: "No information"

        if (hospitalList.isNullOrEmpty()) {
            recyclerView?.visibility = View.GONE
            noInfoTextView?.visibility = View.VISIBLE
            noInfoTextView?.text = noInfoText
            return
        }

        val userLat = viewModel.lastLat ?: return
        val userLng = viewModel.lastLng ?: return

        // Calculate distance from user to each hospital
        val centerItems = hospitalList.map {
            val distanceMeters = calculateDistanceInMeters(userLat, userLng, it.lat, it.lng)
            val distanceText = if (distanceMeters >= 1000) {
                String.format("%.1fkm", distanceMeters / 1000)
            } else {
                String.format("%dm", distanceMeters.toInt())
            }
            val compressed = compressWeekdays(it.weekday ?: noInfoText)

            VaccinationCenterData(
                id = it.placeId ?: "",
                name = it.name ?: "",
                distance = distanceText,
                hours = compressed,
                latitude = it.lat,
                longitude = it.lng,
                rawDistance = distanceMeters
            )
        }.sortedBy { it.rawDistance }

        // Update markers on map
        googleMap.clear()
        val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker)
        val smallMarker = Bitmap.createScaledBitmap(markerBitmap, 80, 95, false)

        centerItems.forEach { center ->
            val markerOptions = MarkerOptions()
                .position(LatLng(center.latitude, center.longitude))
                .title(center.name)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

            googleMap.addMarker(markerOptions)
        }

        recyclerView.visibility = View.VISIBLE
        noInfoTextView?.visibility = View.GONE

        // Connect adapter
        recyclerView.adapter = VaccinationCenterAdapter(centerItems) { selected ->
            val intent = Intent(requireContext(), MapDetailActivity::class.java).apply {
                putExtra("center_id", selected.id)
            }
            startActivity(intent)
        }
    }

    // Compress weekday information
    fun compressWeekdays(weekdayRaw: String): String {
        if (weekdayRaw.isBlank()) return getString(R.string.label_no_info)

        val dayOrder = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val dayRes = mapOf(
            "Monday" to R.string.mon,
            "Tuesday" to R.string.tue,
            "Wednesday" to R.string.wed,
            "Thursday" to R.string.thu,
            "Friday" to R.string.fri,
            "Saturday" to R.string.sat,
            "Sunday" to R.string.sun
        )

        val regex = Regex("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday):\\s*([^,]+)")
        val dayToTime = linkedMapOf<String, String>()

        regex.findAll(weekdayRaw).forEach { match ->
            val (day, timeRaw) = match.destructured
            val time = timeRaw.trim().replace("[–—-]".toRegex(), "~")
            dayToTime[day] = time
        }

        val weekday = dayOrder.subList(0, 5) // Mon–Fri
        val weekend = dayOrder.subList(5, 7) // Sat–Sun

        val weekdayTime = weekday.mapNotNull { dayToTime[it] }.distinct()
        val weekendTime = weekend.mapNotNull { dayToTime[it] }.distinct()

        val resultLines = mutableListOf<String>()

        if (weekdayTime.size == 1) {
            resultLines.add("${getString(R.string.mon_to_fri)} ${weekdayTime.first()}")
        }

        if (weekendTime.size == 1) {
            resultLines.add("${getString(R.string.sat_to_sun)} ${weekendTime.first()}")
        }

        val coveredDays = mutableSetOf<String>()
        if (weekdayTime.size == 1) coveredDays.addAll(weekday)
        if (weekendTime.size == 1) coveredDays.addAll(weekend)

        for (day in dayOrder) {
            if (dayToTime.containsKey(day) && day !in coveredDays) {
                val dayLabel = getString(dayRes[day] ?: R.string.unknown)
                resultLines.add("$dayLabel ${dayToTime[day]}")
            }
        }

        return resultLines.joinToString("\n").ifBlank {
            getString(R.string.label_no_info)
        }
    }
}