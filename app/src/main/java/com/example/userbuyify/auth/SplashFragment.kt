package com.example.userbuyify.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.R
import com.example.userbuyify.activity.UsersMainActivity
import com.example.userbuyify.databinding.FragmentSplashBinding
import com.example.userbuyify.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var  binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        setStatusBarColor() //call statusBar color change method


        //splash screen to sgnIn fragment + delayed 3 second
        Handler(Looper.getMainLooper()).postDelayed({

            //check is user login first Time (1st time login hoyar por 2nd bar log in kora lagbena)
            //Direct SplashFragment To userMainActivity(just 1bar login)
            lifecycleScope.launch {
                viewModel.isACurrentUser.collect{

                    //user login from first page
                    findNavController().navigate(R.id.action_splashFragment_to_signInFragment)

//                    if (it){
//                        startActivity(Intent(requireActivity(),UsersMainActivity::class.java))
//                        requireActivity().finish()
//                    }
//                    else{
//                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
//                    }
//

                }
            }

        }, 3000)

        return binding.root
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
