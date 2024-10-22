package com.example.userbuyify.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.CartListener
import com.example.userbuyify.R
import com.example.userbuyify.Utils
import com.example.userbuyify.adapters.AdapterProduct
import com.example.userbuyify.databinding.FragmentCategoryBinding
import com.example.userbuyify.databinding.ItemViewProductBinding
import com.example.userbuyify.models.Product
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.ClassCastException

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel : UserViewModel by viewModels()
    private  var category :String? =null
    private lateinit var adapterProduct:AdapterProduct
    private var cartListener : CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        onNavigationIconClick()
        getProductCategory()
        setToolBarTitle()
        fetchCategoryProduct()
        onSearchMenuClick()
        setStatusBarColor()
        return binding.root
    }

    private fun onNavigationIconClick() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }


    //toolbar menu te on click set kora hoyse
    private fun onSearchMenuClick() {
        binding.tbSearchFragment.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.searchMenu ->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> {false}
            }
        }
    }

    private fun fetchCategoryProduct() {
        binding.shimmerViewContainer.visibility = View.VISIBLE //set visibility of shimmer

        lifecycleScope.launch{
            viewModel.getCategoryProduct(category!!).collect {
                if(it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE

                }
                adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                binding.shimmerViewContainer.visibility = View.GONE //uorer 3 line set korar por eta chole jabe,set visibility of shimmer(product loading structure xml)

           }
        }

    }

    //toolbar new tittle set
    private fun setToolBarTitle() {
        binding.tbSearchFragment.title =category
    }

    private fun getProductCategory() {
        val bundle = arguments
         category=bundle?.getString("category")
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
        if (product.productStock!! + 1 > itemCountInc) {
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
    private fun saveProductInRoomDb(product: Product) {

        val cartProduct = CartProductTable(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "\u09F3" + "${product.productPrice}", //Bangla Taka sign code = (/u09F3)
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productimageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )

        lifecycleScope.launch { viewModel.insertCartProduct(cartProduct) }

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