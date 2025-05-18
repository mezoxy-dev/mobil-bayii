package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mezoxy.mobilbayii.R
import dataClasses.Request

class RequestAdapter(
    private val requestList: List<Request>,
    private val context: Context
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    private var filteredList = requestList.toMutableList()

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRequestId: TextView = itemView.findViewById(R.id.textRequestId)
        val productContainer: LinearLayout = itemView.findViewById(R.id.productContainer)
        val buttonApprove: Button = itemView.findViewById(R.id.buttonApprove)
        val buttonReject: Button = itemView.findViewById(R.id.buttonReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }


    fun filterByRequestId(query: String?) {
        filteredList = if (query.isNullOrEmpty()) {
            requestList.toMutableList()
        } else {
            requestList.filter {
                it.requestId.toString().contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = filteredList[position]

        holder.textRequestId.text = "Talep ID: #${request.requestId}"

        // Ürün container'ı önce temizle
        holder.productContainer.removeAllViews()

        // Her ürün için küçük bir view oluştur ve ekle
        for (urun in request.urunList) {
            val urunView = LayoutInflater.from(context)
                .inflate(R.layout.item_product, holder.productContainer, false)

            val imageView: ImageView = urunView.findViewById(R.id.imageProduct)
            val nameView: TextView = urunView.findViewById(R.id.textProductName)

            nameView.text = urun.ad
            if (!urun.image_url.isNullOrEmpty()) {
                Glide.with(context).load(urun.image_url).into(imageView)
            }

            holder.productContainer.addView(urunView)
        }

        // Buton click örnekleri:
        holder.buttonApprove.setOnClickListener {
            Toast.makeText(context, "Talep ${request.requestId} onaylandı", Toast.LENGTH_SHORT).show()
            // Buraya onaylama işlemini gerçekleştirebilirsiniz
            // Kullanıcı bilgilerini kayıt etmek için Customers sayfasına gidilir
            // kullanıcı bilgileri kayıt edildikten sonra Order Alınır ve Request Tablosundan Request_conf durumu true yapılır
        }

        holder.buttonReject.setOnClickListener {
            Toast.makeText(context, "Talep ${request.requestId} reddedildi", Toast.LENGTH_SHORT).show()
            // Talebi göstermeyi bırakabiliriz
        }
    }

    override fun getItemCount(): Int = filteredList.size
}

