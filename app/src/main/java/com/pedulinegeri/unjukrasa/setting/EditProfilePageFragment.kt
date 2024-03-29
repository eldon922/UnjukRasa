package com.pedulinegeri.unjukrasa.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentEditProfilePageBinding
import com.squareup.picasso.Picasso


class EditProfilePageFragment : Fragment() {

    private var _binding: FragmentEditProfilePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    private val user = Firebase.auth.currentUser!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        binding.btnSubmit.setOnClickListener {
            when {
                binding.etName.text.isBlank() -> {
                    binding.etName.error = getString(R.string.name_input_empty)
                    binding.etName.requestFocus()
                    binding.etEmail.error = null
                }
                else -> {
                    binding.btnSubmit.isEnabled = false

                    if (binding.etEmail.text.isNotBlank()) {
                        if (isValidEmail(binding.etEmail.text)) {
                            user.updateEmail(binding.etEmail.text.toString()).addOnFailureListener {
                                toast.setText(getString(R.string.email_input_exist))
                                toast.show()
                                binding.etEmail.setText(user.email)
                            }
                        } else {
                            toast.setText(getString(R.string.email_not_valid))
                            toast.show()
                            binding.btnSubmit.isEnabled = true
                            return@setOnClickListener
                        }
                    }

                    val db = Firebase.firestore
                    db.collection("users").document(user.uid)
                        .update("name", binding.etName.text.toString()).addOnSuccessListener {
                            binding.tvSuccess.isVisible = true
                        }.addOnFailureListener {
                            toast.setText(getString(R.string.unknown_error_message, it))
                            toast.show()
                        }.addOnSuccessListener {
                            binding.btnSubmit.isEnabled = true
                        }
                }
            }
        }

        val imageRef =
            Firebase.storage.reference.child("profile_picture/${user.uid}.png")

        imageRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.ivPerson)
        }

        binding.etName.setText(authViewModel.name)
        binding.etEmail.setText(user.email)

        binding.btnPhotoProfile.setOnClickListener {
            ImagePicker.with(this).compress(1024)
                .cropSquare()
                .maxResultSize(170, 170)
                .start()
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            val uri: Uri = data?.data!!

            val imageRef =
                Firebase.storage.reference.child("profile_picture/${user.uid}.png")
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.ivPerson)
                }
            }.addOnFailureListener {
                toast.setText(getString(R.string.upload_photo_profile_failed, it))
                toast.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}