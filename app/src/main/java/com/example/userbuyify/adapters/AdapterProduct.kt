package com.example.userbuyify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.userbuyify.FilteringProducts

import com.example.userbuyify.databinding.ItemViewProductBinding
import com.example.userbuyify.models.Product

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) :RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {
    class ProductViewHolder (val binding : ItemViewProductBinding) : ViewHolder(binding.root) {


    }

    //recycle view te new data add or remove korar fole render hoy, tai DiffUtil
    val diffutil= object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, diffutil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //differ er maddome current list size bahir hobe
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //position onujai product set kora
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {

            val imageList = ArrayList<SlideModel>()

            val productImage = product.productimageUris

            //0 -> image size porjonto loop chalano hbe
            for (i in 0 until productImage?.size!!){
                imageList.add(SlideModel(product.productimageUris!![i].toString())) //here first create slide model object then add images
            }
            //now image list show in the image slider
            ivImageSlider.setImageList(imageList)

            tvProductTitle.text = product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text = quantity
            tvProductPrice.text = "৳" + product.productPrice

            //check itemCount For save & show item count in add button
            if (product.itemCount!! > 0){
                tvProductCount.text = product.itemCount.toString()
                tvAdd.visibility = View.GONE
                llProductCount.visibility= View.VISIBLE
            }

            tvAdd.setOnClickListener{
                onAddButtonClicked(product,this)
            }
            tvIncrementCount.setOnClickListener {
                onIncrementButtonClicked(product,this)
            }
            tvDecrementCount.setOnClickListener {
                onDecrementButtonClicked(product,this)
            }
        }

    }


//    private val filter : FilteringProducts? = null
//    var originalList = ArrayList<Product>()
//    //for search use filterable interface
//    override fun getFilter(): Filter {
//        if(filter == null) return FilteringProducts(this,originalList)
//        return  filter
//
//    }

    private var filter: FilteringProducts? = null
    var originalList = ArrayList<Product>()

    override fun getFilter(): Filter {
        if (filter == null) filter = FilteringProducts(this, originalList)
        return filter as FilteringProducts
    }

}