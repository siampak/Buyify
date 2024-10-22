package com.example.userbuyify.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao

interface CartProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //onConflict =error solution for app to room database live data
    fun insertCartProduct(products: CartProductTable)

    @Update
    fun updateCartProduct(products: CartProductTable)

    @Query("SELECT * FROM CartProductTable")  // it should be here  If  we want to see the data live(room data)
    fun  getAllCartProducts() : LiveData<List<CartProductTable>>

    @Query("DELETE FROM CartProductTable WHERE productId =:productId") // : colon is  important here otherwise  product is not delete
    suspend fun deleteCartProduct(productId : String?)

    @Query("DELETE FROM CartProductTable")
    suspend fun deleteCartProducts()
}