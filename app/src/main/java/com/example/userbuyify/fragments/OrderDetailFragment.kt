package com.example.userbuyify.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.R
import com.example.userbuyify.adapters.AdapterCartProducts
import com.example.userbuyify.adapters.AdapterOrders
import com.example.userbuyify.databinding.FragmentOrderDetailBinding
import com.example.userbuyify.viewmodels.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OrderDetailFragment : Fragment() {

    private lateinit var adapterCartProducts: AdapterCartProducts
    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var orderId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        getValues()
        settingStatus()
        getOrderedProducts()
        setStatusBarColor()
        onBackButtonClicked()
        return binding.root
    }

    private fun getOrderedProducts() {

        lifecycleScope.launch {
            viewModel.getOrderedProducts(orderId).collect { cartList ->
                adapterCartProducts = AdapterCartProducts()
                binding.rbProductsItem.adapter = adapterCartProducts
                adapterCartProducts.differ.submitList(cartList)

            }
        }

    }

    //status based on color changes
    private fun settingStatus() {
        val colorStateList = ContextCompat.getColorStateList(requireContext(), R.color.blue)

        val views = listOf(
            binding.iv1,
            binding.iv2,
            binding.view1,
            binding.view2,
            binding.iv3,
            binding.view3,
            binding.iv4
        )

        for (i in 0..(2 * status)) {
            views[i].backgroundTintList = colorStateList
        }
//        when(status){
//            0 ->{
//                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//
//            }1 ->{
//            binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//
//            }2 ->{
//            binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//
//
//            }3 ->{
//            binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.view3.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//            binding.iv4.backgroundTintList = ContextCompat.getColorStateList(requireContext() ,R.color.blue)
//
//            }
//        }
    }

    //bundle receive from orderFragment
    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId")!!
    }

    //status bar color change
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.white_yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    //back button for fragment to Fragment(NavigationToolbar)
    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }

}