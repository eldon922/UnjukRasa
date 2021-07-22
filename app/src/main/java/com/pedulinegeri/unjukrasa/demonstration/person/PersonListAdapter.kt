package com.pedulinegeri.unjukrasa.demonstration.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.get
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.PersonListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationPageFragmentDirections
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipationListBottomSheetDialogDirections


class PersonListAdapter(
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<PersonListAdapter.ViewHolder>() {

    private var personList = arrayListOf<String>()

    inner class ViewHolder(private val binding: PersonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            if (text.isNotEmpty()) {
                binding.chipRole.text = text
            } else {
                binding.chipRole.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                with(mainNavController) {
                    if (currentDestination == graph[R.id.participationListBottomSheetDialog]) {
                        // TODO DEV
                        navigate(
                            ParticipationListBottomSheetDialogDirections.actionParticipationListBottomSheetDialogToNavigationProfilePage(
                                1
                            )
                        )
                    } else {
                        // TODO DEV
                        navigate(
                            DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationProfilePage(
                                1
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PersonListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(personList[position])
    }

    override fun getItemCount() = personList.size

    fun addPerson(name: String) {
        personList.add(name)
        notifyItemInserted(itemCount - 1)
    }

    fun initPersonList(personList: ArrayList<String>) {
        this.personList = personList
        notifyDataSetChanged()
    }
}