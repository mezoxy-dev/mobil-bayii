package com.mezoxy.mobilbayii

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mezoxy.mobilbayii.databinding.ActivityAdminPageBinding // View Binding sınıfınızı import edin

class AdminPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding'i şişirin (inflate) ve içeriği ayarlayın
        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // Toolbar'ı AppBar olarak ayarlayın
        setSupportActionBar(binding.toolbar)

        // Geri düğmesini etkinleştirin
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Geri simgesini göster
            setDisplayShowTitleEnabled(true) // Toolbar'ın kendi başlığını gizle (TextView kullandığımız için)
        }

        // Geri düğmesine tıklama olayını dinleyin
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Geri navigasyonu tetikle
        }

        // Toolbar başlığını TextView üzerinden ayarlayın (XML'de zaten "AdminPage" olarak ayarlandı)
        // binding.toolbarTitle.text = "AdminPage" // İsteğe bağlı: Kodu kullanarak başlığı ayarlamak isterseniz

        // Buraya admin sayfasının diğer başlatma kodlarını ekleyebilirsiniz


    }
    fun goToUserProperties(view: View){
        val intent = Intent(this@AdminPageActivity, UserPropertiesActivity::class.java)
        startActivity(intent)

    }
    fun goToInventory(view: View){
        val intent = Intent(this@AdminPageActivity, StockActivity::class.java)
        startActivity(intent)
    }
    fun goToOrders(view: View){
        val intent = Intent(this@AdminPageActivity, OrdersActivity::class.java)
        startActivity(intent)

    }
    fun goToRequests(view: View?){
        val intent = Intent(this@AdminPageActivity, RequestsActivity::class.java)
        startActivity(intent)
    }
}