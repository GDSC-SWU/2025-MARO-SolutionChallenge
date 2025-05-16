package com.example.maro.view

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.maro.R

class ImageFullscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_fullscreen)

        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        val imageView = findViewById<ImageView>(R.id.imgFullscreen)

        Glide.with(this)
            .load(imageUri)
            .into(imageView)

        findViewById<ImageView>(R.id.btnClose).setOnClickListener {
            finish()
        }
    }
}
