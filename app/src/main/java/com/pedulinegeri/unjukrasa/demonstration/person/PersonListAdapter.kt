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

    private var personList = arrayListOf<Person>()

    inner class ViewHolder(private val binding: PersonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.chipRole.text = "Initiator"
            binding.tvName.text = person.name
            binding.textView7.text = person.uid

            binding.root.setOnClickListener {
                with(mainNavController) {
                    if (currentDestination == graph[R.id.participationListBottomSheetDialog]) {
                        navigate(
                            ParticipationListBottomSheetDialogDirections.actionParticipationListBottomSheetDialogToNavigationProfilePage(
                                person.uid
                            )
                        )
                    } else {
                        navigate(
                            DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationProfilePage(
                                person.uid
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

    fun addPerson(person: Person) {
        personList.add(person)
        notifyItemInserted(itemCount - 1)
    }

    fun initPersonList(personList: ArrayList<Person>) {
        this.personList = personList
        notifyDataSetChanged()
    }
}