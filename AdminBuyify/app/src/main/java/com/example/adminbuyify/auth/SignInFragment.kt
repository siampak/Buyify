package com.example.adminbuyify.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.adminbuyify.R
import com.example.adminbuyify.Utils
import com.example.adminbuyify.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater) //viewBinding

        setStatusBarColor()//StatusBar color change code
        getUserNumber()//number length check with color change Btn.
        onContinueButtonClick()
        return binding.root
    }

    //check the user input number r8 or wrong and then number pass by bundle
    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener {
            val number = binding.etUserNumber.text.toString()  //input number = number

            if (number.isEmpty() || number.length != 10) {
                Utils.showToast(
                    requireContext(),
                    "please enter valid phone number"
                ) // Toast call from object
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }
        }
    }

    //number length check with color change Btn.
    private fun getUserNumber() {
        binding.etUserNumber.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length

                if(len == 10){
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.green
                    ))
                }
                else{
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.grayish_blue
                    ))

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    //StatusBar color change code
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}