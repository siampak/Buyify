package com.example.userbuyify.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.userbuyify.databinding.ItemViewProductCatagoryBinding
import com.example.userbuyify.models.Category
import kotlin.reflect.KFunction0

class AdapterCategory(

    val categoryList: ArrayList<Category>,
    val onCategoryIconClicked: (Category)->Unit
): RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>(){
    //viewholder connect to item binding xml
    class CategoryViewHolder (val binding: ItemViewProductCatagoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemViewProductCatagoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)) //this catagoryviewholder now return(niye jasse)  Item binding xml
    }

    override fun getItemCount(): Int {
       return categoryList.size //size number return to the category list
    }

    //pass image and tittle //image and title ta thik jayga moto bosy dimu
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener{
            onCategoryIconClicked(category)
        }

    }

}

