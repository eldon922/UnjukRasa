package com.pedulinegeri.unjukrasa.demonstration.discussion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.DiscussionReplyListItemBinding


class DiscussionReplyListAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<DiscussionReplyListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: DiscussionReplyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DiscussionReplyListItemBinding.inflate(
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