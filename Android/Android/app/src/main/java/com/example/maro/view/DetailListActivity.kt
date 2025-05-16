package com.example.maro.view

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.api.RetrofitClient
import com.example.maro.data.db.AppDatabase
import com.example.maro.model.SharedPrefsData
import com.example.maro.model.VaccineEntity
import com.example.maro.utils.LanguageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: DetailListAdapter

    private var childId: Int = -1
    private var allVaccines: List<VaccineEntity> = emptyList()
    private var currentFilter: String = "ALL"
    private var sortByLatest: Boolean = true
    private var sortOrder: String = "Latest"

    private var selectedVaccine: VaccineEntity? = null

    // Request gallery permission
    companion object {
        private const val REQUEST_CODE_GALLERY = 1001
    }

    // Language setting
    override fun attachBaseContext(newBase: Context) {
        val langCode = SharedPrefsData.getLanguageCode(newBase) ?: "en" // Using SharedPrefsData
        Log.d("LanguageCheck", "ChildActivity - Using langCode: $langCode") // Add log
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_list)

        db = AppDatabase.getInstance(this)
        childId = intent.getIntExtra("child_id", -1)

        // Set layout manager
        findViewById<RecyclerView>(R.id.recycler_vaccine_detail).layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)

        // Create adapter and attach to RecyclerView
        adapter = DetailListAdapter(
            onStatusChanged = { vaccine -> handleStatusChange(vaccine) },
            onUploadRequested = { vaccine ->
                selectedVaccine = vaccine
                checkGalleryPermissionAndShowDialog()
            }
        )
        findViewById<RecyclerView>(R.id.recycler_vaccine_detail).adapter = adapter

        // Back button
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        setupFilterAndSort()
        loadChildAndVaccines()
    }

    // Set click listeners for filter and sort buttons
    private fun setupFilterAndSort() {
        // Sort buttons
        val sortOldest = findViewById<TextView>(R.id.tvOldest)
        val sortLatest = findViewById<TextView>(R.id.tvLatest)

        val allSorts = listOf(sortOldest, sortLatest)

        // Filter buttons
        val filters = mapOf(
            "ALL" to findViewById<TextView>(R.id.btn_filter_all),
            "Not yet" to findViewById<TextView>(R.id.btn_filter_notyet),
            "Coming up" to findViewById<TextView>(R.id.btn_filter_comingUp),
            "Completed" to findViewById<TextView>(R.id.btn_filter_completed)
        )
        filters["ALL"]?.text = getString(R.string.tab_all)
        filters["Not yet"]?.text = getString(R.string.tab_not_yet)
        filters["Coming up"]?.text = getString(R.string.tab_coming_up)
        filters["Completed"]?.text = getString(R.string.tab_completed)

        // Reflect initial filter selection state
        filters.forEach { (status, btn) ->
            if (status == currentFilter) {
                btn.setTypeface(null, Typeface.BOLD)
            } else {
                btn.setTypeface(null, Typeface.NORMAL)
            }
        }

        filters.forEach { (status, btn) ->
            btn.setOnClickListener {
                currentFilter = status

                // Bold selected filter text
                filters.values.forEach { it.setTypeface(null, Typeface.NORMAL) }
                moveIndicatorToButton(btn)
                btn.setTypeface(null, Typeface.BOLD)
                updateRecyclerView()
            }
        }

        findViewById<TextView>(R.id.tvLatest).setOnClickListener {
            sortByLatest = true
            sortOrder = "Latest"

            allSorts.forEach {
                it.setTextColor(ContextCompat.getColor(this, R.color.Neutral70))
            }

            sortLatest.setTextColor(ContextCompat.getColor(this, R.color.Neutral0))

            updateRecyclerView()
        }

        findViewById<TextView>(R.id.tvOldest).setOnClickListener {
            sortByLatest = false
            sortOrder = "Oldest"

            // Reset all button colors
            allSorts.forEach {
                it.setTextColor(ContextCompat.getColor(this, R.color.Neutral70))
            }

            // Highlight selected button
            sortOldest.setTextColor(ContextCompat.getColor(this, R.color.Neutral0))
            updateRecyclerView()
        }
        sortOldest.text = getString(R.string.sort_oldest)
        sortLatest.text = getString(R.string.sort_latest)
    }

    // Load child info and vaccine list from DB
    private fun loadChildAndVaccines() {
        lifecycleScope.launch {
            val childDao = db.childDao()
            val vaccineDao = db.vaccineDao()

            val child = withContext(Dispatchers.IO) {
                childDao.getChildById(childId)
            }

            child?.let {
                // Update child info card view
                findViewById<TextView>(R.id.tv_child_name).text = it.name
                findViewById<TextView>(R.id.tvChildName).text = it.name
                findViewById<TextView>(R.id.tvChildBirth).text = it.birth
                val genderResId =
                    if (it.gender == "boy") R.string.child_gender_boy else R.string.child_gender_girl
                val genderText = getString(genderResId)

                // Change ImageView based on gender
                val assetImageView = findViewById<ImageView>(R.id.tvAsset)
                val imageResId = if (it.gender == "boy") R.drawable.ic_boy else R.drawable.ic_girl
                assetImageView.setImageResource(imageResId)

                val ageText = if (it.months < 36) {
                    getString(R.string.child_age_m, it.months)
                } else {
                    getString(R.string.child_age_y, it.months / 12)
                }

                findViewById<TextView>(R.id.tvChildInfo).text = "$genderText, $ageText"
            }

            allVaccines = withContext(Dispatchers.IO) {
                vaccineDao.getVaccinesByChildId(childId)
            }
            val feedbacks = withContext(Dispatchers.IO) {
                db.vaccineFeedbackDao().getAllFeedbacks()
            }
            val feedbackMap = feedbacks.associateBy { it.vaccineId }

            updateRecyclerView()
        }
    }

    // Apply filter + sort and update adapter
    private fun updateRecyclerView() {
        // Prepare feedbackMap
        val feedbackMap = runBlocking {
            db.vaccineFeedbackDao().getAllFeedbacks().associateBy { it.vaccineId }
        }

        val filtered = when (currentFilter) {
            "Not yet" -> allVaccines.filter { it.status == "Not yet" }
            "Coming up" -> allVaccines.filter { it.status == "Coming up" || it.status == "Uploaded" }
            "Completed" -> allVaccines.filter { it.status == "Completed" }
            else -> allVaccines
        }

        val sorted = filtered.sortedWith(
            compareBy {
                val startDate = it.dateRange.split("~")[0].trim()
                val epoch = LocalDate.parse(startDate).toEpochDay()
                if (sortByLatest) -epoch else epoch
            }
        )

        Log.d("FilterDebug", "filtered count = ${filtered.size}")
        filtered.forEach {
            Log.d("FilterDebug", "→ ${it.title} | ${it.status} | ${it.dateRange}")
        }

        // Show empty view if no items
        val emptyView = findViewById<View>(R.id.emptyPlaceholder)
        if (sorted.isEmpty()) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }

        adapter.submitList(sorted.map { it.copy() }, feedbackMap)

        // Top vaccine info card
        val upcomingCount = allVaccines.count { it.status == "Coming up" }
        val vaccineInfoText = getString(R.string.banner_upcoming_vaccines, upcomingCount)
        findViewById<TextView>(R.id.tvVaccineInfo).text = vaccineInfoText
    }

    // Called on status change → update DB and refresh list
    private fun handleStatusChange(vaccine: VaccineEntity) {
        lifecycleScope.launch {
            val updated = vaccine.copy(status = "Completed")

            withContext(Dispatchers.IO) {
                db.vaccineDao().updateVaccine(updated)
            }

            allVaccines = allVaccines.map {
                if (it.id == vaccine.id) updated else it
            }

            setResult(RESULT_OK)

            updateRecyclerView()
        }
    }

    // Move indicator under selected button
    private fun moveIndicatorToButton(button: View) {
        val indicator = findViewById<View>(R.id.filter_indicator)

        indicator.post {
            val textView = button as? TextView ?: return@post
            val textCenterX = textView.x + textView.width / 2f

            val indicatorWidth = indicator.width.takeIf { it > 0 } ?: run {
                indicator.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
                )
                indicator.measuredWidth
            }

            val targetX = textCenterX - (indicatorWidth / 2f)
            indicator.translationX = targetX
        }
    }

    // Handle certificate image upload
    private fun handleImageUploadCompleted(uri: Uri) {
        Toast.makeText(this, "Uploading certificate...", Toast.LENGTH_SHORT).show()

        selectedVaccine?.let { vaccine ->
            // 1. Localization: resource ID → string
            val resName = "vaccine_${vaccine.title.lowercase()}"
            val titleIdKo = resources.getIdentifier(resName, "string", packageName)
            val vaccineKo = if (titleIdKo != 0) getString(titleIdKo) else vaccine.title

            val titleIdEn =
                resources.getIdentifier(vaccine.title, "string", Locale.ENGLISH.language)
            val vaccineEn = if (titleIdEn != 0) getString(titleIdEn) else vaccine.title

            // yyyy-MM-dd ~ yyyy-MM-dd → yyyy.MM.dd~yyyy.MM.dd
            val rawPeriod = vaccine.dateRange
            val formattedPeriod = try {
                val parts = rawPeriod.split("~").map { it.trim() }
                val start = LocalDate.parse(parts[0])
                val end = LocalDate.parse(parts[1])
                "${start.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}~${
                    end.format(
                        DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    )
                }"
            } catch (e: Exception) {
                Log.e("PeriodFormat", "Date format conversion failed: $rawPeriod", e)
                rawPeriod
            }

            // 2. Convert to RequestBody
            val koBody = vaccineKo.toRequestBody("text/plain".toMediaTypeOrNull())
            val enBody = vaccineEn.toRequestBody("text/plain".toMediaTypeOrNull())
            val periodBody = formattedPeriod.toRequestBody("text/plain".toMediaTypeOrNull())

            // 3. Convert URI to File and then MultipartBody.Part
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val imageBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, imageBody)

            // 5. Log upload data
            Log.d(
                "UploadPayload",
                """
    🔽 Upload certificate request:
    - vaccine_ko: $vaccineKo
    - vaccine_en: $vaccineEn
    - period: $formattedPeriod
    - image name: ${tempFile.name}
    - image size: ${tempFile.length()} bytes
                """.trimIndent()
            )

            // 4. Retrofit API call
            RetrofitClient.vaccineUploadApi.uploadCertificate(
                vaccineKo = koBody,
                vaccineEn = enBody,
                period = periodBody,
                image = imagePart
            ).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful && response.body() == true) {
                        val updated = vaccine.copy(status = "Uploaded")

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                AppDatabase.getInstance(this@DetailListActivity)
                                    .vaccineDao()
                                    .updateVaccine(updated)
                            }

                            allVaccines = allVaccines.map {
                                if (it.id == vaccine.id) updated else it
                            }
                            updateRecyclerView()
                            Toast.makeText(
                                this@DetailListActivity,
                                getString(R.string.upload_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@DetailListActivity,
                            getString(R.string.upload_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Upload", "Server response error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(
                        this@DetailListActivity,
                        getString(R.string.upload_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Upload", "Network error", t)
                }
            })
        }
    }

    // Check permission
    private fun checkGalleryPermissionAndShowDialog() {
        val permission = android.Manifest.permission.READ_MEDIA_IMAGES
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            showGalleryDialog()
        } else {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_CODE_GALLERY
            )
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_GALLERY &&
            grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            showGalleryDialog()
        } else {
            Toast.makeText(
                this,
                "Gallery permission is required to upload image.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Show gallery dialog
    private fun showGalleryDialog() {
        val dialog = GalleryDialogFragment { uri ->
            handleImageUploadCompleted(uri)
        }
        dialog.show(supportFragmentManager, "GalleryDialog")
    }
}