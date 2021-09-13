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
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipationListBottomSheetDialogDirections
import com.squareup.picasso.Picasso


class PersonListAdapter(
    private val mainNavController: NavController,
    private val uid: String
) :
    RecyclerView.Adapter<PersonListAdapter.ViewHolder>() {

    private var personList = arrayListOf<Person>()

    inner class ViewHolder(private val binding: PersonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.chipRole.text = "Inisiator"
            binding.tvName.text = person.name
            binding.textView7.text = person.uid

            val imageRef =
                Firebase.storage.reference.child("profile_picture/$uid.png")

            imageRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.ivPerson)
            }.addOnFailureListener {
                Picasso.get().load(R.drawable.no_img).into(binding.ivPerson)
            }

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