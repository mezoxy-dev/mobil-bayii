package com.mezoxy.mobilbayii.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mezoxy.mobilbayii.R
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