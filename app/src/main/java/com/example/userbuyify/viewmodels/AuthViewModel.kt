package com.example.userbuyify.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.userbuyify.utils.Utils
import com.example.userbuyify.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit  //timeunit

class AuthViewModel: ViewModel() {

    //mutable stateFlow
    private val _verificationId = MutableStateFlow<String?>(null)

    //From firebase phone authentication code(callBack part)
    private val _otpSent= MutableStateFlow<Boolean>( false)
    val otpSent = _otpSent //bahire theke edit kote parbona(viewModel er bahire access korte parbena)

    //this time for Credential mutableStateFlow(credential part)
    private  val _isSignedInSuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully = _isSignedInSuccessfully

    //1st Time Log in check er jonno (1st time login hoy 2nd bar log in kora lagbena)
    private val _isACurrentUser = MutableStateFlow(false)
    val isACurrentUser = _isACurrentUser
    init {
        Utils.getAuthInstance().currentUser?.let {
            _isACurrentUser.value = true
        }
    }

    //From firebase phone authentication code(callBack part)
    fun sendOTP(userNumber : String, activity: Activity){   //here userNumber ar Activity call for phone auth part
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,)
            {
                //two mutable stateflow  create
                _verificationId.value =verificationId  //use for Credential part
                _otpSent.value = true
            }
        }


        //From firebase phone authentication code(Phone auth part)
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // credential part --> 1.pass verificationId(check) 2.all user entry Information
     fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: Users) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            user.userToken = it.result

            Utils.getAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    user.uid = Utils.getCurrentUserId()
                    //for set all users and users Information
                    if (task.isSuccessful) {
                        // save to Firebase
                        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
                            .child(user.uid!!).setValue(user)

                        _isSignedInSuccessfully.value = true



                    }

                }
        }
     }

}
