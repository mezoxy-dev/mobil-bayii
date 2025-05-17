package com.mezoxy.mobilbayii

import adapters.UrunAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mezoxy.mobilbayii.databinding.ActivityOrderConfirmationBinding

class OrderConfirmationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrderConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater) // View Binding kullanıyorsanız
        setContentView(binding.root) // View Binding kullanıyorsanız
        // setContentView(R.layout.activity_orders) // View Binding kullanmıyorsanız
        // Toolbar'ı Action Bar olarak ayarla
        setSupportActionBar(binding.toolbarOrders) // View Binding kullanıyorsanız
        // setSupportActionBar(findViewById(R.id.toolbar_orders)) // View Binding kullanmıyorsanız

        // Geri tuşunu etkinleştir
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Toolbar başlığını gizle

        // RecyclerView kurulumu
        setupRecyclerView()

        // Sipariş taleplerini yükle (veritabanından veya API'den)
        loadOrderRequests()
    }

    // Options Menüyü oluştur (Search Bar ve Stok Tuşları için)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_order_confirmation, menu) // menu_orders.xml dosyasını oluşturacaksınız

        // Search Bar'ı ayarla
        val searchItem = menu?.findItem(R.id.action_search_orders) // menu_orders.xml'deki ID
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Kullanıcı arama sorgusunu gönderdiğinde yapılacaklar
                // Siparişleri filtrele
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Kullanıcı arama sorgusunu yazarken yapılacaklar
                // Siparişleri filtrele
                return true
            }
        })

        return true
    }

    // Menü öğelerine tıklama olaylarını yönet
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Geri tuşuna basıldığında
                onBackPressedDispatcher.onBackPressed() // Veya finish()w
                true
            }
            R.id.action_add_stock -> { // menu_orders.xml'deki ID
                // Stok Ekle tuşuna basıldığında
                // Stok ekleme ekranına git veya dialog göster
                true
            }
            R.id.action_remove_stock -> { // menu_orders.xml'deki ID
                // Stok Çıkar tuşuna basıldığında
                // Stok çıkarma ekranına git veya dialog göster
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
    //RecyclerView'ı layout manager ve adapter ile ayarla
    //     binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this) // View Binding
//         binding.recyclerViewOrders.adapter = UrunAdapter(urunList = listOf(), sepetListesi = mutableListOf()) // View Binding


    }

    private fun loadOrderRequests() {
        // Veritabanından veya API'den sipariş taleplerini çek
        // Bu işlemi arka plan thread'inde yapmayı unutmayın!
        // Çekilen verileri RecyclerView adapter'ına gönderin
    }

    // Sipariş taleplerini onaylama veya reddetme gibi işlemler için metotlar
    // Bu metotlar OrderRequestAdapter içinden çağrılabilir
    fun approveOrderRequest(orderId: Int) {
        // Veritabanında siparişin durumunu güncelle
        // Bu işlemi arka plan thread'inde yapın uygulamanın çalışması için
    }

    fun rejectOrderRequest(orderId: Int) {
        // Veritabanında siparişin durumunu güncelle veya sil
        // Bu işlemi arka plan thread'inde yapın uygulamanın çalışması için
    }
}