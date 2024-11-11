package com.example.userbuyify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.userbuyify.databinding.ItemViewBestsellerBinding
import com.example.userbuyify.models.Bestseller

class AdapterBestseller(val onSeeAllButtonClicked : (Bestseller) -> Unit) : RecyclerView.Adapter<AdapterBestseller.BestsellerViewHolder>() {
    class BestsellerViewHolder(val binding : ItemViewBestsellerBinding) : ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Bestseller>() {
        override fun areItemsTheSame(oldItem: Bestseller, newItem: Bestseller): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bestseller, newItem: Bestseller): Boolean {
            return oldItem == newItem
        }

    }


    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder(ItemViewBestsellerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        val productType = differ.currentList[position]
        holder.binding.apply {

            tvProductType.text = productType.productType
            tvTotalProducts.text = productType.products?.size.toString() + " products"

            val listOfIv = listOf(ivProduct1, ivProduct2, ivProduct3)

            val minimumSize = minOf(listOfIv.size, productType.products?.size!!)

            for (i in 0 until minimumSize){
                listOfIv[i].visibility = View.VISIBLE
                Glide.with(holder.itemView).load(productType.products[i].productimageUris?.get(0)).into(listOfIv[i])
            }

            if (productType.products.size > 3){
                tvProductCount.visibility = View.VISIBLE
                tvProductCount.text = "+" +(productType.products?.size!!-3).toString()
            }

        }

        holder.itemView.setOnClickListener{ onSeeAllButtonClicked(productType)}
    }


}

