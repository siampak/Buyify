package com.example.userbuyify.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.userbuyify.CartListener
import com.example.userbuyify.R
import com.example.userbuyify.Utils
import com.example.userbuyify.adapters.AdapterCartProducts
import com.example.userbuyify.databinding.ActivityOrderPlaceBinding
import com.example.userbuyify.databinding.AddressLayoutBinding
import com.example.userbuyify.models.Orders
import com.example.userbuyify.models.Users
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderPlaceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderPlaceBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts : AdapterCartProducts
    private var cartListener : CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        backToUserMainActivity()
        onPlaceOrder()
        setStatusBarColor()
    }



//    private fun onPlaceOrder() {
//        binding.btnNext.setOnClickListener{
//            viewModel.getAddressStatus().observe(this){status->
//                Utils.hideDialog()
//
////                if(status){
//////                    //payment work
//////                    Utils.showToast(this@OrderPlaceActivity,"Payment done")
//////                    //order save, Delete products
//////                    saveOrder()
//////                    Utils.hideDialog()
//////                    startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
//////                    finish()
////                }
////                else{
////                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
////
////                    val alertDialog = AlertDialog.Builder(this)
////                        .setView(addressLayoutBinding.root)
////                        .create()
////                    alertDialog.show()
////
////                    addressLayoutBinding.btnAdd.setOnClickListener{
////                        saveAddress(alertDialog, addressLayoutBinding)
////                    }
////            }
//
//            }
//        }
//    }



    //GPT//

    private fun onPlaceOrder() {
        binding.btnNext.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                Utils.hideDialog()
                if (status) {
                    // If address is already saved, proceed with saving the order
                    saveOrder()


                } else {
                    // If address is not saved, prompt user to enter address
                    showAddressDialog()
                }
            }
        }
    }

    private fun showAddressDialog() {
        val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

        val alertDialog = AlertDialog.Builder(this)
            .setView(addressLayoutBinding.root)
            .create()
        alertDialog.show()

        addressLayoutBinding.btnAdd.setOnClickListener {
            saveAddress(alertDialog, addressLayoutBinding)
        }
    }


//    save order to firebase
    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductsList->
            if(cartProductsList.isNotEmpty()) {
                viewModel.getUserAddress { address ->
                    if (address != null) {
                        val order = Orders(
                            orderId = Utils.getRandomId(),
                            orderList = cartProductsList,
                            userAddress = address,
                            orderStatus = 0,
                            orderDate = Utils.getCurrentDate(),
                            orderingUserId = Utils.getCurrentUserId()
                        )
                        viewModel.saveOrderProducts(order)

                        // Update stock and delete cart products in a coroutine
                        lifecycleScope.launch {
                            for (products in cartProductsList) {
                                val count = products.productCount
                                val stock = products.productStock?.minus(count!!)
                                if (stock != null) {
                                    viewModel.saveProductsAfterOrder(stock, products)
                                }
                            }
                            // Delete cart products and reset item count
                            viewModel.deleteCartProducts()
                            viewModel.savingCartItemCount(0)
                            cartListener?.hideCartLayout()
                        }

                        Utils.showToast(this, "Order placed successfully.")
                        startActivity(Intent(this, UsersMainActivity::class.java))
                        finish()
                    } else {
                        Utils.showToast(this, "Failed to get user address.")
                    }
                }
            }
        }
}

//                        Utils.showToast(this, "Order placed successfully.")
//                        startActivity(Intent(this, UsersMainActivity::class.java))
//                        finish()
//                    } else {
//                        Utils.showToast(this, "Failed to get user address.")
//                    }
//                    // If order is already saved, proceed with deleting the product cart & saving item count(0)
//                    viewModel.deleteCartProducts()
//                    viewModel.savingCartItemCount(0)
//                    cartListener?.hideCartLayout()
//
//                }
//                for (products in cartProductsList) {
//                    val count = products.productCount
//                    val stock = products.productStock?.minus(count!!)
//                    if (stock != null) {
//                        viewModel.saveProductsAfterOrder(stock, products)
//                    }
//                }
//            }
//
//
//        }
//    }

    //Save address of users
    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {

        Utils.showDialog(this, "Processing...")

        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()

        }
        Utils.showToast( this, "Address saved.")
        alertDialog.dismiss()
        Utils.hideDialog()
        // Now that the address is saved, proceed with saving the order
        saveOrder()
    }

    //back button for activity to activity
    private fun backToUserMainActivity() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    //data retrieve from the room database
    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList->

            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0

            for(products in cartProductList){
                val price = products.productPrice?.substring(1)?.toInt() // ৳14 first index minus
                val itemCount = products.productCount!!
                totalPrice += (price?.times(itemCount)!!)
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if(totalPrice < 200){
                binding.tvDeliveryCharge.text = "৳30"
                totalPrice += 30
            }

            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }


    //StatusBar color change code for this activity(Activity)
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors= ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}