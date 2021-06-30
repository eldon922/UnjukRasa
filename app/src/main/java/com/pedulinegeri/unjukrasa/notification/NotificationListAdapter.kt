package com.pedulinegeri.unjukrasa.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.NotificationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter


class NotificationListAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: NotificationListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.vpImages.adapter = DemonstrationImageAdapter(listOf(R.drawable.indonesian_flag))
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = NotificationListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
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