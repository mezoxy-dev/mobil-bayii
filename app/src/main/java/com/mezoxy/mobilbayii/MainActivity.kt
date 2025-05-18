package com.mezoxy.mobilbayii

import adapters.UrunAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mezoxy.mobilbayii.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dataClasses.Urun

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myDatabaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDataBase()
        registerActionBar(R.id.my_tool_bar)
        initAndFillRecyclerView()
        initSpetegitButtun()
    }

    private fun initDataBase()
    {
        myDatabaseHelper = DatabaseHelper(this)
        myDatabaseHelper.addAdmin("admin", "admin")
        myDatabaseHelper.initializeDatabaseIfNeeded(this)
    }

    private fun initSearchBar(adapter: UrunAdapter) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun initRadioButtons(adapter: UrunAdapter) {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_asc -> adapter.sort(true)   // A-Z
                R.id.radio_desc -> adapter.sort(false) // Z-A
            }
        }
    }

    private fun initAndFillRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Veritabanından ürünleri çek
        val urunList = myDatabaseHelper.getAllProducts()

        // Adapter’i oluştur
        val urunAdapter = UrunAdapter(urunList)
        binding.recyclerView.adapter = urunAdapter

        // Arama ve sıralama filtrelerini başlat
        initSearchBar(urunAdapter)
        initRadioButtons(urunAdapter)
    }


    private fun initSpetegitButtun()
    {
        binding.sepetButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
            startActivity(intent)
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

    //Açılır çekmece pencere Butonlarına işlev verme
    private fun setupNavigationView() {
        binding.hamburgerButton.setOnClickListener {binding.drawerLayout.openDrawer(GravityCompat.START)}
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_sepet_page -> {
                    val intent = Intent(this, SepetActivity::class.java)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    true
                }

                R.id.nav_admin_page -> {
                    val intent = Intent(this, AdminGirisActivity::class.java)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    true
                }
            }
            false
        }
    }
}