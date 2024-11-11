package com.example.userbuyify.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.userbuyify.utils.CartListener
import com.example.userbuyify.adapters.AdapterCartProducts
import com.example.userbuyify.databinding.ActivityUsersMainBinding
import com.example.userbuyify.databinding.BsCartProductsBinding
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class UsersMainActivity : AppCompatActivity(), CartListener {
    private lateinit var  binding: ActivityUsersMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList : List<CartProductTable>
    private lateinit var adapterCartProducts: AdapterCartProducts  //call here first . (Do not work direct call adapter!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener{
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    //if i want to save room live data & see in studio(optional) //product fetch & assign in 'cartProductList' from room data
    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
                cartProductList = it
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener{
            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this) //import BottomSheetDialog
            bs.setContentView(bsCartProductsBinding.root)


            bsCartProductsBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text
            //onNextButtonClicked(2)
            bsCartProductsBinding.btnNext.setOnClickListener{
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }


            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this){
            if (it >0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()

            }
            else{
                binding.llCart.visibility = View.GONE


            }

        }
    }

    override fun showCartLayout(itemCount:Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount
        //0 theke boro hole  visible to add cart
        if (updatedCount> 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()

        }
        else{
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text ="0"
        }

    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this) {

            viewModel.savingCartItemCount(it + itemCount)
        }
    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text ="0"
    }
}