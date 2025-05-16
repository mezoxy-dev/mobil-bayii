package com.mezoxy.mobilbayii

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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