package com.mezoxy.mobilbayii

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mezoxy.mobilbayii.databinding.ActivityUserPropertiesBinding

private lateinit var binding: ActivityUserPropertiesBinding
class UserPropertiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPropertiesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

          // setSupportActionBar(binding.userPropertiesToolbar)

//        //geri düğmesini etkinleştirin
//        supportActionBar?.apply {
//            setDisplayHomeAsUpEnabled(true) // Geri simgesini göster
//            setDisplayShowTitleEnabled(false) // Toolbar'ın kendi başlığını gizle (TextView kullandığımız için)
//        }
//        // Geri düğmesine tıklama olayını dinleyin
//        binding.userPropertiesToolbar.setNavigationOnClickListener {
//            onBackPressedDispatcher.onBackPressed() // Geri navigasyonu tetikle
//        }

    }



}