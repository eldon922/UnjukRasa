package com.pedulinegeri.unjukrasa.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pedulinegeri.unjukrasa.databinding.FragmentLoginPageBinding


class LoginPageFragment : Fragment() {

    private var fragmentBinding: FragmentLoginPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}