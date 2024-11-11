package com.example.adminbuyify.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.adminbuyify.R
import com.example.adminbuyify.databinding.ActivityAdminMainBinding

class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Click Same Fragment to Same Fragment show
        NavigationUI.setupWithNavController(binding.bottomMenu , Navigation.findNavController(this,
            R.id.fragmentContainerViewAdmin
        ))

    }
}