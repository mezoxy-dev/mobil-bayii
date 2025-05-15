package com.mezoxy.mobilbayii.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mezoxy.mobilbayii.R
import com.mezoxy.mobilbayii.databinding.ActivityStockBinding

class StockActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStockBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}