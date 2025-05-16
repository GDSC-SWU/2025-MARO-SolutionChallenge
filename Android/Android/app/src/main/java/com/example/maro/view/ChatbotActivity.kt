package com.example.maro.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.api.RetrofitClient
import com.example.maro.api.ServerChabotResponse
import com.example.maro.model.ChatMessageData
import com.example.maro.model.SharedPrefsData
import com.example.maro.utils.FileUtils
import com.example.maro.utils.LanguageUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatbotActivity : AppCompatActivity() {

    // chat item
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerChat: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var btnSend: ImageView
    private val chatMessageList = mutableListOf<ChatMessageData>()

    // back button
    private lateinit var BackButton: ImageView

    // send image
    private lateinit var btnPhoto: ImageView

    // send image for server
    private var lastSentImageUri: Uri? = null
    private var waitingForImageText = false

    // set language
    override fun attachBaseContext(newBase: Context) {
        val langCode = LanguageUtil.getSavedLanguage(newBase) // Language code from SharedPreferences
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Request gallery permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }
        }

        // View binding
        recyclerChat = findViewById(R.id.recyclerChat)
        editMessage = findViewById(R.id.editMessage)
        btnSend = findViewById(R.id.btnSend)

        // Adapter setup
        adapter = ChatAdapter(chatMessageList)
        recyclerChat.adapter = adapter
        recyclerChat.layoutManager = LinearLayoutManager(this)

        // Show intro message at the start
        chatMessageList.add(
            ChatMessageData(isIntro = true)
        )
        adapter.notifyItemInserted(chatMessageList.size - 1)

        // Back button
        BackButton = findViewById<ImageView>(R.id.btnBack)

        BackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Send button click listener
        btnSend.setOnClickListener {
            val messageText = editMessage.text.toString().trim()

            if (messageText.isNotEmpty()) {
                val newMessage = ChatMessageData(
                    text = messageText,
                    isUser = true
                )
                chatMessageList.add(newMessage)
                adapter.notifyItemInserted(chatMessageList.size - 1)
                recyclerChat.scrollToPosition(chatMessageList.size - 1)
                editMessage.text.clear()

                // If waiting for image description, send image + text
                if (waitingForImageText && lastSentImageUri != null) {
                    sendImageAndTextToServer(lastSentImageUri!!, messageText)
                    waitingForImageText = false
                    lastSentImageUri = null
                } else {
                    sendTextOnlyToServer(messageText)
                }
            }
        }

        // Image upload button
        btnPhoto = findViewById(R.id.btnPhoto)

        btnPhoto.setOnClickListener {
            GalleryDialogFragment { selectedUri ->
                sendImageToChat(selectedUri)
            }.show(supportFragmentManager, "GalleryDialog")
        }

    }

    private fun sendImageToChat(uri: Uri) {
        val imageMessage = ChatMessageData(
            imageUri = uri,
            isUser = true
        )
        chatMessageList.add(imageMessage)
        adapter.notifyItemInserted(chatMessageList.size - 1)
        recyclerChat.scrollToPosition(chatMessageList.size - 1)

        // Save image URI + auto-response flag
        lastSentImageUri = uri
        waitingForImageText = true

        // Add chatbot auto-response after image
        Handler(Looper.getMainLooper()).postDelayed({
            val autoResponse = ChatMessageData(
                text = getString(R.string.image_description_hint),
                isUser = false
            )
            chatMessageList.add(autoResponse)
            adapter.notifyItemInserted(chatMessageList.size - 1)
            recyclerChat.scrollToPosition(chatMessageList.size - 1)
        }, 500)
    }

    // Load gallery image list
    private fun loadGalleryImages(): List<Uri> {
        val imageList = mutableListOf<Uri>()

        // Load gallery images (leave as is)
        val projection = arrayOf(android.provider.MediaStore.Images.Media._ID)
        val sortOrder = "${android.provider.MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = android.net.Uri.withAppendedPath(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                imageList.add(contentUri)
            }
        }

        return imageList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "READ_MEDIA_IMAGES permission granted")
                // Initialize logic if needed after permission granted
            } else {
                Log.d("Permission", "READ_MEDIA_IMAGES permission denied")
                // Show explanation why permission is needed
            }
        }
    }

    // Send image + text to server
    private fun sendImageAndTextToServer(imageUri: Uri, userText: String) {
        val language = SharedPrefsData.getLanguageCode(this) ?: "en"

        // Convert image file to MultipartBody.Part
        val imagePart = FileUtils.uriToMultipart(this, imageUri, "image")

        // Convert text and language to RequestBody
        val textPart = FileUtils.stringToRequestBody(userText)
        val langPart = FileUtils.stringToRequestBody(language)

        // Retrofit API call
        val api = RetrofitClient.instance
        api.sendImageAndText(imagePart, textPart, langPart)
            .enqueue(object : Callback<ServerChabotResponse> {
                override fun onResponse(call: Call<ServerChabotResponse>, response: Response<ServerChabotResponse>) {
                    val responseText = response.body()?.response ?: "No response from server"
                    chatMessageList.add(ChatMessageData(responseText, isUser = false))
                    adapter.notifyItemInserted(chatMessageList.size - 1)
                    recyclerChat.scrollToPosition(chatMessageList.size - 1)
                }

                override fun onFailure(call: Call<ServerChabotResponse>, t: Throwable) {
                    Toast.makeText(this@ChatbotActivity, "Server response failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Chatbot", " Connection failed", t)
                }
            })
    }

    // Send text only to server
    private fun sendTextOnlyToServer(messageText: String) {
        val language = SharedPrefsData.getLanguageCode(this) ?: "en"

        Log.d("DEBUG", "sendTextOnlyToServer() called")
        Log.d("DEBUG", "Sending question=$messageText, language=$language")

        val questionPart = FileUtils.stringToRequestBody(messageText)
        val languagePart = FileUtils.stringToRequestBody(language)

        RetrofitClient.instance.sendTextOnly(
            question = questionPart,
            language = languagePart
        ).enqueue(object : Callback<ServerChabotResponse> {
            override fun onResponse(
                call: Call<ServerChabotResponse>,
                response: Response<ServerChabotResponse>
            ) {
                if (response.isSuccessful) {
                    val aiResponse = response.body()?.response ?: "No response"
                    val chatMessage = ChatMessageData(aiResponse, isUser = false)
                    chatMessageList.add(chatMessage)
                    adapter.notifyItemInserted(chatMessageList.size - 1)
                    recyclerChat.scrollToPosition(chatMessageList.size - 1)
                } else {
                    Toast.makeText(this@ChatbotActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ServerChabotResponse>, t: Throwable) {
                Toast.makeText(this@ChatbotActivity, "Connection failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Chatbot", " Connection failed", t)
            }
        })
    }
}