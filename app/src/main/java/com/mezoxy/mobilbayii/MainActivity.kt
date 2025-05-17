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
    private lateinit var sepetListesi: MutableList<Urun>
    private lateinit var myDatabaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerActionBar(R.id.my_tool_bar)
        sepetListesi = mutableListOf<Urun>()
        initAndFillRecyclerView()
        initSpetegitButtun(sepetListesi)

        initDataBase()

    }

    private fun initDataBase()
    {
        myDatabaseHelper = DatabaseHelper(this)
        myDatabaseHelper.addAdmin("admin", "admin")
        //myDatabaseHelper.addSampleProductsAndStocks()

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

    private fun initAndFillRecyclerView()
    {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        var urunList = listOf(
            Urun(R.drawable.phone1, "iPhone 13", "Apple", 7999.99, "128GB, 4GB RAM", true),
            Urun(R.drawable.phone1, "Galaxy S21", "Samsung", 11999.99, "256GB, 8GB RAM", false),
            Urun(R.drawable.phone1, "Redmi Note 12", "Xiaomi", 9999.99, "128GB, 6GB RAM", true)
        )

        val urunAdapter = UrunAdapter(urunList)
        binding.recyclerView.adapter = urunAdapter
        initSearchBar(urunAdapter)
        initRadioButtons(urunAdapter)
    }

    private fun initSpetegitButtun(sepetListesi: MutableList<Urun>)
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
}