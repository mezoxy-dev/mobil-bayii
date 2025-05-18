package com.mezoxy.mobilbayii

import adapters.StockAdapter
import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

        stockAdapter = StockAdapter(allStockPhones) { phone, newQuantity ->
            val success = dbHelper.updateStockQuantity(phone.product_id, newQuantity)
            if (success) {
                Toast.makeText(this, "${phone.name} stoğu güncellendi: $newQuantity", Toast.LENGTH_SHORT).show()
                // Güncel listeyi çek ve adapter'a bildir
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stock, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.action_add_stock -> {
//                // Stok ekleme işlemi başlat
//                Toast.makeText(this, "Stok Ekle seçildi", Toast.LENGTH_SHORT).show()
//                return true
//            }
            R.id.action_remove_stock -> {
                // Stok çıkarma işlemi başlat
                Toast.makeText(this, "Stok Çıkar seçildi", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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