package com.example.maro.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maro.R
import com.example.maro.model.ChatMessageData

class ChatAdapter(private val messages: List<ChatMessageData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_INTRO = 0
        private const val VIEW_TYPE_USER_TEXT = 1
        private const val VIEW_TYPE_USER_IMAGE = 2
        private const val VIEW_TYPE_AI_TEXT = 3
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isIntro -> VIEW_TYPE_INTRO
            message.isUser && message.imageUri != null -> VIEW_TYPE_USER_IMAGE
            message.isUser -> VIEW_TYPE_USER_TEXT
            else -> VIEW_TYPE_AI_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_INTRO -> {
                val view = inflater.inflate(R.layout.item_chat_intro, parent, false)
                IntroViewHolder(view)
            }
            VIEW_TYPE_USER_TEXT -> {
                val view = inflater.inflate(R.layout.item_user_talk, parent, false)
                UserTextViewHolder(view)
            }
            VIEW_TYPE_USER_IMAGE -> {
                val view = inflater.inflate(R.layout.item_user_image, parent, false)
                UserImageViewHolder(view)
            }
            VIEW_TYPE_AI_TEXT -> {
                val view = inflater.inflate(R.layout.item_ai_talk, parent, false)
                AiTextViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder) {
            is IntroViewHolder -> { /* static intro, nothing to bind */ }
            is UserTextViewHolder -> holder.bind(message)
            is UserImageViewHolder -> holder.bind(message)
            is AiTextViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    // ViewHolders
    class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // User 채팅 text
    class UserTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.txtUserMessage)
        fun bind(message: ChatMessageData) {
            textView.text = message.text
        }
    }

    // User 채팅 image
    class UserImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgUserMessage)
        fun bind(message: ChatMessageData) {
            Glide.with(itemView).load(message.imageUri).into(imageView)

            // 이미지 클릭 시 전체화면 액티비티로 전환
            imageView.setOnClickListener {
                val intent = Intent(itemView.context, ImageFullscreenActivity::class.java)
                intent.putExtra("imageUri", message.imageUri)
                itemView.context.startActivity(intent)
            }
        }
    }

    // AI 채팅
    class AiTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.txtAiMessage)
        fun bind(message: ChatMessageData) {
            textView.text = message.text
        }
    }
}
