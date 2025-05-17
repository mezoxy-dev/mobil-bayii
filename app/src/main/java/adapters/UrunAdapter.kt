package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mezoxy.mobilbayii.R
import com.mezoxy.mobilbayii.SepetManager
import dataClasses.Urun

class UrunAdapter(private var urunList: List<Urun>) : RecyclerView.Adapter<UrunAdapter.UrunViewHolder>() {

    private var fullUrunList: List<Urun> = urunList.toList()
    private var filteredUrunList: List<Urun> = urunList.toList()
    private var currentQuery: String = ""
    private var isAscending: Boolean = true

    inner class UrunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fiyat: TextView = itemView.findViewById(R.id.text_price)
        val resim: ImageView = itemView.findViewById(R.id.image_phone)
        val ad: TextView = itemView.findViewById(R.id.text_name)
        val marka: TextView = itemView.findViewById(R.id.text_brand)
        val ozellikler: TextView = itemView.findViewById(R.id.text_specs)
        val stok: TextView = itemView.findViewById(R.id.text_stock)
        val sepeteEkleBtn: Button = itemView.findViewById(R.id.button_add_to_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_urun, parent, false)
        return UrunViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UrunViewHolder, position: Int) {
        val urun = filteredUrunList[position]

        holder.resim.setImageResource(urun.resim)
        holder.ad.text = urun.ad
        holder.marka.text = urun.marka
        holder.ozellikler.text = urun.ozellikler
        holder.fiyat.text = urun.fiyat.toString()

        holder.sepeteEkleBtn.setOnClickListener {
            if (!SepetManager.sepetList.contains(urun)) {
                SepetManager.sepetList.add(urun)
                Toast.makeText(holder.itemView.context, "${urun.ad} sepete eklendi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "${urun.ad} zaten sepette", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = filteredUrunList.size

    fun filter(query: String) {
        currentQuery = query
        applyFilterAndSort()
    }

    fun sort(ascending: Boolean) {
        isAscending = ascending
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        filteredUrunList = fullUrunList
            .filter {
                it.ad.contains(currentQuery, ignoreCase = true) ||
                        it.marka.contains(currentQuery, ignoreCase = true)
            }
            .sortedWith(compareBy {
                val name = it.ad.lowercase()
                val index = name.indexOf(currentQuery.lowercase())
                if (index >= 0) index else Int.MAX_VALUE // "One" en başta geçenler öne alınır
            })

        if (!isAscending) {
            filteredUrunList = filteredUrunList.reversed()
        }

        notifyDataSetChanged()
    }




    fun updateList(newList: List<Urun>) {
        urunList = newList
        fullUrunList = newList
        filteredUrunList = newList
        notifyDataSetChanged()
    }
}