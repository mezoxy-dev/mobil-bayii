package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.*
import com.google.android.material.textfield.TextInputEditText
import dataClasses.StockPhone
import com.mezoxy.mobilbayii.R
import com.mezoxy.mobilbayii.databinding.ItemStockPhoneBinding

class StockAdapter(
    private var phoneList: List<StockPhone>,
    private val onUpdateStockClick: (phone: StockPhone, newQuantity: Int) -> Unit
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>(), android.widget.Filterable {
    private lateinit var binding : ItemStockPhoneBinding
    private var phoneListFull: List<StockPhone> = ArrayList(phoneList)


    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.textView_phone_name_stock)
        val quantityTextView: TextView = itemView.findViewById(R.id.textView_current_stock_value)
        val imageView: ImageView = itemView.findViewById(R.id.imageView_phone_stock)
        val buttonUpdateStock: Button = itemView.findViewById(R.id.button_update_stock)
        val editTextNewStock: TextInputEditText = itemView.findViewById(R.id.editText_new_stock_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock_phone, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val phone = phoneList[position]
        holder.name.text = phone.name

        holder.quantityTextView.text = "Adet: ${phone.quantity}"

        if (!phone.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.imageView.context)
                .load(phone.imageUrl)
                .placeholder(R.drawable.phone1)
                .error(R.drawable.phone1)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.phone1)
        }
        holder.buttonUpdateStock.setOnClickListener {
            val newQuantityStr = holder.editTextNewStock.text.toString()
            val context = holder.itemView.context
            if (newQuantityStr.isNotEmpty()) {
                val newQuantity = newQuantityStr.toIntOrNull()
                if (newQuantity != null && newQuantity >= 0) {
                    onUpdateStockClick(phone, newQuantity)
                } else {
                    Toast.makeText(context, "Geçerli bir sayı girin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Yeni stok miktarını girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateData(newList: List<StockPhone>) {
        phoneList = newList
        phoneListFull = ArrayList(newList)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return phoneList.size
    }


    override fun getFilter(): android.widget.Filter {
        return object : android.widget.Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<StockPhone>()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(phoneListFull)
                } else {
                    val filterPattern = constraint.toString().trim().lowercase()
                    for (item in phoneListFull) {
                        if (item.name.lowercase().contains(filterPattern)
                        ) {
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                phoneList = results?.values as List<StockPhone>
                notifyDataSetChanged()
            }
        }
    }
}
