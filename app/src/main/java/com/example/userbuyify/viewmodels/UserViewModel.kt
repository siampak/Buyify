package com.example.userbuyify.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.userbuyify.utils.Utils
import com.example.userbuyify.api.ApiUtilities
import com.example.userbuyify.models.Bestseller
import com.example.userbuyify.models.Message
import com.example.userbuyify.models.Notification
import com.example.userbuyify.models.Orders
import com.example.userbuyify.models.Product
import com.example.userbuyify.models.PushNotify
import com.example.userbuyify.roomdb.CartProductDao
import com.example.userbuyify.roomdb.CartProductTable
import com.example.userbuyify.roomdb.CartProductsDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserViewModel(application: Application): AndroidViewModel(application){

    //initialize
    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My_pref",MODE_PRIVATE)
    val cartProductDao : CartProductDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    //Room DB
    suspend fun insertCartProduct(products: CartProductTable){
        cartProductDao.insertCartProduct(products)
    }


    //if i want to see room live data in studio(optional)
    fun getAll(): LiveData<List<CartProductTable>> {
        return  cartProductDao.getAllCartProducts()
    }

    //delete cart products
    suspend fun deleteCartProducts(){
        cartProductDao.deleteCartProducts()
    }


    suspend fun updateCartProduct(products: CartProductTable){
        cartProductDao.updateCartProduct(products)
    }

    suspend fun deleteCartProduct(productId : String){
        cartProductDao.deleteCartProduct(productId)
    }

    //'AllProducts' Fetch(bring) from Firebase --> "product maybe"(Admin save in the Firebase)
    fun fetchAllTheProducts(): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        //Automatic create two function from object
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    //now add list (products) in recyclerView 'Product'(model)
                    products.add(prod!!)
                }
                //& pass/sent
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.addValueEventListener(eventListener)

        awaitClose { db.removeEventListener(eventListener) }
    }


    //'orderDetails' Fetch(bring) from Firebase --> for "OrderFragment"
    fun getAllOrders() : Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        //Automatic create two function from object
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for(orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if(order?.orderingUserId == Utils.getCurrentUserId()) {
                        //now add list (orderList) in recyclerView 'Order'(model)
                        orderList.add(order!!)
                    }
                }
                //& pass/sent
                trySend(orderList)

            }
            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    //'category all product' Fetch(bring) from Firebase -->for "CategoryFragment"(Admin save in the Firebase)
    fun getCategoryProduct(category: String) : Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductsCategory/${category}")

        //Automatic create two function from object
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    //now add list (products) in recyclerView 'Product'(model)
                    products.add(prod!!)

                }
                //& pass/sent
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    //'OrderDetail' Fetch(bring) from Firebase (from orderList -cart product)
    fun getOrderedProducts(orderId: String) :Flow<List<CartProductTable>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)

            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener) }
    }


    // save & show count in add button --> Save in the Firebase
    fun updateItemCount(product : Product, itemCount: Int){

        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsType/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)
    }

    //order save in firebase database with details --> Save in the Firebase
    fun saveProductsAfterOrder(stock : Int, product: CartProductTable){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsCategory/${product.productCategory}/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsType/${product.productType}/${product.productId}").child("itemCount").setValue(0)


        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsCategory/${product.productCategory}/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductsType/${product.productType}/${product.productId}").child("productStock").setValue(stock)
    }

    //Save Address to Firebase --> Save in the Firebase
    fun saveUserAddress(address : String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress").setValue(address)//save to firebase
    }
    //getUserAddress from firebase--> Fetch Address.
    fun getUserAddress(callback : (String?) -> Unit){
       val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress")//save to firebase

    //Automatic create two function from object
        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                else{
                    callback(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun saveAddress(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress").setValue(address)//save to firebase

    }

    fun logOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    //saveOrder to Firebase --> Save in the Firebase
    fun saveOrderProducts(orders: Orders){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId!!).setValue(orders)
    }

    fun fetchProductTypes() : Flow<List<Bestseller>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins/ProductsType")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productTypeList = ArrayList<Bestseller>()
                for (productType in snapshot.children){
                    val productTypeNane = productType.key

                    val productList = ArrayList<Product>()

                    for (products in productType.children){
                        val products = products.getValue(Product::class.java)
                        productList.add(products!!)
                    }
                    val bestseller = Bestseller(productType = productTypeNane, products = productList)
                    productTypeList.add(bestseller)
                }
                trySend(productTypeList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener) }
    }

    //sharedPreference
    fun savingCartItemCount(itemCount : Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }
    fun fetchTotalCartItemCount() : MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }

    //Now save address in sharedPreference
    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    //get Address from sharedPreference
    fun getAddressStatus() : MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status
    }

    fun sendNotification(adminUid: String, title: String, message: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
//              Retrieve the admin's FCM token from Firebase
                val adminTokenTask = FirebaseDatabase.getInstance()
                    .getReference("Admins")
                    .child("AdminInfo")
                    .child(adminUid)
                    .child("adminToken")
                    .get()
                // Await completion of user token retrieval
                val tokenId = adminTokenTask.await().getValue(String::class.java)
                Log.e("GGG", "AccessToken is: $adminUid")
                if (!tokenId.isNullOrEmpty()) {
                    // Create the notification data
                    val notification = PushNotify(
                        message = Message(
                            token = tokenId,
                            notification = Notification(
                                title = title,
                                body = message
                            )
                        )
                    )

                    // Call the API to send notification
                    val response = withContext(Dispatchers.IO) {
                        ApiUtilities.getApiInterface().sendNotification(notification)
                            .execute()
                    }
                    if (response.isSuccessful) {
                        Log.d("Notification", "Notification sent successfully")
                    } else {
                        Log.e("Notification", "Notification response unsuccessful")
                    }
                } else {
                    Log.e("Notification", "User token is null or empty")
                }
            } catch (e: Exception) {
                Log.e("Notification", "Error sending notification: ${e.message}")
            }
        }
    }


    /*fun sendNotification(adminUid: String, title: String, message : String) {
        // Retrieve the admin's FCM token from Firebase
        FirebaseDatabase.getInstance()
            .getReference("Admins")
            .child("AdminInfo")
            .child(adminUid)
            .child("adminToken")
            .get()
            .addOnCompleteListener { task ->
                val tokenId = task.result.getValue(String::class.java)
                Log.e(ContentValues.TAG, "AccessToken is: $tokenId")

                if (!tokenId.isNullOrEmpty()) {
                    // Create the notification data
                    val notification = PushNotify(Messages(token = tokenId), Notifications(title, message))



                    // Call the API to send notification
                    ApiUtilities.getApiInterface().sendNotification(notification)
                        .enqueue(object : Callback<PushNotify> {
                            override fun onResponse(
                                call: Call<PushNotify>,
                                response: Response<PushNotify>
                            ) {
                                Log.d("Notification", "Notification sent successfully")

                            }

                            override fun onFailure(call: Call<PushNotify>, t: Throwable) {
                                Log.e("Notification", "Failed to send notification: ${t.message}")
                            }
                        })
                } else {
                    Log.e("NotificationError", "Admin FCM token is null or empty.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("NotificationError", "Failed to retrieve token: $error")
            }
    }*/
}