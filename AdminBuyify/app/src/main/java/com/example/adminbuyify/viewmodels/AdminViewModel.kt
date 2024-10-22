package com.example.adminbuyify.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminbuyify.Utils
import com.example.adminbuyify.api.ApiUtilities
import com.example.adminbuyify.model.CartProductTable
import com.example.adminbuyify.model.Message
import com.example.adminbuyify.model.Notification
import com.example.adminbuyify.model.Orders
import com.example.adminbuyify.model.Product
import com.example.adminbuyify.model.PushNotify
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class AdminViewModel : ViewModel() {

    //1. state_flow for notify
    private val _isImagesUploaded = MutableStateFlow(false)
    var isImagesUploaded: StateFlow<Boolean> = _isImagesUploaded

    //2. state_flow for notify
    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadedUrls: StateFlow<ArrayList<String?>> = _downloadedUrls

    //3. state_flow for notify
    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved

    //first save the storage , then download url(image) and add array list
    fun saveImageInDB(imageUri: ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId())
                .child("images")
                .child(
                    UUID.randomUUID().toString()
                ) //*** image saved carefully -> here 'child "images"' means al images saved in database together

            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUrls.add(url.toString())

                if (downloadUrls.size == imageUri.size) {
                    _isImagesUploaded.value = true
                    _downloadedUrls.value = downloadUrls
                }
            }
        }


    }


    //save product in database (set on format)
    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)

            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins")
                    .child("ProductsCategory/${product.productCategory}/${product.productRandomId}")
                    .setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child("ProductsType/${product.productType}/${product.productRandomId}")
                            .setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }

                    }
            }
    }


    //normally add all products from Database(when admin add any product)
    //here, ekhane alada vabe category o clicked er parameter o pass kora hoyse.
    fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        //object theke duita function
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    //when "All" then add all products in recycler and pass/sent
                    //here, check 'product category' er sathe 'category' match korle product add korbe
                    if (category == "All" || prod?.productCategory == category) {
                        products.add(prod!!)
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }


    //then show dialog with data edit (for save btn)
    fun savingUpdateProducts(product: Product) {

        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductsCategory/${product.productCategory}/${product.productRandomId}")
            .setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductsType/${product.productType}/${product.productRandomId}")
            .setValue(product)

    }


    //'orderDetails' Fetch(bring) from Firebase --> for "OrderFragment"
    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .orderByChild("orderStatus")

        //Automatic create two function from object
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)

                    //now add list (orderList) in recyclerView 'Order'(model)
                    orderList.add(order!!)

                }
                //& pass/sent
                trySend(orderList)

            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    //'OrderDetail' Fetch(bring) from Firebase (from orderList -cart product)
    fun getOrderedProducts(orderId: String): Flow<List<CartProductTable>> = callbackFlow {
        val db =
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)

            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    //Save update status from admin when admin changed the status
    fun updateOrderStatus(orderId: String, status: Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId).child("orderStatus").setValue(status)
    }

    fun sendNotification(orderId: String, title: String, message: String) {
        // Start a coroutine on the IO dispatcher for background work
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Retrieve the admin's FCM token from Firebase
                val userIdTask = FirebaseDatabase.getInstance().getReference("Admins")
                    .child("Orders")
                    .child(orderId)
                    .child("orderingUserId")
                    .get()

                // Await completion and proceed if successful
                val userUId = userIdTask.await().getValue(String::class.java)
                if (userUId != null) {
                    val userTokenTask = FirebaseDatabase.getInstance().getReference("AllUsers")
                        .child("Users")
                        .child(userUId)
                        .child("userToken")
                        .get()

                    // Await completion of user token retrieval
                    val userToken = userTokenTask.await().getValue(String::class.java)
                    if (!userToken.isNullOrEmpty()) {
                        // Build notification data
                        val notification = PushNotify(
                            message = Message(
                                token = userToken,
                                notification = Notification(
                                    title = title,
                                    body = message
                                )
                            )
                        )

                        // Send the notification via API
                        val response = withContext(Dispatchers.IO) {
                            ApiUtilities.getApiInterface().sendNotification(notification).execute()
                        }

                        if (response.isSuccessful) {
                            Log.d("Notification", "Notification sent successfully")
                        } else {
                            Log.e("Notification", "Notification response unsuccessful")
                        }
                    } else {
                        Log.e("Notification", "User token is null or empty")
                    }
                } else {
                    Log.e("Notification", "User ID is null")
                }
            } catch (e: Exception) {
                Log.e("Notification", "Error sending notification: ${e.message}")
            }
        }
    }



    /*
        fun sendNotification(orderId: String, title: String, message: String) {
            // Retrieve the admin's FCM token from Firebase
            FirebaseDatabase.getInstance().getReference("Admins")
                .child("Orders")
                .child(orderId)
                .child("orderingUserId")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userUId = task.result.getValue(String::class.java)
                        if (userUId != null) {
                            FirebaseDatabase.getInstance().getReference("AllUsers")
                                .child("Users")
                                .child(userUId)
                                .child("userToken")
                                .get()
                                .addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val userToken = tokenTask.result.getValue(String::class.java)
                                        if (!userToken.isNullOrEmpty()) {
                                            // Create the notification data
                                            val notification = PushNotify(
                                                Message(userToken),
                                                Notification(title, message)
                                            )

                                            // Call the API to send notification
                                            ApiUtilities.getApiInterface().sendNotification(notification)
                                                .enqueue(object : Callback<PushNotify> {
                                                    override fun onResponse(
                                                        call: Call<PushNotify>,
                                                        response: Response<PushNotify>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            Log.d("Notification", "Notification sent successfully")
                                                        } else {
                                                            Log.e("Notification", "Notification response unsuccessful")
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<PushNotify>, t: Throwable) {
                                                        Log.e("Notification", "Failed to send notification: ${t.message}")
                                                    }
                                                })
                                        } else {
                                            Log.e("Notification", "User token is null or empty")
                                        }
                                    } else {
                                        Log.e("Notification", "Failed to retrieve user token")
                                    }
                                }
                        } else {
                            Log.e("Notification", "User ID is null")
                        }
                    } else {
                        Log.e("Notification", "Failed to retrieve orderingUserId")
                    }
                }
        }
    */


    /*
    fun sendNotification(orderId: String, title: String, message: String) {
    // Retrieve the admin's FCM token from Firebase
    val getToken = FirebaseDatabase.getInstance()
    .getReference("Admins")
    .child("Orders")
    .child(orderId)
    .child("orderingUserId")
    .get()
    Log.d("Notification", getToken.result.getValue(String::class.java).toString())
    getToken.addOnCompleteListener { task ->
    val userUId = task.result.getValue(String::class.java)
    Log.d("GGG", userUId.toString())
    val userToken = FirebaseDatabase.getInstance()
    .getReference("AllUsers")
    .child("Users")
    .child(userUId!!)
    .child("userToken")
    .get()
    userToken.addOnCompleteListener {

    // Create the notification data
    val notification = PushNotify(Message(userToken.toString()), Notification(title,message))


    // Call the API to send notification
    if (notification != null) {
    ApiUtilities.getApiInterface().sendNotification(notification)
    .enqueue(object : Callback<PushNotify> {
    override fun onResponse(
    call: Call<PushNotify>,
    response: Response<PushNotify>
    ) {
    if (response.isSuccessful) {
    Log.d("Notification", "Notification sent successfully")
    Log.d("Notification", it.result.getValue(String::class.java).toString())
    }
    }

    override fun onFailure(call: Call<PushNotify>, t: Throwable) {
    Log.e("Notification", "Failed to send notification: ${t.message}")
    }
    })
    }
    }

    }

    }
    */

}