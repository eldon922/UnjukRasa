package com.pedulinegeri.unjukrasa.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.NotificationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter


class NotificationListAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.vpImages.adapter = DemonstrationImageAdapter(listOf(R.drawable.indonesian_flag))
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationListItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}