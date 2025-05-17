package adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.compose.animation.with
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import androidx.core.graphics.values
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import com.mezoxy.mobilbayii.R
import com.mezoxy.mobilbayii.databinding.ItemStockPhoneBinding
import dataClasses.StockPhone
import kotlin.text.clear
import kotlin.text.isNotEmpty
import kotlin.text.trim
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop


class StockAdapter(private var stockPhoneList: List<StockPhone>) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    // Filtrelenmiş listeyi tutmak için ayrı bir liste. Başlangıçta tüm öğeleri içerir.
    private var stockPhoneListFiltered: MutableList<StockPhone> = stockPhoneList.toMutableList()

    // Arayüz tanımı
    interface OnStockUpdateListener {
        fun onUpdateStock(productId: Int, newQuantity: Int, itemPosition: Int) // Pozisyonu da gönderelim
    }

    inner class StockViewHolder(val binding: ItemStockPhoneBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stockPhone: StockPhone) {
            binding.textViewPhoneNameStock.text = stockPhone.brand //TODO: NAME OLACAK
            binding.textViewCurrentStockValue.text = stockPhone.quantity.toString()

            // Glide ile resim yükleme
            Glide.with(binding.imageViewPhoneStock.context)
                .load(stockPhone.imageUrl) // imageUrl null veya boş olabilir, kontrol ekleyebilirsiniz
                .placeholder(R.drawable.phone1) // Projenizde bu drawable'ların olduğundan emin olun
                .error(R.drawable.ic_launcher_foreground)   // Projenizde bu drawable'ların olduğundan emin olun
                .into(binding.imageViewPhoneStock)

            // Güncelle butonuna tıklama olayı
            binding.buttonUpdateStock.setOnClickListener {
                val newQuantityString = binding.editTextNewStockQuantity.text.toString().trim()
                if (newQuantityString.isNotEmpty()) {
                    try {
                        val newQuantity = newQuantityString.toInt()
                        if (newQuantity >= 0) { // Negatif stok olmaması için kontrol
                            // Adapter pozisyonunu değil, veri listesindeki gerçek pozisyonu veya ID'yi kullanmak daha güvenli.
                            // Ancak hızlı güncelleme için adapterPosition kullanılabilir.
                            // Eğer productId StockPhone içinde varsa onu kullanmak en iyisi.
                            // StockPhone veri sınıfınızda productId olduğundan emin olun.
//                            listener.onUpdateStock(stockPhone.productId, newQuantity, adapterPosition)
//                            binding.editTextNewStockQuantity.text?.clear() // Giriş alanını temizle
                        } else {
                            Toast.makeText(binding.root.context, "Stok miktarı negatif olamaz.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(binding.root.context, "Lütfen geçerli bir sayı girin.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "Yeni stok miktarını girin.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockPhoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockPhoneListFiltered[position]) // Filtrelenmiş listeyi kullan
    }

    override fun getItemCount(): Int {
        return stockPhoneListFiltered.size // Filtrelenmiş listenin boyutunu döndür
    }

    // Filtreleme mekanizması
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val charString = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
//                stockPhoneListFiltered = if (charString.isEmpty()) {
//                    stockPhoneList.toMutableList() // Arama boşsa orijinal listeyi göster
//                } else {
//                    val filteredList = mutableListOf<StockPhone>()
////                    stockPhoneList.forEach { phone ->
////                        if (phone.name.lowercase(Locale.getDefault()).contains(charString)) {
////                            filteredList.add(phone)
////                        }
////                    }
//                    filteredList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = stockPhoneListFiltered
//                return filterResults
//            }
//
//            @SuppressLint("NotifyDataSetChanged") // Dikkatli kullanın, daha spesifik notify'lar tercih edilebilir
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                stockPhoneListFiltered = results?.values as? MutableList<StockPhone> ?: mutableListOf()
//                notifyDataSetChanged() // Listenin tamamının güncellendiğini bildir
//            }
//        }
//    }

    // Activity'den listeyi güncellemek için bir metot (isteğe bağlı ama kullanışlı)
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<StockPhone>) {
        stockPhoneList = newList // Ana listeyi de güncelle
        stockPhoneListFiltered = newList.toMutableList()
        notifyDataSetChanged()
    }

    // Belirli bir öğeyi güncellemek ve RecyclerView'ı haberdar etmek için
    fun updateItem(position: Int, updatedPhone: StockPhone) {
        if (position >= 0 && position < stockPhoneListFiltered.size) {
            stockPhoneListFiltered[position] = updatedPhone
            notifyItemChanged(position) // Sadece değişen öğeyi güncelle

            // Ana listeyi de güncellemek gerekebilir, eğer productId ile eşleşiyorsa
//            val originalIndex = stockPhoneList.indexOfFirst { it.productId == updatedPhone.productId }
//            if (originalIndex != -1) {
//                (stockPhoneList as MutableList<StockPhone>)[originalIndex] = updatedPhone
            }
        }
    }
