package com.pedulinegeri.unjukrasa.new_demonstration

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.NewImageOrVideoItemBinding


class NewDemonstrationImageAdapter :
    RecyclerView.Adapter<NewDemonstrationImageAdapter.ViewHolder>() {
    
    val imagesUri = arrayListOf<Uri>()

    inner class ViewHolder(private val binding: NewImageOrVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            binding.ivDemonstration.setImageURI(uri)

            binding.civCancel.visibility = View.VISIBLE
            binding.civCancel.setOnClickListener {
                imagesUri.removeAt(absoluteAdapterPosition)
                notifyItemRemoved(absoluteAdapterPosition)
                binding.ivDemonstration.setImageResource(0)

                binding.ivDemonstration.visibility = View.GONE
                binding.civCancel.visibility = View.GONE
            }
        }
    }

    fun addImage(uri: Uri) {
        imagesUri.add(uri)
        notifyItemInserted(itemCount - 1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = NewImageOrVideoItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUri[position])
    }

    override fun getItemCount(): Int = imagesUri.size
}