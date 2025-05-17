package com.mezoxy.mobilbayii

import adapters.SepetAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezoxy.mobilbayii.databinding.ActivitySepetBinding
import dataClasses.Urun

private lateinit var binding: ActivitySepetBinding
private lateinit var adapter: SepetAdapter

class SepetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        val sepetList = SepetManager.sepetList

        setupNavigationView()
    }

    private fun setupRecyclerView(sepetList: MutableList<Urun>) {
        adapter = SepetAdapter(sepetList) { removedItem ->
            updateTotalPrice(sepetList)
            if (sepetList.isEmpty()) {
                binding.emptyCartText.visibility = View.VISIBLE
                binding.emptyCartText.visibility = View.GONE
            }
        }

        binding.recyclerViewSepet.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSepet.adapter = adapter
        updateTotalPrice(sepetList)
    }

    private fun updateTotalPrice(sepetList: List<Urun>) {
        val toplam = sepetList.sumOf { it.fiyat }
        binding.totalPrice.text = "Toplam: %.2f".format(toplam)
    }

    //Açılır çekmece pencere Butonlarına işlev verme
    private fun setupNavigationView() {
        binding.sepetHamburgerButton.setOnClickListener {binding.drawerLayoutSepet.openDrawer(GravityCompat.START)}
        binding.navViewSepet.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_admin_page -> {
                    val intent = Intent(this, AdminPageActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
            false
        }
    }
}
