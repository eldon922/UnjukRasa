package com.pedulinegeri.unjukrasa.demonstration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.MostActiveTodayDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.ProfileDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.RecommendedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding

class DemonstrationListAdapter(
    private val dataSet: ArrayList<String>,
    private val viewType: ViewType,
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<DemonstrationListAdapter.ViewHolder>() {

    enum class ViewType {
        TRENDING, MOST_ACTIVE, RECOMMENDED, PROFILE
    }

    inner class ViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.root.setOnClickListener {
                mainNavController.navigate(R.id.action_global_demonstrationPageFragment)
            }
        }
    }

    fun addDemonstration(text: String) {
        dataSet.add(text)
        notifyItemChanged(itemCount - 1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (this.viewType) {
            ViewType.TRENDING -> TrendingDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
            ViewType.MOST_ACTIVE -> MostActiveTodayDemonstrationListItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
            ViewType.RECOMMENDED -> RecommendedDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
            ViewType.PROFILE -> ProfileDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}