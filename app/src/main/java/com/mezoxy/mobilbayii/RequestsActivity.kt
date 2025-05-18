package com.mezoxy.mobilbayii

import adapters.RequestAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezoxy.mobilbayii.databinding.ActivityRequestsBinding
import dataClasses.Request

class RequestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestsBinding
    private lateinit var requestList: MutableList<Request>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val db = DatabaseHelper(this)
        requestList = db.getAllRequests() // Bu fonksiyonun içinde her Request'in urunList'i de olmalı
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_requests)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RequestAdapter(requestList, this)
    }


    private fun setupActionBar()
    {
        // Toolbar'ı Action Bar olarak ayarla
        setSupportActionBar(binding.toolbarRequests)

        // Geri tuşunu etkinleştir
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Toolbar başlığını gizle
    }

    // Options Menüyü oluştur (Search Bar ve Stok Tuşları için)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_requests, menu)

        // Search Bar'ı ayarla
        val searchItem = menu?.findItem(R.id.action_search_reqeusts)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.recyclerViewRequests.adapter?.let { adapter ->
                    if (adapter is RequestAdapter) {
                        adapter.filterByRequestId(query)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.recyclerViewRequests.adapter?.let { adapter ->
                    if (adapter is RequestAdapter) {
                        adapter.filterByRequestId(newText)
                    }
                }
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
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}