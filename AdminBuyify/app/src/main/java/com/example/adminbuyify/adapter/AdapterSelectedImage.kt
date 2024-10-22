package com.example.adminbuyify.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.adminbuyify.databinding.ItemViewImageSelectionBinding

class AdapterSelectedImage (val imageUris : ArrayList<Uri>): RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {
    //adapter with attach Item_view_selection in SelectedImageViewHolder
    class SelectedImageViewHolder (val binding: ItemViewImageSelectionBinding) : ViewHolder(binding.root)

    //return SelectedImageViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
       return SelectedImageViewHolder(ItemViewImageSelectionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    //position onujayi image pass/ set kore dibe.
    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val image = imageUris[position]

        holder.binding.apply {
            ivImage.setImageURI(image)
        }

        //remove image -> cross button working on it
        holder.binding.closeButton.setOnClickListener{
            if(position< imageUris.size){
                imageUris.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }


}