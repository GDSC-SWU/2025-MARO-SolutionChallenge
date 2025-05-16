package com.example.maro.view

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GalleryDialogFragment(
    private val onImageSelected: (Uri) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: GalleryAdapter
    private var selectedUri: Uri? = null

    override fun onStart() {
        super.onStart()

        dialog?.let { d ->
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet?.requestLayout()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_gallery_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewGallery)
        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val btnDone = view.findViewById<ImageView>(R.id.btnDone)

        adapter = GalleryAdapter(
            getSelectedUri = { selectedUri },
            onClick = { uri ->
                selectedUri = uri
                adapter.notifyDataSetChanged()


                btnDone.isEnabled = true
                btnDone.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        )
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.adapter = adapter


        btnClose.setOnClickListener {
            dismiss()
        }


        btnDone.setOnClickListener {
            selectedUri?.let {
                onImageSelected(it)
                dismiss()
            }
        }


        loadGalleryImages()
    }

    private fun loadGalleryImages() {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                imageList.add(contentUri)
            }
        }

        adapter.submitList(imageList)
    }
}
