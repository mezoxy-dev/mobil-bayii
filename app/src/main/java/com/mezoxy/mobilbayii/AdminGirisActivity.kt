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
            val admin = dbHelper.getAdminByUsername(kullaniciAdi)

            if (admin != null) {
                // Kullanıcı bulundu, şimdi şifreyi kontrol et
                // DİKKAT: Gerçek uygulamada şifreler hash'lenerek karşılaştırılmalıdır!
                // Örnek: if (BCrypt.checkpw(parola, admin.sifre)) { ... }
                if (admin.sifre == parola) {
                    // Giriş başarılı
                    Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(
                        this,
                        AdminPageActivity::class.java
                    ) // AdminPageActivity sınıf adınız
                    // İsteğe bağlı: Admin bilgilerini veya bir bayrağı AdminPageActivity'ye gönderebilirsiniz
                    // intent.putExtra("ADMIN_USERNAME", admin.kullaniciAdi)
                    startActivity(intent)
                    finish() // Giriş yapıldıktan sonra bu aktiviteyi kapat
                } else {
                    // Şifre yanlış
                    Toast.makeText(this, "Şifre yanlış. Lütfen tekrar deneyin.", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                // Kullanıcı bulunamadı
                Toast.makeText(
                    this,
                    "Kullanıcı adı bulunamadı. Lütfen doğru girin.",
                    Toast.LENGTH_LONG
                ).show()


            }
        }
    }

    fun goToAdminPage(view: View) {
        val intent = Intent(this@AdminGirisActivity, AdminPageActivity::class.java)
        startActivity(intent)
    }
}


