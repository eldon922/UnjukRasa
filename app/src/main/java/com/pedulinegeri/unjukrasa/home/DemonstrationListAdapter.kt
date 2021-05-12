package com.pedulinegeri.unjukrasa.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.MostActiveTodayDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.ProfileDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.RecommendedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationPageActivity

enum class ViewType {
    TRENDING, MOST_ACTIVE, RECOMMENDED, PROFILE
}

class DemonstrationListAdapter(private val dataSet: List<String>, private val viewType: ViewType) :
    RecyclerView.Adapter<DemonstrationListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DemonstrationPageActivity::class.java)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (this.viewType) {
            ViewType.TRENDING -> TrendingDemonstrationListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            ViewType.MOST_ACTIVE -> MostActiveTodayDemonstrationListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            ViewType.RECOMMENDED -> RecommendedDemonstrationListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            ViewType.PROFILE -> ProfileDemonstrationListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}