package com.pedulinegeri.unjukrasa.demonstration.discussion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.DiscussionListItemBinding


class DiscussionListAdapter(
    private val dataSet: List<String>,
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<DiscussionListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: DiscussionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.chipReply.setOnClickListener {
                mainNavController.navigate(R.id.action_demonstrationPageFragment_to_discussionReplyBottomSheetDialog)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DiscussionListItemBinding.inflate(
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