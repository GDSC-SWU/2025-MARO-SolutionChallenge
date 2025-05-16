package com.example.maro.view

import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.data.db.AppDatabase
import com.example.maro.model.VaccineEntity
import com.example.maro.model.VaccineFeedbackEntity
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId

class DetailListAdapter(
    private val onStatusChanged: (VaccineEntity) -> Unit,
    private val onUploadRequested: (VaccineEntity) -> Unit
) : RecyclerView.Adapter<DetailListAdapter.DetailViewHolder>() {

    // Current list of vaccines being displayed
    private var items: List<VaccineEntity> = emptyList()

    // Map for storing feedback
    private var feedbackMap: Map<Int, VaccineFeedbackEntity> = emptyMap()

    // ViewHolder inner class: references to item view components
    inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutHeader: View = view.findViewById(R.id.layout_status_header) // Top background
        val tvStatus: TextView = view.findViewById(R.id.tv_status) // Status text
        val tvDateRange: TextView = view.findViewById(R.id.tv_date_range) // Date range
        val tvTitle: TextView = view.findViewById(R.id.tv_vaccine_title) // Vaccine title
        val layoutCompletedExtra: View =
            view.findViewById(R.id.layout_completed_extra) // Extra info shown after completion
        val btnUpload: MaterialCardView = view.findViewById(R.id.btn_certificate) // Upload button
        val btnGoogleCalendar: MaterialCardView =
            view.findViewById(R.id.btn_googleCalender) // Calendar button
        val tvUploadText: TextView = btnUpload.findViewById(R.id.tv_upload_text) // Text on upload button

        // Emoji buttons & text
        val btnGood: ImageView = view.findViewById(R.id.btn_good)
        val btnOkay: ImageView = view.findViewById(R.id.btn_okay)
        val btnBad: ImageView = view.findViewById(R.id.btn_bad)
        val textGood: TextView = view.findViewById(R.id.text_good)
        val textOkay: TextView = view.findViewById(R.id.text_okay)
        val textBad: TextView = view.findViewById(R.id.text_bad)
        val etSideEffect: EditText = view.findViewById(R.id.et_side_effect) // Side effect memo field
    }

    // Create item view and return ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_list, parent, false)
        return DetailViewHolder(view)
    }

    // Bind data to each vaccine item and update UI based on status
    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Multilingual status handling
        holder.tvStatus.text = context.getString(
            when (item.status) {
                "Coming up" -> R.string.label_coming_up
                "Not yet" -> R.string.label_not_yet
                "Completed" -> R.string.label_completed
                "Uploaded" -> R.string.label_completed
                else -> R.string.label_completed
            }
        )
        // Multilingual vaccine name handling
        val titleResId = context.resources.getIdentifier(
            "vaccine_${item.title.lowercase()}",
            "string",
            context.packageName
        )
        holder.tvTitle.text = if (titleResId != 0) {
            context.getString(titleResId)
        } else {
            item.title
        }
        holder.tvDateRange.text = item.dateRange

        val feedback = feedbackMap[item.id]
        val savedEmotion = feedback?.emotion
        val savedMemo = feedback?.memo

        val isFeedbackSaved = feedback != null

        holder.etSideEffect.setText(savedMemo ?: "")
        holder.etSideEffect.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        holder.etSideEffect.isEnabled = !isFeedbackSaved

        var selectedEmotion = savedEmotion

        val emotionButtons = listOf(
            holder.btnGood to holder.textGood,
            holder.btnOkay to holder.textOkay,
            holder.btnBad to holder.textBad
        )

        emotionButtons.forEach { (btn, txt) ->
            val match = when (btn.id) {
                R.id.btn_good -> "Good"
                R.id.btn_okay -> "Okay"
                R.id.btn_bad -> "Bad"
                else -> null
            }

            if (match == savedEmotion) {
                btn.setBackgroundResource(R.drawable.bg_selected_emotion)
                btn.alpha = 1f
                txt.alpha = 1f
                txt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            } else {
                btn.background = null
                btn.alpha = 0.5f
                txt.alpha = 0.5f
                txt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Neutral70))
            }

            btn.isEnabled = !isFeedbackSaved
            txt.isEnabled = !isFeedbackSaved

            btn.setOnClickListener {
                if (isFeedbackSaved) return@setOnClickListener

                emotionButtons.forEach { (b, t) ->
                    b.background = null
                    b.alpha = 0.5f
                    t.alpha = 0.5f
                }

                btn.setBackgroundResource(R.drawable.bg_selected_emotion)
                btn.alpha = 1f
                txt.alpha = 1f
                txt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))

                selectedEmotion = when (btn.id) {
                    R.id.btn_good -> "Good"
                    R.id.btn_okay -> "Okay"
                    R.id.btn_bad -> "Bad"
                    else -> null
                }
            }
        }

        // Save emoji + memo
        val saveBtn = holder.itemView.findViewById<MaterialCardView>(R.id.btn_save)
        saveBtn.isEnabled = !isFeedbackSaved
        saveBtn.visibility = if (isFeedbackSaved) View.GONE else View.VISIBLE

        // Save button for emoji + memo
        saveBtn.setOnClickListener {
            val memo = holder.etSideEffect.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getInstance(holder.itemView.context).vaccineFeedbackDao()
                dao.insertOrUpdate(
                    VaccineFeedbackEntity(
                        vaccineId = item.id,
                        emotion = selectedEmotion,
                        memo = memo
                    )
                )
                // Update status to Completed
                val updatedVaccine = item.copy(status = "Completed")
                AppDatabase.getInstance(holder.itemView.context)
                    .vaccineDao()
                    .updateVaccine(updatedVaccine)
                // Callback to update UI
                withContext(Dispatchers.Main) {
                    onStatusChanged(updatedVaccine)
                }
            }

            holder.etSideEffect.isEnabled = false
            emotionButtons.forEach { (b, t) ->
                b.isEnabled = false
                t.isEnabled = false
            }
            saveBtn.isEnabled = false
            saveBtn.visibility = View.GONE
        }

        // Handle buttons by vaccine status
        when (item.status) {
            "Not yet" -> {
                holder.layoutHeader.setBackgroundResource(R.color.notYet)
                holder.layoutCompletedExtra.visibility = View.GONE
                holder.btnUpload.alpha = 0.5f
                holder.btnUpload.isEnabled = false
                holder.btnGoogleCalendar.alpha = 1f
                holder.btnGoogleCalendar.isEnabled = true
            }
            "Coming up" -> {
                holder.layoutHeader.setBackgroundResource(R.color.comingUp)
                holder.layoutCompletedExtra.visibility = View.GONE
                holder.btnUpload.alpha = 1f
                holder.btnUpload.isEnabled = true
                holder.btnGoogleCalendar.alpha = 1f
                holder.btnGoogleCalendar.isEnabled = true
                holder.tvUploadText.text = context.getString(R.string.upload_certificate)

                // When upload button is clicked
                holder.btnUpload.setOnClickListener {
                    onUploadRequested(item)
                }
            }
            "Completed" -> {
                holder.layoutHeader.setBackgroundResource(R.color.completed)
                holder.layoutCompletedExtra.visibility = View.VISIBLE
                holder.btnUpload.alpha = 0.5f
                holder.btnUpload.isEnabled = false
                holder.btnGoogleCalendar.alpha = 0.5f
                holder.btnGoogleCalendar.isEnabled = false
                holder.tvUploadText.text = context.getString(R.string.certificate_uploaded)
            }
            "Uploaded" -> {
                holder.layoutHeader.setBackgroundResource(R.color.completed)
                holder.layoutCompletedExtra.visibility = View.VISIBLE
                holder.btnUpload.alpha = 0.5f
                holder.btnUpload.isEnabled = false
                holder.btnGoogleCalendar.alpha = 0.5f
                holder.btnGoogleCalendar.isEnabled = false
                holder.tvUploadText.text = context.getString(R.string.certificate_uploaded)
            }
        }

        // Calendar button is active only for Not yet and Coming up statuses
        if (item.status == "Not yet" || item.status == "Coming up") {
            holder.btnGoogleCalendar.setOnClickListener {
                val context = holder.itemView.context
                try {
                    val (startStr, endStr) = item.dateRange.split("~").map { it.trim() }
                    val startDate = LocalDate.parse(startStr)
                    val endDate = LocalDate.parse(endStr)

                    val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val endMillis = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                        putExtra(CalendarContract.Events.TITLE, "Vaccination: ${item.title}")
                        putExtra(CalendarContract.Events.EVENT_LOCATION, "Clinic / Hospital")
                        putExtra(CalendarContract.Events.DESCRIPTION, "Scheduled vaccine: ${item.title}")
                    }

                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Return total number of items
    override fun getItemCount(): Int = items.size

    // Called externally when list needs to be updated + feedback passed together
    fun submitList(newItems: List<VaccineEntity>, feedbackMap: Map<Int, VaccineFeedbackEntity>) {
        items = newItems
        this.feedbackMap = feedbackMap
        notifyDataSetChanged()
    }

    // Highlight selected emoji button
    private fun highlight(
        holder: DetailViewHolder,
        selectedBtn: ImageView,
        selectedText: TextView
    ) {
        listOf(
            holder.btnGood to holder.textGood,
            holder.btnOkay to holder.textOkay,
            holder.btnBad to holder.textBad
        ).forEach { (btn, txt) ->
            btn.background = null
            btn.alpha = 0.5f
            txt.alpha = 0.5f
        }
        selectedBtn.background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_selected_emotion)
        selectedBtn.alpha = 1f
        selectedText.alpha = 1f
    }
}