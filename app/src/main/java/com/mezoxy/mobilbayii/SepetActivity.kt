package com.mezoxy.mobilbayii

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mezoxy.mobilbayii.databinding.ActivitySepetBinding
import dataClasses.Urun

private lateinit var binding: ActivitySepetBinding

class SepetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        val sepet = intent.getSerializableExtra("sepet") as ArrayList<Urun>

        binding.textView.setText("Sepet Activity\n-Ürünler:\n ${sepet.toString()}")
    }
}