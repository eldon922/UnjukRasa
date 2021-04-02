package com.pedulinegeri.unjukrasa.demonstration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.DemonstrationImageItemBinding

class DemonstrationImagesAdapter(private val imagesUrl: List<Int>) :
    RecyclerView.Adapter<DemonstrationImagesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = DemonstrationImageItemBinding.bind(view)

        fun bind(url: Int) {
            binding.ivDemonstration.setImageResource(url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.demonstration_image_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUrl[position])
    }

    override fun getItemCount(): Int = imagesUrl.size
}