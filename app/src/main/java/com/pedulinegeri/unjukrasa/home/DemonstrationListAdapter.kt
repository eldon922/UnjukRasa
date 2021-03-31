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
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationActivity

enum class ViewType {
    TRENDING, MOST_ACTIVE, RECOMMENDED, PROFILE
}

class DemonstrationListAdapter(private val dataSet: List<String>, private val viewType: ViewType) :
    RecyclerView.Adapter<DemonstrationListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = when (viewType) {
            ViewType.TRENDING -> TrendingDemonstrationListItemBinding.bind(view)
            ViewType.MOST_ACTIVE -> MostActiveTodayDemonstrationListItemBinding.bind(view)
            ViewType.RECOMMENDED -> RecommendedDemonstrationListItemBinding.bind(view)
            ViewType.PROFILE -> ProfileDemonstrationListItemBinding.bind(view)
        }

        fun bind(text: String) {
            binding.root.setOnClickListener {
                val intent = Intent(view.context, DemonstrationActivity::class.java)
                view.context.startActivity(intent)
                Toast.makeText(view.context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        val layout = when (this.viewType) {
            ViewType.TRENDING -> R.layout.trending_demonstration_list_item
            ViewType.MOST_ACTIVE -> R.layout.most_active_today_demonstration_list_item
            ViewType.RECOMMENDED -> R.layout.recommended_demonstration_list_item
            ViewType.PROFILE -> R.layout.profile_demonstration_list_item
        }

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}