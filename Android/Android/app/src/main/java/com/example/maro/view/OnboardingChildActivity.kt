package com.example.maro.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.maro.R
import com.example.maro.data.db.AppDatabase
import com.example.maro.model.ChildEntity
import com.example.maro.model.SharedPrefsData
import com.example.maro.model.VaccineEntity
import com.example.maro.model.VaccineSchedule
import com.example.maro.utils.LanguageUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class OnboardingChildActivity : AppCompatActivity() {

    // Declare variables for layout binding
    private lateinit var container: LinearLayout
    private lateinit var addButton: MaterialButton
    private lateinit var continueButton: MaterialCardView
    private val childViews = mutableListOf<View>()

    // Language setting
    override fun attachBaseContext(newBase: Context) {
        val langCode = SharedPrefsData.getLanguageCode(newBase) ?: "en" // Use SharedPrefsData
        Log.d("LanguageCheck", "ChildActivity - Using langCode: $langCode") // Add log
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_onboarding_child)

        // Initialize UI
        container = findViewById(R.id.container_child_cards)
        addButton = findViewById(R.id.button_add_sibling)
        continueButton = findViewById(R.id.btnContinue)

        val radioBoy = findViewById<RadioButton>(R.id.radio_boy)
        val radioGirl = findViewById<RadioButton>(R.id.radio_girl)
        val selector = ContextCompat.getColorStateList(this, R.color.radiobutton_color)

        radioBoy.buttonTintList = selector
        radioGirl.buttonTintList = selector
        radioBoy.setTextColor(selector!!)
        radioGirl.setTextColor(selector)

        // Initially disable the Continue button
        continueButton.isEnabled = false

        // Add the initial card view which is already included in the layout
        childViews.add(container.getChildAt(0))

        // Hide the remove button in the first card
        val removeBtn = childViews[0].findViewById<ImageButton>(R.id.button_remove_child)
        removeBtn.visibility = View.GONE

        // Set input listeners for the initial card view
        setupInputListeners(childViews[0])

        // Button click listeners
        addButton.setOnClickListener { addNewChildCard() }
        continueButton.setOnClickListener { showConfirmationDialog() }
    }

    // Add a new child info card
    private fun addNewChildCard() {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.item_child_info, container, false)

        // Configure radio buttons
        val radioBoy = cardView.findViewById<RadioButton>(R.id.radio_boy)
        val radioGirl = cardView.findViewById<RadioButton>(R.id.radio_girl)
        val selector = ContextCompat.getColorStateList(this, R.color.radiobutton_color)

        radioBoy.buttonTintList = selector
        radioGirl.buttonTintList = selector
        radioBoy.setTextColor(selector!!)
        radioGirl.setTextColor(selector)

        // Configure remove button
        val removeBtn = cardView.findViewById<ImageButton>(R.id.button_remove_child)
        removeBtn.visibility = View.VISIBLE
        removeBtn.setOnClickListener {
            container.removeView(cardView)
            childViews.remove(cardView)
            checkAllFieldsValid()
        }

        // Set input listeners
        setupInputListeners(cardView)

        container.addView(cardView)
        childViews.add(cardView)
        checkAllFieldsValid()
    }

    // Set input listeners and check validity
    private fun setupInputListeners(view: View) {
        val nameEdit = view.findViewById<EditText>(R.id.edit_child_name)
        val dobEdit = view.findViewById<EditText>(R.id.edit_child_dob)
        val genderRadio = view.findViewById<RadioGroup>(R.id.radio_group_gender)

        // ① Auto format & validate date of birth
        setupDobFormatting(dobEdit)

        // ② Detect gender selection
        genderRadio.setOnCheckedChangeListener { _, _ ->
            checkAllFieldsValid()
        }

        // ③ Validate name when focus is lost
        nameEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateNameInput(nameEdit)
            }
        }

        // ④ Live validation while typing name
        nameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateNameInput(nameEdit)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Validate name input
    private fun validateNameInput(editText: EditText) {
        val input = editText.text.toString()
        val hasNumberOrEmoji = input.contains(Regex("[0-9]")) || input.contains(Regex("[\\p{So}\\p{Cn}]"))

        if (hasNumberOrEmoji) {
            // Apply warning style
            editText.setTextColor(ContextCompat.getColor(this, R.color.Main02))
            editText.background = ContextCompat.getDrawable(this, R.drawable.edittext_error_background)
            editText.error = "Numbers and emojis are not allowed."
        } else {
            // Restore normal style
            editText.setTextColor(ContextCompat.getColor(this, R.color.black))
            editText.background = ContextCompat.getDrawable(this, R.drawable.bg_rounded_edittext)
            editText.error = null
        }

        checkAllFieldsValid()
    }

    // Validate date of birth input
    private fun validateDobInput(editText: EditText) {
        val input = editText.text.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val isValid = try {
            val date = LocalDate.parse(input, formatter)
            val today = LocalDate.now()
            !date.isAfter(today) // Prevent future dates
        } catch (e: Exception) {
            false
        }

        if (!isValid) {
            editText.setTextColor(ContextCompat.getColor(this, R.color.Main02))
            editText.background = ContextCompat.getDrawable(this, R.drawable.edittext_error_background)
            editText.error = "Please enter a valid date."
        } else {
            editText.setTextColor(ContextCompat.getColor(this, R.color.black))
            editText.background = ContextCompat.getDrawable(this, R.drawable.bg_rounded_edittext)
            editText.error = null
        }

        checkAllFieldsValid()
    }

    // Format date input
    private fun setupDobFormatting(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                val input = s.toString().replace("-", "").take(8)
                if (input == current) return

                val sb = StringBuilder()
                var cursorPos = input.length

                if (input.length >= 4) {
                    sb.append(input.substring(0, 4)).append("-")
                    if (input.length >= 6) {
                        sb.append(input.substring(4, 6)).append("-")
                        sb.append(input.substring(6))
                    } else {
                        sb.append(input.substring(4))
                    }
                } else {
                    sb.append(input)
                }

                current = input
                isFormatting = true
                editText.setText(sb.toString())
                editText.setSelection(sb.length.coerceAtMost(editText.text.length))
                isFormatting = false

                // Only validate when full date is entered
                if (input.length == 8) {
                    validateDobInput(editText)
                } else {
                    editText.error = null
                    editText.setTextColor(ContextCompat.getColor(editText.context, R.color.black))
                    editText.background = ContextCompat.getDrawable(editText.context, R.drawable.bg_rounded_edittext)
                }

                checkAllFieldsValid() // Always check to toggle Continue button
            }
        })
    }

    // Check whether all fields are valid
    private fun checkAllFieldsValid() {
        var isAllValid = true

        for (view in childViews) {
            val name = view.findViewById<EditText>(R.id.edit_child_name).text.toString().trim()
            val dob = view.findViewById<EditText>(R.id.edit_child_dob).text.toString().trim()
            val genderGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)
            val genderSelected = genderGroup.checkedRadioButtonId != -1

            val isDobValid = dob.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                    try {
                        val date = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        !date.isAfter(LocalDate.now())
                    } catch (e: Exception) {
                        false
                    }

            if (name.isEmpty() || !isDobValid || !genderSelected) {
                isAllValid = false
                break
            }
        }

        // Update button UI
        continueButton.isEnabled = isAllValid
        continueButton.backgroundTintList = ContextCompat.getColorStateList(
            this,
            if (isAllValid) R.color.Main01 else R.color.Neutral80
        )
    }

    // Show confirmation dialog on complete
    private fun showConfirmationDialog() {
        // Initialize DB
        val db = AppDatabase.getInstance(this)
        val childDao = db.childDao()
        val vaccineDao = db.vaccineDao()

        // Run in background
        lifecycleScope.launch {
            for (view in childViews) {
                // Extract name, dob, gender
                val name = view.findViewById<EditText>(R.id.edit_child_name).text.toString().trim()
                val dobText = view.findViewById<EditText>(R.id.edit_child_dob).text.toString().trim()
                val genderGroup = view.findViewById<RadioGroup>(R.id.radio_group_gender)
                val genderId = genderGroup.checkedRadioButtonId
                val gender = when (genderId) {
                    R.id.radio_boy -> "boy"
                    R.id.radio_girl -> "girl"
                    else -> "unknown"
                }

                // Convert dob to months
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val dob = LocalDate.parse(dobText, formatter)
                val months = calculateMonthsFromBirth(dob)

                // 1. Save child info
                val child = ChildEntity(name = name, gender = gender, months = months, birth = dobText)
                val childId = childDao.insertChild(child)

                // 2. Generate and save vaccine list
                val vaccines = createVaccineList(childId.toInt(), dob, gender)
                vaccineDao.insertVaccines(vaccines)
            }

            // After saving → show dialog
            val dialogView = LayoutInflater.from(this@OnboardingChildActivity)
                .inflate(R.layout.dialog_congratulations, null)
            val dialog = AlertDialog.Builder(this@OnboardingChildActivity)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val confirmBtn = dialogView.findViewById<MaterialButton>(R.id.button_go_home)
            confirmBtn.setOnClickListener {
                // Onboarding complete
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                prefs.edit().putBoolean("onboarding_completed", true).apply()

                dialog.dismiss()
                val intent = Intent(this@OnboardingChildActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    // Calculate months from birth
    private fun calculateMonthsFromBirth(birthDate: LocalDate): Int {
        val today = LocalDate.now()
        val period = Period.between(birthDate, today)
        return period.years * 12 + period.months
    }

    // Generate vaccine list
    fun createVaccineList(childId: Int, dob: LocalDate, gender: String): List<VaccineEntity> {
        val today = LocalDate.now()
        val vaccines = mutableListOf<VaccineEntity>()

        for ((name, monthRange, label) in VaccineSchedule.schedule) {
            // Skip HPV for boys
            if (name == "HPV" && gender != "girl") continue

            val startDate = dob.plusMonths(monthRange.first.toLong())
            val endDate = dob.plusMonths(monthRange.last.toLong())

            val status = when {
                today.isBefore(startDate) -> "Not yet"
                today.isAfter(endDate) -> "Completed"
                else -> "Coming up"
            }

            vaccines.add(
                VaccineEntity(
                    childId = childId,
                    title = label,
                    dateRange = "$startDate ~ $endDate",
                    status = status
                )
            )
        }

        return vaccines
    }
}