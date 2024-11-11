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
import com.example.userbuyify.utils.CartListener
import com.example.userbuyify.utils.Constants
import com.example.userbuyify.R
import com.example.userbuyify.utils.Utils
import com.example.userbuyify.adapters.AdapterBestseller
import com.example.userbuyify.adapters.AdapterCategory
import com.example.userbuyify.adapters.AdapterProduct
import com.example.userbuyify.databinding.BsSeeAllBinding
import com.example.userbuyify.databinding.FragmentHomeBinding
import com.example.userbuyify.databinding.ItemViewProductBinding
import com.example.userbuyify.models.Bestseller
import com.example.userbuyify.models.Category
import com.example.userbuyify.models.Product
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var  binding: FragmentHomeBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterBestseller: AdapterBestseller
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener : CartListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigatingToSearchFragment()
        onProfileClicked()
        fetchBestseller()
//        get()

        return binding.root
    }

    private fun fetchBestseller() {
        binding.shimmerViewContainer.visibility =View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductTypes().collect{

                adapterBestseller = AdapterBestseller(::onSeeAllButtonClicked)
                binding.rvBestsellers.adapter = adapterBestseller
                adapterBestseller.differ.submitList(it)
                binding.shimmerViewContainer.visibility =View.GONE
            }
        }
    }

    private fun onProfileClicked() {
        binding.ivProfile.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_porfileFragment)}
    }



    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        //0-20 product object create/ add korbe 'category list' e
        for (i in 0 until Constants.allProductCatagoryIcon.size) {
            categoryList.add(
                Category(
                    Constants.allProductsCategory[i],
                    Constants.allProductCatagoryIcon[i]
                )
            )
        }
        //recycler view list adapter er maddome? receive korbe.
        binding.rvCategories.adapter = AdapterCategory(categoryList, :: onCategoryIconClicked)

    }

    //category clicked hole ekhan theke bundler maddome toolbar er category title o pass hbe
    fun onCategoryIconClicked(category: Category){
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment,bundle)

    }

    fun onSeeAllButtonClicked(produType: Bestseller){

        val bsSeeAllBinding = BsSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs = BottomSheetDialog(requireContext())
            bs.setContentView(bsSeeAllBinding.root)

        adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)

        bsSeeAllBinding.rvProducts.adapter =adapterProduct
        adapterProduct.differ.submitList(produType.products)
        bs.show()
    }

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



    //status var color change
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.orange)
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



    //    //if i want to save room live data & see in studio(optional)
//    private fun get(){
//        viewModel.getAll().observe(viewLifecycleOwner){
//            for (i in it){
//                Log.d("vvv" , i.productTitle.toString())
//                Log.d("vvv" , i.productCount.toString())
//            }
//        }
//    }

}