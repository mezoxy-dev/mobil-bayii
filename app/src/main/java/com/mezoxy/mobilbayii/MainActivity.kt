package com.mezoxy.mobilbayii

import adapters.UrunAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mezoxy.mobilbayii.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dataClasses.Urun

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sepetListesi: MutableList<Urun>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerActionBar(R.id.my_tool_bar)
        initAndFillRecyclerView()
        initSpetegitButtun(sepetListesi)

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
        sepetListesi = mutableListOf<Urun>()

        var urunList = listOf(
            Urun(R.drawable.phone1, "iPhone 13", "Apple", "128GB, 4GB RAM", true),
            Urun(R.drawable.phone1, "Galaxy S21", "Samsung", "256GB, 8GB RAM", false),
            Urun(R.drawable.phone1, "Redmi Note 12", "Xiaomi", "128GB, 6GB RAM", true)
        )

        val urunAdapter = UrunAdapter(urunList, sepetListesi)
        binding.recyclerView.adapter = urunAdapter
        initSearchBar(urunAdapter)
        initRadioButtons(urunAdapter)
    }

    private fun initSpetegitButtun(sepetListesi: MutableList<Urun>)
    {
        binding.sepetButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
            intent.putExtra("sepet", ArrayList(sepetListesi)) // MutableList → ArrayList
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
            binding.sepetButton.setOnClickListener {sepet_button_func()}
            binding.hamburgerButton.setOnClickListener {hamburger_button_func()}
            //TODO: istenen button işlevleri, kayarak açılan menü vs.
        }
    }

    public fun sepet_button_func()
    {
        Toast.makeText(this, "func1", Toast.LENGTH_SHORT).show()
    }

    public fun hamburger_button_func()
    {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
}