package com.example.adminbuyify.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminbuyify.R
import com.example.adminbuyify.Utils
import com.example.adminbuyify.adapter.AdapterCartProducts
import com.example.adminbuyify.databinding.FragmentOrderDetailBinding
import com.example.adminbuyify.viewmodels.AdminViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch


class OrderDetailFragment : Fragment() {
    private lateinit var adapterCartProducts: AdapterCartProducts
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentOrderDetailBinding
    private var status = 0
    private var currentStatus = 0
    private var orderId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        onChangeStatusButtonClicked()
        getValues()
        settingStatus(status)
        getOrderedProducts()
        setStatusBarColor()
        onBackButtonClicked()
        return binding.root

    }

    private fun onChangeStatusButtonClicked() {
        binding.btnChangeStatus.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {

                    R.id.menuReceived -> {
                        currentStatus=1
                        if(currentStatus > status){
                            status = 1
                        settingStatus(1)
                            try {
                                viewModel.updateOrderStatus(orderId, 1)
                                    viewModel.sendNotification(
                                        orderId,
                                        "Received",
                                        "Your Order is received"
                                    )

                            }catch (e: Exception) {
                                Log.e("ButtonClick", "Error updating status: ${e.message}")
                            }
                        }
                        else{
                            Utils.showToast(requireContext(),"Order is already received...")
                        }
                        true
                    }

                    R.id.menuDispatched -> {

                        currentStatus=2
                        if(currentStatus > status){
                            status =2
                        settingStatus(2)
                        viewModel.updateOrderStatus(orderId,2)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId,"Dispatched","Your Order is Dispatched")

                            }

                        }
                        else{
                            Utils.showToast(requireContext(),"Order is already dispatched...")
                        }
                        true
                    }

                    R.id.menuDelivered -> {

                        currentStatus=3
                        if(currentStatus > status){
                            status =3
                        settingStatus(3)
                        viewModel.updateOrderStatus(orderId,3)
                            lifecycleScope.launch {
                                viewModel.sendNotification(orderId,"Delivered","Your Order is Delivered")

                            }

                        }
                        true
                    }

                    else -> {
                        false
                    }
                }

            }
        }
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
    private fun settingStatus(status: Int) {
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
        binding.tvUserAddress.text = bundle.getString("userAddress").toString()
    }

    //status bar color change
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.white_yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    //back button for fragment to Fragment(NavigationToolbar)
    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }

}