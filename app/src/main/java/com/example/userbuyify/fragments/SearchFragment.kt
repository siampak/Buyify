package com.example.userbuyify.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.CartListener
import com.example.userbuyify.R
import com.example.userbuyify.Utils
import com.example.userbuyify.adapters.AdapterProduct
import com.example.userbuyify.databinding.FragmentSearchBinding
import com.example.userbuyify.databinding.ItemViewProductBinding
import com.example.userbuyify.models.Product
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    val viewModel: UserViewModel by viewModels()

    private lateinit var adapterProduct: AdapterProduct
    private lateinit var binding : FragmentSearchBinding
    private var cartListener : CartListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        //** Initialize adapterProduct early**//
        adapterProduct = AdapterProduct(
            ::onAddButtonClicked,
            ::onIncrementButtonClicked,
            ::onDecrementButtonClicked
        )
        binding.rvProducts.adapter = adapterProduct

        searchProducts()
        getAllTheProducts()
        backToHomeFragment()

        return binding.root
    }
    //for search products(filtering products)
    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter.filter(query)

            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    //backButton
    private fun backToHomeFragment() {
        binding.backArrowEt.setOnClickListener{
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

    }

    //set all products home fragment by vieModel(admin ViewModel) and pass adapter recyclerview
    private fun getAllTheProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE //set visibility of shimmer
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts().collect { productList->
                //check Empty product show text view or not!
                if(productList.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE

                }
//                adapterProduct = AdapterProduct(
//                    ::onAddButtonClicked,
//                    ::onIncrementButtonClicked,
//                    ::onDecrementButtonClicked
//                )
//                binding.rvProducts.adapter = adapterProduct
//                adapterProduct.differ.submitList(it)
//                adapterProduct.originalList = it as ArrayList<Product> //for search button
//                binding.shimmerViewContainer.visibility = View.GONE //set visibility of shimmer(product structure xml)

                adapterProduct.differ.submitList(productList)
                adapterProduct.originalList = ArrayList(productList) // for search functionality
                binding.shimmerViewContainer.visibility = View.GONE // hide shimmer after loading products
            }
        }

    }

    //clicked Add product btn then visible productCount btn
    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility= View.VISIBLE

        //step 1.
        var itemCount =productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()
        cartListener?.showCartLayout(1)


        //step 2
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product , itemCount) //save & show item count in add button
        }
    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountInc =productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        //stock check, if have products then increments
//        if (product.productStock!! + 1 > itemCountInc)
        if (product.productStock != null && product.productStock!! + 1 > itemCountInc){
            productBinding.tvProductCount.text = itemCountInc.toString()
            cartListener?.showCartLayout(1)

            //step 2
            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(
                    product,
                    itemCountInc
                ) //save & show item count in add button
            }
        }
        else{
            Utils.showToast(requireContext(),"Can't add more item of this")
        }
    }


    fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountDec =productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--


        //step 2
        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product , itemCountDec) //save & show item count in add button
        }

        if(itemCountDec > 0){
            productBinding.tvProductCount.text = itemCountDec.toString()
        }
        else{
            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId!!) }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility= View.GONE
            productBinding.tvProductCount.text = "0"

        }
        cartListener?.showCartLayout(-1)

    }


    //RoomDB
//    private fun saveProductInRoomDb(product: Product) {
//
//        // Log product data for debugging
//        Log.d("SearchFragment", "Saving product: $product")
//
//        val cartProduct = CartProductTable(
//            productId = product.productRandomId!!,
//            productTitle = product.productTitle,
//            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
//            productPrice = "\u09F3" + "${product.productPrice}", //Bangla Taka sign code = (/u09F3)
//            productCount = product.itemCount,
//            productStock = product.productStock,
//            productImage = product.productimageUris?.get(0)!!,
//            productCategory = product.productCategory,
//            adminUid = product.adminUid,
//            productType = product.productType
//        )
//
//        // Log cart product data for debugging
//        Log.d("SearchFragment", "Cart product: $cartProduct")
//
//        lifecycleScope.launch { viewModel.insertCartProduct(cartProduct) }
//
//    }

    private fun saveProductInRoomDb(product: Product) {
        val cartProduct = CartProductTable(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "\u09F3" + "${product.productPrice}", // Bangla Taka sign code = (/u09F3)
            productCount = product.itemCount ?: 0, // handle potential null value
            productStock = product.productStock ?: 0, // handle potential null value
            productImage = product.productimageUris?.get(0) ?: "", // handle potential null value
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch { viewModel.insertCartProduct(cartProduct) }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please Implement Cart Listener")
        }
    }


}