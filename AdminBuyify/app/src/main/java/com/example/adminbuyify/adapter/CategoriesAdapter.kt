package com.example.adminbuyify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.adminbuyify.databinding.ItemViewProductCatagoriesBinding
import com.example.adminbuyify.model.Categories

class CategoriesAdapter(

    private val categoryArrayList: ArrayList<Categories>,
    val onCategoryClicked: (Categories) -> Unit,
    ): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>(){
    //viewHolder connect to item binding xml
    class CategoriesViewHolder (val binding: ItemViewProductCatagoriesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(ItemViewProductCatagoriesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    //return the size number
    override fun getItemCount(): Int {
       return categoryArrayList.size
    }

    //pass image and tittle //image and title ta thik jayga moto bosy dimu
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryArrayList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.icon)
            tvCategoryTitle.text = category.category
        }
        holder.itemView.setOnClickListener{
            onCategoryClicked(category)
        }
    }
}
