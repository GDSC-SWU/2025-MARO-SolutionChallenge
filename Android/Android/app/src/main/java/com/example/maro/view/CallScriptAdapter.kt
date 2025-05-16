package com.example.maro.adapter

import android.content.Context
import android.media.MediaPlayer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.model.CallScriptItem
import java.io.File
import java.io.FileOutputStream

class CallScriptAdapter(
    private val context: Context,
    private val onTextChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val TYPE_INPUT = 0      // EditText input view
        private const val TYPE_QUESTION = 1   // Question card
        private const val TYPE_ANSWER = 2     // Answer card
    }

    private val items = mutableListOf<CallScriptItem>()
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        super.onAttachedToRecyclerView(rv)
        recyclerView = rv
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CallScriptItem.Input -> TYPE_INPUT
            is CallScriptItem.Question -> TYPE_QUESTION
            is CallScriptItem.Answer -> TYPE_ANSWER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_INPUT -> {
                val view = inflater.inflate(R.layout.item_call_script_eidttext, parent, false)
                InputViewHolder(view)
            }
            TYPE_QUESTION -> {
                val view = inflater.inflate(R.layout.item_call_script_question, parent, false)
                QuestionViewHolder(view)
            }
            TYPE_ANSWER -> {
                val view = inflater.inflate(R.layout.item_call_script_answer, parent, false)
                AnswerViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CallScriptItem.Input -> (holder as InputViewHolder).bind(item, position)
            is CallScriptItem.Question -> (holder as QuestionViewHolder).bind(item)
            is CallScriptItem.Answer -> (holder as AnswerViewHolder).bind(item)
        }
    }

    // ViewHolder for input EditText
    inner class InputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val editText: EditText = itemView.findViewById(R.id.editTextQuestion)
        private var watcher: TextWatcher? = null

        fun bind(item: CallScriptItem.Input, position: Int) {
            // Remove old watcher to prevent duplicate triggering
            watcher?.let { editText.removeTextChangedListener(it) }

            editText.setText(item.text)

            // Force visible and layout params reset
            itemView.visibility = View.VISIBLE
            itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Apply enabled/disabled state
            editText.isEnabled = item.isEditable

            // Apply background based on editable state
            val bgRes = if (item.isEditable) {
                R.drawable.bg_rounded_edittext
            } else {
                R.drawable.bg_rounded_grey_edittext
            }
            editText.setBackgroundResource(bgRes)

            // Add TextWatcher to update internal item state
            watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    items[position] = item.copy(text = s.toString())
                    onTextChanged?.invoke()
                }
            }

            editText.addTextChangedListener(watcher)
        }
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textLanguage: TextView = itemView.findViewById(R.id.text_language)
        private val textEnglish: TextView = itemView.findViewById(R.id.text_english)
        private val textKorean: TextView = itemView.findViewById(R.id.text_korean1)
        private val koreanSpeaker: ImageView = itemView.findViewById(R.id.korean_speaker)


        fun bind(item: CallScriptItem.Question) {
            // Set language label
            textLanguage.text = item.language

            // Show original English question text
            textEnglish.text = item.originalText

            // Show Korean translated text
            textKorean.text = item.text

            // Connect Korean audio playback to speaker button
            koreanSpeaker.setOnClickListener {
                if (item.audioUrl.isNotEmpty()) {
                    playBase64Audio(item.audioUrl)
                } else {
                    Toast.makeText(itemView.context, "Audio not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ViewHolder for displaying an AI-generated answer
    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textAnswer: TextView = itemView.findViewById(R.id.label_answer)
        private val textLanguage: TextView = itemView.findViewById(R.id.text_language)
        private val textEnglish: TextView = itemView.findViewById(R.id.text_english)
        private val textKorean: TextView = itemView.findViewById(R.id.text_korean)
        private val keywordText1: TextView = itemView.findViewById(R.id.text_keyword1)
        private val keywordText2: TextView = itemView.findViewById(R.id.text_keyword2)
        private val keywordText3: TextView = itemView.findViewById(R.id.text_keyword3)
        private val keywordIcon1: ImageView = itemView.findViewById(R.id.text_keyword_img)
        private val keywordIcon2: ImageView = itemView.findViewById(R.id.text_keyword_img2)
        private val keywordIcon3: ImageView = itemView.findViewById(R.id.text_keyword_img3)
        private val koreanSpeaker: ImageView = itemView.findViewById(R.id.korean_speaker)

        fun bind(item: CallScriptItem.Answer) {

            // Set text label based on answer index
            val label = when (item.answerIndex) {
                1 -> context.getString(R.string.call_script_suggestion_1)
                2 -> context.getString(R.string.call_script_suggestion_2)
                3 -> context.getString(R.string.call_script_suggestion_3)
                else -> "Suggested answer"
            }
            textAnswer.text = label

            // Set basic text fields
            textLanguage.text = item.language
            textEnglish.text = item.textEnglish
            textKorean.text = item.textKorean

            // Main audio for Korean sentence
            koreanSpeaker.setOnClickListener {
                playBase64Audio(item.audioUrl)
            }

            // Bind up to 3 keywords and their corresponding audios
            val keywords = item.keywords
            val audios = item.keywordAudios

            keywordText1.text = keywords.getOrNull(0) ?: ""
            keywordText2.text = keywords.getOrNull(1) ?: ""
            keywordText3.text = keywords.getOrNull(2) ?: ""

            keywordIcon1.setOnClickListener {
                playBase64Audio(audios.getOrNull(0) ?: item.audioUrl)
            }
            keywordIcon2.setOnClickListener {
                playBase64Audio(audios.getOrNull(1) ?: item.audioUrl)
            }
            keywordIcon3.setOnClickListener {
                playBase64Audio(audios.getOrNull(2) ?: item.audioUrl)
            }
        }
    }

    // Replace all items with a new list
    fun updateItems(newItems: List<CallScriptItem>) {
        Log.d("UPDATE_ITEMS", "→ items.clear() 완료")
        items.clear()

        Log.d("UPDATE_ITEMS", "→ items.addAll() 호출")
        items.addAll(newItems)

        Log.d("UPDATE_ITEMS", "→ notifyDataSetChanged() 호출")
        notifyDataSetChanged()
    }

    // Return current item list
    fun getItems(): List<CallScriptItem> = items

    // Return last input position in the list
    fun getLastInputPosition(): Int {
        return items.indexOfLast { it is CallScriptItem.Input }
    }

    // Return current text of the last input field
    fun getLastInputText(): String {
        val inputItem = items.lastOrNull { it is CallScriptItem.Input } as? CallScriptItem.Input
        return inputItem?.text.orEmpty()
    }
    // play Audio From Server
    @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
    fun playBase64Audio(base64Audio: String) {
        try {
            // 1. Decode Base64 to byte array
            val audioBytes = android.util.Base64.decode(base64Audio, android.util.Base64.DEFAULT)

            // 2. Write to temporary WAV file
            val tempFile = File.createTempFile("audio", ".wav", context.cacheDir)
            FileOutputStream(tempFile).use { it.write(audioBytes) }

            // 3. Play with MediaPlayer
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    release()
                    tempFile.delete() // Clean up
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayback", "Base64 audio playback error", e)
            Toast.makeText(context, "Base64 audio playback failed", Toast.LENGTH_SHORT).show()
        }
    }
    // Audio Stop
    fun stopAudioIfPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}