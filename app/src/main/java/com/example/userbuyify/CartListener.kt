package com.example.userbuyify

interface CartListener {
    fun showCartLayout(itemCount:Int)

    fun savingCartItemCount(itemCount: Int)

    fun hideCartLayout()
}