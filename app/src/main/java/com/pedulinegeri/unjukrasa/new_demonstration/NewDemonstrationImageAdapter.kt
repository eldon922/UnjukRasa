package com.pedulinegeri.unjukrasa.new_demonstration

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.DemonstrationImageItemBinding


class NewDemonstrationImageAdapter(private val imagesUrl: ArrayList<String>) :
    RecyclerView.Adapter<NewDemonstrationImageAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: DemonstrationImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            val bmImg = BitmapFactory.decodeFile(url)
            binding.ivDemonstration.setImageBitmap(bmImg)
        }
    }

    fun addImage(url: String) {
        imagesUrl.add(url)
        notifyItemInserted(itemCount-1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DemonstrationImageItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUrl[position])
    }

    override fun getItemCount(): Int = imagesUrl.size
}