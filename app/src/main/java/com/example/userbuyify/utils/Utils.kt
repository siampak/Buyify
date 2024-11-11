package com.example.userbuyify.utils


import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.userbuyify.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object Utils {


    //Alert Dialog for ("Sending Otp...")
    private var dialog: AlertDialog?= null

    fun showDialog(context: Context, message: String){
        val process = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        process.tvMessage.text =message
        dialog =AlertDialog.Builder(context).setView(process.root).setCancelable(false).create()
        dialog!!.show()
    }
    fun hideDialog(){
        dialog?.dismiss()
    }



    //show Toast for valid user number("please enter valid phone number")
    fun showToast(context: Context, message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }



    //Firebase auth for call
    private var firebaseAuthInstance: FirebaseAuth?=null

    fun getAuthInstance(): FirebaseAuth{
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }


    //current user format fun + and pass the id(L)
    fun getCurrentUserId() : String{
        return FirebaseAuth.getInstance().currentUser!!.uid

    }

    //random id generator
    fun getRandomId(): String{
        return (1..25).map { (('A'..'z') + ('a'..'z') +('0'..'9')).random() }.joinToString  ("")
    }

    //date generate
    fun getCurrentDate(): String? {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }


}