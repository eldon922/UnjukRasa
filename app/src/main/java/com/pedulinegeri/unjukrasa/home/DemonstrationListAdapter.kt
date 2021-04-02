package com.pedulinegeri.unjukrasa.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = when (viewType) {
            ViewType.TRENDING -> TrendingDemonstrationListItemBinding.bind(view)
            ViewType.MOST_ACTIVE -> MostActiveTodayDemonstrationListItemBinding.bind(view)
            ViewType.RECOMMENDED -> RecommendedDemonstrationListItemBinding.bind(view)
            ViewType.PROFILE -> ProfileDemonstrationListItemBinding.bind(view)
        }

        fun bind(text: String) {
            binding.root.setOnClickListener {
                val intent = Intent(view.context, DemonstrationPageActivity::class.java)
                view.context.startActivity(intent)
                Toast.makeText(view.context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (this.viewType) {
            ViewType.TRENDING -> R.layout.trending_demonstration_list_item
            ViewType.MOST_ACTIVE -> R.layout.most_active_today_demonstration_list_item
            ViewType.RECOMMENDED -> R.layout.recommended_demonstration_list_item
            ViewType.PROFILE -> R.layout.profile_demonstration_list_item
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}