package com.mezoxy.mobilbayii

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mezoxy.mobilbayii.databinding.ActivityOrdersBinding

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}