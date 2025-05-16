package com.example.maro.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maro.R

class GalleryAdapter(
    private val getSelectedUri: () -> Uri?, // select Image URI
    private val onClick: (Uri) -> Unit // click Item -> callback
) : ListAdapter<Uri, GalleryAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Uri>() {
    override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem
    override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean = oldItem == newItem
}) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imgThumbnail)
        private val check = view.findViewById<ImageView>(R.id.imgCheckMark)
        private val container = view.findViewById<FrameLayout>(R.id.container)

        fun bind(uri: Uri) {
            Glide.with(image.context).load(uri).centerCrop().into(image)

            val isSelected = getSelectedUri() == uri
            check.visibility = if (isSelected) View.VISIBLE else View.GONE
            container.setBackgroundResource(
                if (isSelected) R.drawable.bg_selected_gallery_image else 0
            )

            itemView.setOnClickListener {
                onClick(uri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
