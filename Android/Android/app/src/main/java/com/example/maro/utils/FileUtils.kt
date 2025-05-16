package com.example.maro.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    // Uri → MultipartBody.Part 로 변환 (이미지 파일 전송용)
    fun uriToMultipart(context: Context, uri: Uri, paramName: String): MultipartBody.Part {
        // 1. 이미지 URI로부터 입력 스트림 열기
        val inputStream = context.contentResolver.openInputStream(uri)

        // 2. 앱의 임시 캐시 디렉토리에 이미지 파일 생성
        val file = File(context.cacheDir, "upload_image.jpg")

        // 3. 출력 스트림으로 URI 데이터를 파일로 복사
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        // 4. 파일 → RequestBody 생성 (MIME 타입은 image/* 로 지정)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)

        // 5. Multipart 파트로 변환 (서버 파라미터명, 파일명, 데이터)
        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
    }

    // 일반 문자열을 RequestBody로 변환 (예: 언어 코드 전송)
    fun stringToRequestBody(text: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), text)
    }
}
