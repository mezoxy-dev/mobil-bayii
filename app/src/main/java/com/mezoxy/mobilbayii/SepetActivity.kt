package com.mezoxy.mobilbayii

import adapters.SepetAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezoxy.mobilbayii.databinding.ActivitySepetBinding
import dataClasses.Urun

class SepetActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySepetBinding
    private lateinit var adapter: SepetAdapter
    private lateinit var myDatabaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        myDatabaseHelper = DatabaseHelper(this)
        val intent = intent
        val sepetList = SepetManager.sepetList
        initCheckOutButton(sepetList)
        setupRecyclerView(sepetList)
        setupNavigationView()
    }

    private fun setupRecyclerView(sepetList: MutableList<Urun>) {
        adapter = SepetAdapter(sepetList) { removedItem ->
            updateTotalPrice(sepetList)
        }
        binding.recyclerViewSepet.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSepet.adapter = adapter
        updateTotalPrice(sepetList)
    }

    private fun initCheckOutButton(sepetList: MutableList<Urun>)
    {
        binding.checkoutButton.setOnClickListener {
            var stokSorunuVar = false
            sepetList.forEach {urun ->
                if (!myDatabaseHelper.urunStoktaVarMi(urun.urun_id)){
                    myDatabaseHelper.showStockAlert("${urun.ad} tükenmiştir!", this, "Stok Uyarısı!")
                    urun.stoktaVarMi = false
                    stokSorunuVar = true
                }
            }
            if (stokSorunuVar) {
                adapter.notifyDataSetChanged()
                return@setOnClickListener
            }
            var requestId = myDatabaseHelper.insertRequest()
            sepetList.forEach {urun ->
                myDatabaseHelper.insertRequestItem(requestId, urun.urun_id, urun.fiyat)
            }
            sepetList.clear()
            adapter.notifyDataSetChanged()
            updateTotalPrice(sepetList)
            myDatabaseHelper.showStockAlert("Talep Onaylandı\nTalep No: $requestId", this, "Talep Onaylandı!")
        }
    }

    private fun updateTotalPrice(sepetList: List<Urun>) {
        val toplam = sepetList.sumOf { it.fiyat }
        binding.totalPrice.text = "Toplam: %.2f".format(toplam)
        if (sepetList.isEmpty())
            binding.emptyCartText.visibility = View.VISIBLE
        else
            binding.emptyCartText.visibility = View.INVISIBLE
        adapter.notifyDataSetChanged()
    }

    //Açılır çekmece pencere Butonlarına işlev verme
    private fun setupNavigationView() {
        binding.sepetHamburgerButton.setOnClickListener {binding.drawerLayoutSepet.openDrawer(GravityCompat.START)}
        binding.navViewSepet.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    binding.drawerLayoutSepet.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    true
                }
                R.id.nav_sepet_page -> {
                    val intent = Intent(this, SepetActivity::class.java)
                    binding.drawerLayoutSepet.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_admin_page -> {
                    val intent = Intent(this, AdminGirisActivity::class.java)
                    binding.drawerLayoutSepet.closeDrawer(GravityCompat.START)
                    startActivity(intent)
                    true
                }
            }
            false
        }
    }
}
