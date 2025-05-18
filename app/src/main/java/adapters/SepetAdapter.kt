package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mezoxy.mobilbayii.R
import dataClasses.Urun

class SepetAdapter(
    private var sepetList: MutableList<Urun>,
    private val onItemRemoved: (Urun) -> Unit
) : RecyclerView.Adapter<SepetAdapter.SepetViewHolder>() {

    inner class SepetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image_phone)
        val name = itemView.findViewById<TextView>(R.id.text_name)
        val brand = itemView.findViewById<TextView>(R.id.text_brand)
        val specs = itemView.findViewById<TextView>(R.id.text_specs)
        val stock = itemView.findViewById<TextView>(R.id.text_stock)
        val price = itemView.findViewById<TextView>(R.id.text_price)
        val removeButton = itemView.findViewById<Button>(R.id.button_add_to_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SepetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_urun, parent, false)
        return SepetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SepetViewHolder, position: Int) {
        val urun = sepetList[position]

        holder.name.text = urun.ad
        holder.brand.text = "Marka: ${urun.marka}"
        holder.specs.text = "Özellikler: ${urun.ozellikler}"
        holder.stock.text = if (urun.stoktaVarMi) "Stok: Var" else "Stok: Yok"
        holder.stock.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (urun.stoktaVarMi) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )
        holder.price.text = "₺%.2f".format(urun.fiyat)
        holder.removeButton.text = "Sepetten Çıkar"
        if (!urun.image_url.isNullOrEmpty()) {
            Glide.with(holder.image.context)
                .load(urun.image_url)
                .placeholder(R.drawable.phone1)
                .error(R.drawable.phone1)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.phone1)
        }

        holder.removeButton.setOnClickListener {
            sepetList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, sepetList.size)
            onItemRemoved(urun)
        }
    }

    override fun getItemCount(): Int = sepetList.size
}
