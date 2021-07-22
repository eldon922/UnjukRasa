package com.pedulinegeri.unjukrasa.demonstration.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ProgressListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter


class ProgressListAdapter :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    private var progressList = arrayListOf<String>()

    class ViewHolder(private val binding: ProgressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.reContent.setEditorFontSize(16)
            binding.reContent.setEditorFontColor(binding.textView7.currentTextColor)
            binding.reContent.setEditorBackgroundColor(binding.root.solidColor)
            binding.reContent.setInputEnabled(false)
            binding.reContent.html =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris"

            val adapter = DemonstrationImageAdapter()
            binding.vpImages.adapter = adapter
            adapter.initDemonstrationImageList(arrayListOf(R.drawable.indonesian_flag))
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProgressListItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(progressList[position])
    }

    override fun getItemCount() = progressList.size

    fun initProgressList(progressList: ArrayList<String>) {
        this.progressList = progressList
        notifyDataSetChanged()
    }
}