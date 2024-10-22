package com.example.adminbuyify.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminbuyify.AdminMainActivity
import com.example.adminbuyify.Constants
import com.example.adminbuyify.R
import com.example.adminbuyify.Utils
import com.example.adminbuyify.adapter.AdapterSelectedImage
import com.example.adminbuyify.databinding.FragmentAddProductBinding
import com.example.adminbuyify.model.Product
import com.example.adminbuyify.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class AddProductFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val imageUris: ArrayList<Uri> = arrayListOf()
    //for select photo from gallery
    val selectedImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){listOfUri->
        val fiveImages = listOfUri.take(5)  // only 5 picture select from Gallery
        imageUris.clear()
        imageUris.addAll(fiveImages)

        binding.rvProductImages.adapter = AdapterSelectedImage(imageUris) // image list passing by adapter

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        setStatusBarColor()
        setAutoCompleteTextViews()
        onImageSelectClicked()
        onAddButtonClicked()
        return binding.root
    }

    private fun onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading images....")

            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etProductQuantity.text.toString()
            val productUnit = binding.etProductUnit.text.toString()
            val productPrice= binding.etProductPrice.text.toString()
            val productStock = binding.etProductStock.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()

            if(productTitle.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty() ||
                productPrice.isEmpty() || productCategory.isEmpty() || productStock.isEmpty() || productType.isEmpty()) {
                Utils.apply {
                    hideDialog()  //first show dialog("Uploading message hide")
                    showToast(requireContext(), "Empty fields are not allowed")
                }
            }
            else if (imageUris.isEmpty()){
                Utils.apply {
                    hideDialog()  //first show dialog("Uploading message hide")
                    showToast(requireContext(), "Please upload some images")
                }

            }

            else{
                val product = Product(
                    productTitle = productTitle,
                    productQuantity = productQuantity.toInt(),
                    productUnit = productUnit,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productCategory = productCategory,
                    productType = productType,
                    itemCount = 0,
                    adminUid = Utils.getCurrentUserId(),
                    productRandomId = Utils.getRandomId()
                    )

                saveImage(product)

            }

        }
    }

    //just for image saved in database
    private fun saveImage(product: Product) {

        viewModel.saveImageInDB(imageUris)
        lifecycleScope.launch {
            viewModel.isImagesUploaded.collect{
                if (it){
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext() , "image saved")
                    }
                    getUrls(product)
                }
            }
        }

    }

    private fun getUrls(product: Product) {
        Utils.showDialog(requireContext(),"Publishing product....")

        lifecycleScope.launch {
            viewModel.downloadedUrls.collect{
                val urls =it
                product.productimageUris = urls
                saveProduct(product)
            }
        }

    }

    // save product & publishing product in admin_start_activity
    private fun saveProduct(product: Product) {
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect {
                if (it) {

                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(),"Your product is live")
                }
            }


        }

    }

    //for select photo from gallery
    private fun onImageSelectClicked() {
        binding.btnSelectImage.setOnClickListener{
            selectedImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {

        //array adapter create(Constants object er sathe adapter attach)
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val category = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType= ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)

        //auto_Complete_text_view er shthe set kore deoya hoytese product list (Constant) gulo ke
        binding.apply {
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
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M ){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}
