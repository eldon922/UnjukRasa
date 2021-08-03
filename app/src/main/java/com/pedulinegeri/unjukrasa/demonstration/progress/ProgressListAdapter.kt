package com.pedulinegeri.unjukrasa.demonstration.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ProgressListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter


class ProgressListAdapter(private val fragmentManager: FragmentManager, private val navController: NavController) :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    private var progressList = arrayListOf<String>()

    inner class ViewHolder(private val binding: ProgressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.reContent.setEditorFontSize(16)
            binding.reContent.setEditorFontColor(binding.textView7.currentTextColor)
            binding.reContent.setEditorBackgroundColor(binding.root.solidColor)
            binding.reContent.setInputEnabled(false)
            binding.reContent.html =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris"

            val adapter = DemonstrationImageAdapter(fragmentManager, navController)
            binding.vpImages.adapter = adapter
            adapter.initDemonstrationImageList(
                arrayListOf(
                    "https://www.youtube.com/watch?v=G7H9uo3j5FQ".takeLast(11),
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png"
                )
            )
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