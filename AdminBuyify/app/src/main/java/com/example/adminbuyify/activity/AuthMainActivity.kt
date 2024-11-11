package com.example.adminbuyify.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adminbuyify.R

class AuthMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Dexter.withContext(applicationContext)
//                .withPermission(Manifest.permission.POST_NOTIFICATIONS)
//                .withListener(object : PermissionListener {
//                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {}
//
//                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        p0: PermissionRequest?,
//                        p1: PermissionToken?
//                    ) {
//
//                        p1?.continuePermissionRequest()
//                    }
//                }).check()
//        }
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
    }
}