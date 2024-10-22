package com.example.adminbuyify.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminbuyify.R
import com.example.adminbuyify.adapter.AdapterOrders
import com.example.adminbuyify.databinding.FragmentOrderBinding
import com.example.adminbuyify.model.OrderedItems
import com.example.adminbuyify.viewmodels.AdminViewModel
import kotlinx.coroutines.launch


class OrderFragment : Fragment() {
    private  lateinit var adapterOrders: AdapterOrders
    private val viewModel : AdminViewModel by viewModels()
    private lateinit var binding: FragmentOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)


        getAllOrders()
        setStatusBarColor()
        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect {orderList->

                if(orderList.isNotEmpty()){

                    val orderedList = ArrayList<OrderedItems>()

                    for (orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0

                        for(products in orders.orderList!!){
                            val price = products.productPrice?.substring(1)?.toInt() // ৳14 first index minus
                            val itemCount = products.productCount!!
                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory},")
                        }

                        val orderedItems = OrderedItems(orders.orderId, orders.orderDate, orders.orderStatus, title.toString(), totalPrice, orders.userAddress)
                        orderedList.add(orderedItems)

                    }
                    adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }

            }
        }

    }

    //clicked and goto orderDetail with pass bundle from this fragment
    private fun onOrderItemViewClicked(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)
        bundle.putString("userAddress", orderedItems.userAddress)


        findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle)

    }

    //status bar color
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.white_yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}