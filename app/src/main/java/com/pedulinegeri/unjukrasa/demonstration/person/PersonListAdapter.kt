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
    private val dataSet: ArrayList<String>,
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<PersonListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
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
                        navigate(ParticipationListBottomSheetDialogDirections.actionParticipationListBottomSheetDialogToNavigationProfilePage(1))
                    } else {
                        // TODO DEV
                        navigate(DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationProfilePage(1))
                    }
                }
            }
        }
    }

    fun addPerson(name: String) {
        dataSet.add(name)
        notifyItemInserted(itemCount - 1)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding =
            PersonListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

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