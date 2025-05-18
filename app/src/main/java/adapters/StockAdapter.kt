    package adapters

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.*
    import com.google.android.material.textfield.TextInputEditText
    import com.mezoxy.mobilbayii.DatabaseHelper
    import dataClasses.StockPhone
    import com.mezoxy.mobilbayii.R
    import com.mezoxy.mobilbayii.StockActivity
    import com.mezoxy.mobilbayii.databinding.ItemStockPhoneBinding
    import android.content.Context

    class StockAdapter(
        private var phoneList: MutableList<StockPhone> = mutableListOf<StockPhone>(),
        private val dbHelper: DatabaseHelper,
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
            val buttonUrunDelete: Button = itemView.findViewById(R.id.button_delete_product)
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
            holder.buttonUrunDelete.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context).apply {
                    setTitle("Ürünü Sil")
                    setMessage("Bu ürünü silmek istediğinize emin misiniz?")
                    setPositiveButton("Sil") { dialog, _ ->
                        val success = dbHelper.deleteProductById(phone.product_id)

                        if (success) {
                            Toast.makeText(holder.itemView.context, "Ürün silindi", Toast.LENGTH_SHORT).show()
                            phoneList.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                        } else {
                            Toast.makeText(holder.itemView.context, "Silme işlemi başarısız", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton("İptal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }

        }

        fun updateData(newList: List<StockPhone>) {
            phoneList = ArrayList(newList)
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
                    phoneList = results?.values as MutableList<StockPhone>
                    notifyDataSetChanged()
                }
            }
        }
    }
