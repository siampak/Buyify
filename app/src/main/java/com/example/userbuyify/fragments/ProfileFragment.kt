package com.example.userbuyify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.R
import com.example.userbuyify.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProfileBinding.inflate(layoutInflater)
        onOrdersLayoutClicked()
        onBackButtonClicked()
        return binding.root
    }

    private fun onOrdersLayoutClicked() {
        binding.llOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }
    }

    //back button for fragment to Fragment
    private fun onBackButtonClicked() {
        binding.tbProfileFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }


}