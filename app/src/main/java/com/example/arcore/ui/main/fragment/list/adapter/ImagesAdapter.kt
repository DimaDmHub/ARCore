package com.example.arcore.ui.main.fragment.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.arcore.R
import com.example.arcore.data.local.entity.ImageEntity
import kotlinx.android.synthetic.main.image_item.view.*

class ImagesAdapter constructor(
    var images: List<ImageEntity> = ArrayList(),
    var listener: OnItemClickListener? = null
) : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        )
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(images[position])
    }

    fun updateItems(images: List<ImageEntity>) {
        this.images = images
        notifyDataSetChanged()
    }

    inner class ImagesViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        init {

            itemView.mbImageItemSave?.setOnClickListener {
                val item = images[adapterPosition]
                val newName = itemView.tielImageItem?.text.toString()
                if (newName != item.name) {
                    listener?.onSaveClicked(item.copy(name = newName))
                }
            }
            itemView.mbImageItemDelete?.setOnClickListener {
                listener?.onDeleteClicked(images[adapterPosition])
            }
        }

        fun bind(item: ImageEntity) {
            Glide.with(itemView.ivImageItem)
                .load(item.image)
                .centerCrop()
                .into(itemView.ivImageItem)
            itemView.tielImageItem.setText(item.name)
            itemView.tielImageItem.setSelection(item.name?.length ?: 0)
        }
    }

    interface OnItemClickListener {

        fun onSaveClicked(image: ImageEntity)

        fun onDeleteClicked(image: ImageEntity)
    }
}