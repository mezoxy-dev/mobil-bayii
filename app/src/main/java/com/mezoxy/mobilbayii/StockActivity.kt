    package com.mezoxy.mobilbayii

    import adapters.StockAdapter
    import adapters.UrunAdapter
    import android.content.ContentValues
    import android.os.Bundle
    import android.view.Menu
    import android.view.MenuItem
    import android.view.MenuItem.OnActionExpandListener
    import android.view.View
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.SearchView
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.mezoxy.mobilbayii.databinding.ActivityStockBinding
    import dataClasses.StockPhone

    class StockActivity : AppCompatActivity() {

        private lateinit var binding: ActivityStockBinding
        private lateinit var stockAdapter: StockAdapter
        private lateinit var dbHelper: DatabaseHelper
        private var allStockPhones = mutableListOf<StockPhone>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityStockBinding.inflate(layoutInflater)
            setContentView(binding.root)

            dbHelper = DatabaseHelper(this)

            // Veritabanından verileri çek
            allStockPhones = dbHelper.getAllFromStocks().toMutableList()



            stockAdapter = StockAdapter(allStockPhones, dbHelper) { phone, newQuantity ->
                val success = dbHelper.updateStockQuantity(phone.product_id, newQuantity)
                if (success) {
                    Toast.makeText(
                        this,
                        "${phone.name} stoğu güncellendi: $newQuantity",
                        Toast.LENGTH_SHORT
                    ).show()
                    allStockPhones = dbHelper.getAllFromStocks().toMutableList()
                    stockAdapter.updateData(allStockPhones)
                } else {
                    Toast.makeText(this, "Stok güncelleme başarısız oldu!", Toast.LENGTH_SHORT).show()
                }
            }



            binding.recyclerViewStock.apply {
                layoutManager = LinearLayoutManager(this@StockActivity)
                adapter = stockAdapter
            }

            initSearchBar()
            initToolbar()

        }



        private fun initSearchBar(){
            // SearchView dinleyicisi
            binding.searchViewStock.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    stockAdapter.filter.filter(newText)
                    return true
                }
            })
        }

        private fun initToolbar(){
            // Toolbar

            setSupportActionBar(binding.toolbarStock)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Stok Durumu"
        }









    }