package com.example.maro.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.maro.R
import com.example.maro.databinding.ActivityLoginBinding
import com.example.maro.utils.LanguageUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    // set Language
    override fun attachBaseContext(newBase: Context) {
        val langCode = LanguageUtil.getSavedLanguage(newBase)
        val context = LanguageUtil.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        // test Image
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val dummyAdded = prefs.getBoolean("dummy_image_added", false)
        if (!dummyAdded) {
            val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.chatimage)
            val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.upload_image)
            val bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.imag22)

            insertImageToGallery(bitmap1, "chat_image_sample.jpg")
            insertImageToGallery(bitmap2, "vaccine_certificate_sample.jpg")
            insertImageToGallery(bitmap3, "vaccine_certificate_sample.jpg")

            prefs.edit().putBoolean("dummy_image_added", true).apply()
        }
    }

    // Google login
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("GoogleLogin", "성공: ${account.email}")

                // 로그인 성공 후 온보딩 체크
                val prefs = getSharedPreferences("settings", MODE_PRIVATE)
                val onboardingDone = prefs.getBoolean("onboarding_completed", false)

                val nextActivity = if (onboardingDone) {
                    MainActivity::class.java
                } else {
                    OnboardingLanguageActivity::class.java
                }

                val intent = Intent(this, nextActivity)
                startActivity(intent)
                finish()
            } catch (e: ApiException) {
                Log.w("GoogleLogin", "Login Fail: ${e.statusCode}", e)
                Toast.makeText(this, "Google Login Fail: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    // for Test save Gallery
    private fun insertImageToGallery(bitmap: android.graphics.Bitmap, filename: String) {
        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    }
}
