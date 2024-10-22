package com.example.adminbuyify

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

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