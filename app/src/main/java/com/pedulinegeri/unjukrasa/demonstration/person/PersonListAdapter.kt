package com.pedulinegeri.unjukrasa.demonstration.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.get
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.PersonListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationPageFragmentDirections
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListBottomSheetDialogDirections
import com.squareup.picasso.Picasso


class PersonListAdapter(
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<PersonListAdapter.ViewHolder>() {

    private var personList = arrayListOf<Person>()

    class ViewHolder(
        private val binding: PersonListItemBinding,
        private val mainNavController: NavController
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            if (person.role.isNotBlank()) {
                binding.chipRole.text = person.role
            } else {
                binding.chipRole.visibility = View.GONE
            }
            binding.tvName.text = person.name

            val imageRef =
                Firebase.storage.reference.child("profile_picture/${person.uid}.png")

            imageRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.ivPerson)
            }.addOnFailureListener {
                Picasso.get().load(R.drawable.profile_avatar_placeholder_large)
                    .into(binding.ivPerson)
            }

            binding.root.setOnClickListener {
                with(mainNavController) {
                    if (currentDestination == graph[R.id.personListBottomSheetDialog]) {
                        navigate(
                            PersonListBottomSheetDialogDirections.actionPersonListBottomSheetDialogToNavigationProfilePage(
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

        return ViewHolder(binding, mainNavController)
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

    fun clearPersonList() {
        this.personList = arrayListOf()
        notifyDataSetChanged()
    }
}