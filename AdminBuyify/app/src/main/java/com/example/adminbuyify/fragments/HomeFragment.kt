package com.example.adminbuyify.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminbuyify.activity.AuthMainActivity
import com.example.adminbuyify.utils.Constants
import com.example.adminbuyify.R
import com.example.adminbuyify.utils.Utils
import com.example.adminbuyify.adapter.AdapterProduct
import com.example.adminbuyify.adapter.CategoriesAdapter
import com.example.adminbuyify.databinding.EditProductLayoutBinding
import com.example.adminbuyify.databinding.FragmentHomeBinding
import com.example.adminbuyify.model.Categories
import com.example.adminbuyify.model.Product
import com.example.adminbuyify.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    val viewModel: AdminViewModel by viewModels()

    private lateinit var binding : FragmentHomeBinding
    private  lateinit var adapterProduct: AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        getAllTheProducts("All")//product add
        setCategories() //for categories add
        setStatusBarColor()
        searchProducts()
        onLogOut()
        return binding.root
    }

    private fun onLogOut() {
        binding.tbHomeFragment.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuLogout->{
                    logOutUser()
                    true
                }

                else -> {false}
            }
        }
    }

    private fun logOutUser(){
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()
            builder.setTitle("Log out")
                .setMessage("Do you  want to log out ?")
                .setPositiveButton("Yes"){_,_->
                    viewModel.logOutUser()
                    startActivity(Intent(requireContext(), AuthMainActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No"){_,_->
                    alertDialog.dismiss()
                }
                //out of the dialog not clickable( not_working)
                .show()
                .setCancelable(false)

    }

    //for search products
    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter.filter(query)

            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    //set all products home fragment by vieModel(admin ViewModel) and pass adapter recyclerview
    private fun getAllTheProducts(category: String) {
        binding.shimmerViewContainer.visibility = View.VISIBLE //set visibility of shimmer
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect {
                //check Empty product show text, view or not!
                //set visibility of Empty text view
                if(it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE

                }
                adapterProduct = AdapterProduct(::onEditButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)

                adapterProduct.originalList = it as ArrayList<Product> //for search button
                //set visibility of shimmer
                binding.shimmerViewContainer.visibility = View.GONE //set visibility of shimmer(product structure xml)

            }
        }

    }

    private fun setCategories() {

        val categoryList = ArrayList<Categories>()
        //0-20 product object create/ add korbe 'category list' e
        for (i in 0 until Constants.allProductsCategoryIcon.size) {
            categoryList.add(Categories(
                Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]
                )
            )
        }

        //recycler view list adapter er maddome? receive/set  korbe.
        binding.rvCategories.adapter = CategoriesAdapter(categoryList, :: onCategoryClicked)
    }

    private fun onCategoryClicked(categories: Categories){
        getAllTheProducts(categories.category)

    }

    //for edit button clicked
    private fun  onEditButtonClicked(product : Product){
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductTitle.setText(product.productTitle)
            etProductQuantity.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductPrice.setText(product.productPrice.toString())
            etProductStock.setText(product.productStock.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }

        //then show dialog with data (for edit btn)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()
        //then show dialog with data(for edit btn(2)
        editProduct.btnEdit.setOnClickListener {
            editProduct.etProductTitle.isEnabled = true
            editProduct.etProductQuantity.isEnabled = true
            editProduct.etProductUnit.isEnabled = true
            editProduct.etProductPrice.isEnabled = true
            editProduct.etProductStock.isEnabled = true
            editProduct.etProductCategory.isEnabled = true
            editProduct.etProductType.isEnabled = true
        }
        setAutoCompleteTextViews(editProduct)

        //then show dialog with data edit (for save btn)
        editProduct.btnSave.setOnClickListener {
            lifecycleScope.launch {
                product.productTitle = editProduct.etProductTitle.text.toString()
                product.productQuantity= editProduct.etProductQuantity.text.toString().toInt()
                product.productUnit = editProduct.etProductUnit.text.toString()
                product.productPrice= editProduct.etProductPrice.text.toString().toInt()
                product.productStock = editProduct.etProductStock.text.toString().toInt()
                product.productCategory = editProduct.etProductCategory.text.toString()
                product.productType = editProduct.etProductType.text.toString()
                viewModel.savingUpdateProducts(product)  //for save btn(from admin viewModel)
            }
            Utils.showToast(requireContext(),"Saved changes!")
            alertDialog.dismiss()
        }
    }
    private fun setAutoCompleteTextViews(editProduct:EditProductLayoutBinding) {

        //array adapter create(Constants object er sathe adapter attach)
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType= ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)

        //auto_Complete_text_view er shthe set kore deoya hoytese product list (Constant) gulo ke
        editProduct.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }


    //StatusBar color change code
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}