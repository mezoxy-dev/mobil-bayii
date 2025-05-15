package com.mezoxy.mobilbayii

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mezoxy.mobilbayii.databinding.ActivityMainBinding
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerActionBar(R.id.my_tool_bar)


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