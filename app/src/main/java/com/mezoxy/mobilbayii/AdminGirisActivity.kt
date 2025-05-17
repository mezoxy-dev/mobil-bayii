package com.mezoxy.mobilbayii

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.view.GravityCompat
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
        registerActionBar(R.id.toolbarAdminGirisSayfasi)


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

    private fun registerActionBar(myActionBarId: Int)
    {
        setSupportActionBar(findViewById(myActionBarId))
        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false)
        }
        setupNavigationView()
    }

    private fun setupNavigationView() {
        binding.hamburgerButton.setOnClickListener {binding.drawerLayout.openDrawer(GravityCompat.START)}
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_sepet_page -> {
                    val intent = Intent(this, SepetActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_admin_page -> {
                    val intent = Intent(this, AdminGirisActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
            false
        }
    }

    fun goToAdminPage(view: View) {
        val intent = Intent(this@AdminGirisActivity, AdminPageActivity::class.java)
        startActivity(intent)
        finish()
    }
}


