package com.mezoxy.mobilbayii

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mezoxy.mobilbayii.databinding.ActivityAdminGirisBinding

class AdminGirisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminGirisBinding
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminGirisBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbHelper = DatabaseHelper(this)


        // Toolbar'ı AppBar olarak ayarlayın
        setSupportActionBar(binding.toolbarAdminGirisSayfasi)
        supportActionBar?.title = "Admin Girişi"

        // Geri düğmesini etkinleştirin
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Geri simgesini göster
            setDisplayShowTitleEnabled(true) // Toolbar'ın kendi başlığını gizle (TextView kullandığımız için)
        }
        binding.adminGirisButton.setOnClickListener {
            val kullaniciAdi = binding.editTextKullaniciAdi.text.toString().trim()
            val parola = binding.editTextParola.text.toString().trim()

            if (kullaniciAdi.isEmpty() || parola.isEmpty()) {
                Toast.makeText(this, "Kullanıcı adı ve şifre boş bırakılamaz!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Veritabanından admini kontrol et
            if (dbHelper.foundAdminByUsername(kullaniciAdi, parola))
            {
                val intent = Intent(this@AdminGirisActivity, AdminPageActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                Toast.makeText(this, "Hatalı kullanıcı adı veya şifre!", Toast.LENGTH_SHORT).show()
        }
    }

    fun goToAdminPage(view: View) {
        val intent = Intent(this@AdminGirisActivity, AdminPageActivity::class.java)
        startActivity(intent)
        finish()
    }
}


