package com.pedulinegeri.unjukrasa.demonstration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.DemonstrationImageItemBinding

class DemonstrationImageAdapter :
    RecyclerView.Adapter<DemonstrationImageAdapter.ViewHolder>() {

    private var imagesUrl = arrayListOf<Int>()

    inner class ViewHolder(private val binding: DemonstrationImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url: Int) {
            binding.ivDemonstration.setImageResource(url)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DemonstrationImageItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUrl[position])
    }

    override fun getItemCount(): Int = imagesUrl.size

    fun initDemonstrationImageList(imagesUrl: ArrayList<Int>) {
        this.imagesUrl = imagesUrl
        notifyDataSetChanged()
    }
}