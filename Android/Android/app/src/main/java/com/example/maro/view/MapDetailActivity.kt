package com.example.maro.view

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maro.R
import com.example.maro.api.HospitalDto
import com.example.maro.api.PlaceIdRequest
import com.example.maro.api.RetrofitClient
import com.example.maro.utils.LanguageUtil
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapDetailActivity : AppCompatActivity() {

    // set language
    override fun attachBaseContext(newBase: Context) {
        val langCode = LanguageUtil.getSavedLanguage(newBase) // SharedPreferences 등에서 가져온 언어 코드
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_map_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // back Btn
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // 현재 액티비티 종료 → 뒤로가기
        }

        // google map Btn
        val mapBtn = findViewById<View>(R.id.btnGoogleMap)
        mapBtn.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=Seoul+guangjin-gu+cheonhodaero+537")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        val centerId = intent.getStringExtra("center_id")

        // lang code, plceId -> for server
        val targetLang = LanguageUtil.getSavedLanguage(this) //  ex. "th", "en"
        val placeId = intent.getStringExtra("center_id") ?: return

        // request
        val request = PlaceIdRequest(placeId)
        val call = RetrofitClient.hospitalApi.getTranslatedHospitalDetails(request, targetLang)

        call.enqueue(object : Callback<HospitalDto> {
            override fun onResponse(call: Call<HospitalDto>, response: Response<HospitalDto>) {
                if (response.isSuccessful) {
                    val hospital = response.body() ?: return

                    // set title center name
                    findViewById<TextView>(R.id.tv_title)?.text = hospital.name
                    findViewById<TextView>(R.id.tv_name)?.text = hospital.name

                    // address
                    findViewById<TextView>(R.id.tv_address)?.apply {
                        val isEmpty = hospital.address.isNullOrEmpty()
                        text = if (isEmpty) getString(R.string.label_no_info) else hospital.address
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                if (isEmpty) R.color.Neutral70 else R.color.Neutral0
                            )
                        )
                    }

                    // call
                    findViewById<TextView>(R.id.tv_phone)?.apply {
                        val isEmpty = hospital.phone.isNullOrEmpty()
                        text = if (isEmpty) getString(R.string.label_no_info) else hospital.phone
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                if (isEmpty) R.color.Neutral70 else R.color.Neutral0
                            )
                        )
                    }

                    // hours
                    findViewById<TextView>(R.id.tv_hours)?.apply {
                        text = compressWeekdays(hospital.weekday ?: "")
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                if (hospital.weekday.isNullOrBlank()) R.color.Neutral70 else R.color.Neutral0
                            )
                        )
                    }

                    // map fragment
                    val fragment = SingleMapFragment.newInstance(hospital.lat, hospital.lng)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.map_container, fragment)
                        .commit()

                    // add chip
                    val chipGroup = findViewById<FlexboxLayout>(R.id.vaccine_chip_group)
                    val noInfoText = findViewById<TextView>(R.id.noInformation)

                    chipGroup.removeAllViews()

                    // vaccine chip
                    if (hospital.vaccines.isNullOrEmpty()) {
                        // chip X : visible text
                        chipGroup.visibility = View.GONE
                        noInfoText?.visibility = View.VISIBLE
                        noInfoText?.text = getString(R.string.label_no_info)
                    } else {
                        // chip O
                        chipGroup.visibility = View.VISIBLE
                        noInfoText?.visibility = View.GONE

                        hospital.vaccines.forEach { vaccine ->
                            val chip = Chip(this@MapDetailActivity).apply {
                                text = vaccine
                                isClickable = false
                                isCheckable = false
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                setTextColor(ContextCompat.getColor(context, R.color.Neutral0))
                                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Neutral98))
                                chipStrokeWidth = 0f
                                chipCornerRadius = 50f

                                val marginHorizontal = resources.getDimensionPixelSize(R.dimen.chip_margin_horizontal_3dp)
                                val marginVertical = resources.getDimensionPixelSize(R.dimen.chip_margin_vertical_1dp)
                                layoutParams = ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical)
                                }
                            }
                            chipGroup.addView(chip)
                        }
                    }

                    // google map Btn click -> address send
                    val mapBtn = findViewById<View>(R.id.btnGoogleMap)
                    mapBtn.setOnClickListener {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${hospital.address}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    }
                }
            }

            override fun onFailure(call: Call<HospitalDto>, t: Throwable) {
                // 실패 처리
                Log.e("MapDetail", "Fail", t)
            }
        })
    }
    override fun onResume() {
        super.onResume()
    }

    // set format Weekday
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

        // day group
        val weekday = dayOrder.subList(0, 5) // Mon–Fri
        val weekend = dayOrder.subList(5, 7) // Sat–Sun

        val weekdayTime = weekday.mapNotNull { dayToTime[it] }.distinct()
        val weekendTime = weekend.mapNotNull { dayToTime[it] }.distinct()

        val resultLines = mutableListOf<String>()

        // weekday group
        if (weekdayTime.size == 1) {
            resultLines.add("${getString(R.string.mon_to_fri)} ${weekdayTime.first()}")
        }

        // weekend group
        if (weekendTime.size == 1) {
            resultLines.add("${getString(R.string.sat_to_sun)} ${weekendTime.first()}")
        }

        // else
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
