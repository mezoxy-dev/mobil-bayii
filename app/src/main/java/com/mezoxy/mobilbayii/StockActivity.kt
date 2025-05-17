package com.mezoxy.mobilbayii

import adapters.StockAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.mezoxy.mobilbayii.databinding.ActivityStockBinding
import dataClasses.StockPhone

class StockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockBinding
    private lateinit var stockAdapter: StockAdapter
    private var allStockPhones = mutableListOf<StockPhone>() // Tüm stok verisini tutacak liste
    private lateinit var dbHelper: DatabaseHelper // DatabaseHelper sınıfınız

    /*TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    TODO: STOK EKLE VE ÇIKAR İŞLEMLERİNİ NASIL YAPACAĞINI DÜŞÜN
    */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this) // DatabaseHelper'ı başlat

        // Toolbar kurulumu
        setSupportActionBar(binding.toolbarStock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Geri tuşunu göster
        supportActionBar?.title = "Stok Durumu"

//        setupRecyclerView()
        //loadStockData()
    }

//    private fun setupRecyclerView() {
//        stockAdapter = StockAdapter(emptyList()) // Başlangıçta boş liste ile başlat
//        binding.recyclerViewStock.apply {
//            layoutManager = LinearLayoutManager(this@StockActivity)
//            adapter = stockAdapter
//        }
//    }

 //   private fun loadStockData() {
        // TODO: Bu kısımda SQLite veritabanınızdan telefon stoklarını çekmelisiniz.
        // Örnek: val phonesFromDb = dbHelper.getAllPhonesWithStock()
        // Bu metodu DatabaseHelper sınıfınızda oluşturmanız gerekecek.
        // Bu metot, telefon adı, stok miktarı ve resim URL'si gibi bilgileri içeren
        // StockPhone nesnelerinden oluşan bir liste dönmelidir.
        // Örnek bir DatabaseHelper metodu:
        // fun getAllPhonesWithStock(): List<StockPhone> {
        //     val stockList = mutableListOf<StockPhone>()
        //     val db = this.readableDatabase
        //     // Telefonlar tablonuzdan ve stok tablonuzdan (eğer ayrıysa) join yaparak veri çekin
        //     // Veya telefonlar tablonuzda stok miktarı sütunu varsa doğrudan oradan çekin
        //     val cursor = db.rawQuery("SELECT id, name, quantity, imageUrl FROM YourPhoneTableWithStock", null) // Sorgunuzu uyarlayın
        //     if (cursor.moveToFirst()) {
        //         do {
        //             val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        //             val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        //             val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
        //             val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
        //             stockList.add(StockPhone(id, name, quantity, imageUrl))
        //         } while (cursor.moveToNext())
        //     }
        //     cursor.close()
        //     db.close()
        //     return stockList
        // }

        // Şimdilik örnek statik veri kullanalım:
//        val sampleStockList = mutableListOf(
//            StockPhone(
//                1,
//                "Samsung Galaxy S23",
//                50,
//                "https://cdn.akakce.com/samsung/samsung-galaxy-s23-128-gb-8-gb-z.jpg"
//            ),
//            StockPhone(
//                2,
//                "iPhone 15 Pro",
//                30,
//                "https://cdn.akakce.com/apple/apple-iphone-15-pro-128-gb-z.jpg"
//            ),
//            StockPhone(
//                3,
//                "Xiaomi Redmi Note 12",
//                75,
//                "https://cdn.akakce.com/xiaomi/xiaomi-redmi-note-12-pro-plus-5g-256-gb-8-gb-z.jpg"
//            ),
//            StockPhone(
//                4,
//                "Google Pixel 8",
//                20,
//                "https://cdn.akakce.com/google/google-pixel-8-128-gb-z.jpg"
//            ),
//            StockPhone(5, "OnePlus 11", 40, null) // Resimsiz örnek
//      )
//        allStockPhones.clear()
//        allStockPhones.addAll(sampleStockList) // Orijinal listeyi sakla
//
//        if (allStockPhones.isEmpty()) {
//            binding.recyclerViewStock.visibility = View.GONE
//            binding.textViewNoStock.visibility = View.VISIBLE
//        } else {
//            binding.recyclerViewStock.visibility = View.VISIBLE
//            binding.textViewNoStock.visibility = View.GONE
//            stockAdapter.updateData(allStockPhones)
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stock_toolbar_menu, menu) // Arama menüsünü şişir
        val searchItem = menu?.findItem(R.id.action_search_stock)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.queryHint = "Telefon ara..."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Kullanıcı Enter'a bastığında (genellikle burada bir şey yapmayız,
                // arama anlık olarak onQueryTextChange ile yapılır)
//                stockAdapter.filter(query) // İsteğe bağlı, Enter'a basınca da filtrele
//                searchView.clearFocus() // Klavyeyi gizle
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                // Kullanıcı her harf girdiğinde veya sildiğinde
//                stockAdapter.filter(newText)
                return true
            }
        })

//        // Arama çubuğu kapatıldığında tüm listeyi tekrar göstermek için
//        searchItem?.setOnActionExpandListener(object : OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
//                // Arama çubuğu açıldığında yapılacaklar (isteğe bağlı)
//                return true // Devam etmesine izin ver
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
//                // Arama çubuğu kapandığında yapılacaklar
////                stockAdapter.filter(null) // Filtreyi temizle, tüm listeyi göster
////                return true // Devam etmesine izin ver
//            }
//        })

        return super.onCreateOptionsMenu(menu)
    }

    // Toolbar'daki geri tuşuna basıldığında aktiviteyi sonlandır
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}