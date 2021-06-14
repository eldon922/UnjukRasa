package com.pedulinegeri.unjukrasa.demonstration.module

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.ProgressListItemBinding


class ProgressListAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val binding: ProgressListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = ProgressListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

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