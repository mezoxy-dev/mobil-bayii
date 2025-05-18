package dataClasses

data class StockPhone(

    val product_id: Int,
    val name: String,
    val quantity: Int,
    val imageUrl: String? = null // Picasso/Glide kütüphanesi kullanarak görsel yükelemk için url

)
