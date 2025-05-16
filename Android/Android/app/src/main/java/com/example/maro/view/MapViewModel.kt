package com.example.maro.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.maro.api.HospitalDto
import com.example.maro.api.LocationRequest
import com.example.maro.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapViewModel : ViewModel() {

    // Map fragment ViewModel
    var lastLat: Double? = null
    var lastLng: Double? = null

    private val _hospitalList = MutableLiveData<List<HospitalDto>>()
    val hospitalList: LiveData<List<HospitalDto>> = _hospitalList

    fun fetchNearbyHospitals(lat: Double, lng: Double, lang: String) {
        val request = LocationRequest(lat, lng, lang)

        lastLat = lat
        lastLng = lng
        RetrofitClient.hospitalApi.getTranslatedNearbyHospitals(request, lang)
            .enqueue(object : Callback<List<HospitalDto>> {
                override fun onResponse(
                    call: Call<List<HospitalDto>>,
                    response: Response<List<HospitalDto>>
                ) {
                    if (response.isSuccessful) {
                        _hospitalList.value = response.body()
                    } else {
                        _hospitalList.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<List<HospitalDto>>, t: Throwable) {
                    _hospitalList.value = emptyList()
                }
            })
    }
}
