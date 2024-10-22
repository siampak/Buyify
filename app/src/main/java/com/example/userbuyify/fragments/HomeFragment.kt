package com.example.userbuyify.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.userbuyify.Constants
import com.example.userbuyify.R
import com.example.userbuyify.adapters.AdapterCategory
import com.example.userbuyify.databinding.FragmentHomeBinding
import com.example.userbuyify.models.Category
import com.example.userbuyify.viewmodels.UserViewModel


class HomeFragment : Fragment() {

    private lateinit var  binding: FragmentHomeBinding
    private val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigatingToSearchFragment()
        onProfileClicked()
//        get()

        return binding.root
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