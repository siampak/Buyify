package com.example.userbuyify.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.R
import com.example.userbuyify.utils.Utils
import com.example.userbuyify.activity.AuthMainActivity
import com.example.userbuyify.databinding.AddressBookLayoutBinding
import com.example.userbuyify.databinding.FragmentProfileBinding
import com.example.userbuyify.viewmodels.UserViewModel


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private  val viewModel: UserViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProfileBinding.inflate(layoutInflater)
        onOrdersLayoutClicked()
        onBackButtonClicked()
        onAddressBookClicked()
        onLogoutClicked()
        return binding.root
    }

    private fun onLogoutClicked() {
        binding.llLogout.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()
                builder.setTitle("Log out")
                .setMessage("Do you  want to log out ?")
                .setPositiveButton("Yes"){_,_->
                    viewModel.logOutUser()
                    startActivity(Intent(requireContext(), AuthMainActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No"){_,_->
                    alertDialog.dismiss()
                }
                    //out of the dialog not clickable( not_working)
                    .show()
                    .setCancelable(false)

        }

    }

    private fun onAddressBookClicked() {
        binding.llAddress.setOnClickListener{
            val addressBookLayoutBinding = AddressBookLayoutBinding.inflate(LayoutInflater.from(requireContext()))

            viewModel.getUserAddress {address->
                addressBookLayoutBinding.etAdress.setText(address.toString())
            }
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(addressBookLayoutBinding.root)
                .create()
            alertDialog.show()

            addressBookLayoutBinding.btnEdit.setOnClickListener{
                addressBookLayoutBinding.etAdress.isEnabled =true
            }
            addressBookLayoutBinding.btnSave.setOnClickListener{
                viewModel.saveAddress(addressBookLayoutBinding.etAdress.text.toString())
            alertDialog.dismiss()
                Utils.showToast(requireContext(), "Address updated..")
            }

        }
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