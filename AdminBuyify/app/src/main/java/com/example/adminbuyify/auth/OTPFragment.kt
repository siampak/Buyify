package com.example.adminbuyify.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminbuyify.activity.AdminMainActivity
import com.example.adminbuyify.R
import com.example.adminbuyify.model.Admins
import com.example.adminbuyify.utils.Utils
import com.example.adminbuyify.databinding.FragmentOTPBinding
import com.example.adminbuyify.viewmodels.AuthViewmodel
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {


    private val viewModel: AuthViewmodel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)

        getUserNumber()
        customizingEnteringOTP()
        onBackButtonClick()
        sendOTP()
        onLoginButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(),"Signing you...")   //just for show Dialog
            val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)
            val otp = editTexts.joinToString (""){ it.text.toString() }

            if (otp.length < editTexts.size){
                Utils.showToast(requireContext(), "Please enter right otp")
            }
            else{
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }
    //for verifyOtp (credential part)
    private fun verifyOtp(otp: String) {
        //data send korar somoy(user entry Information)(L)
        val admins= Admins(adminUid = null, adminPhoneNumber = userNumber)
        viewModel.signInWithPhoneAuthCredential(otp,userNumber, admins)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(),"Logged In...")

                    //Ekhan theke 'UserMainActivity' r kaj shuru (homeFragment show)
                    startActivity(Intent(requireContext() , AdminMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }





    //For sent OTP (callback part)
    private fun sendOTP() {

        Utils.showDialog(requireContext(), "Sending Otp...") //just for show Dialog
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {otpSent ->
                    if (otpSent){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(),"OTP sent to the number..")
                    }
                }
            }
        }
    }




    //Back to signIn fragment
    private fun onBackButtonClick() {
        binding.tbOtpfragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }



    //customizing Entering Otp(user input er length 1 dile next box ee automatic chole jabe
    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {              //length check, jodi length = 1 hoy tahole  check dibo i er value choto  then next gore(1gor+hobe) chole jabe
                        if (i<editTexts.size - 1){
                            editTexts[i+1].requestFocus()
                        }

                    }else if (s?.length ==0){           //jodi length = 0 hoy tahole check dibe i jodi boro hoy ager gore chole ashbe(1 gor minus korbe)
                        if (i> 0) {
                            editTexts[i-1].requestFocus()
                        }
                    }


                }

            })
        }
    }


    //bundle receiving (number passing) from user(sign in fragment) and show in this binding
    private fun getUserNumber() {
        val bundle = arguments

        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }


}