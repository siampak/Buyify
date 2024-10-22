package com.example.adminbuyify.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminbuyify.FilteringProducts
import com.example.adminbuyify.databinding.ItemViewProductBinding
import com.example.adminbuyify.model.Product

class AdapterProduct(
    //for btn clicked
    val onEditButtonClicked: (Product) -> Unit) :RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {
    class ProductViewHolder (val binding : ItemViewProductBinding) : ViewHolder(binding.root) {


    }

    //recycle view te new data add or remove korar fole render hoy, tai DiffUtil
    val diffutil= object : DiffUtil.ItemCallback<Product>(){
        //DiffUtil (1)
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.productRandomId == newItem.productRandomId

        }
        //DiffUtil (2)
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffutil)

    //1.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //2. differ er maddome current list size bahir hobe
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //3. position onujai product set kora
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
            tvProductPrice.text = "৳" + product.productPrice //l
        }

        holder.itemView.setOnClickListener{
            onEditButtonClicked(product)
        }

    }

    private val filter : FilteringProducts? = null
    var originalList = ArrayList<Product>()
    //for search use filterable interface
    override fun getFilter(): Filter {
        if(filter == null) return FilteringProducts(this,originalList)
        return  filter

    }


}