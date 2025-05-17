package dataClasses

data class StockPhone(


    val brand : String,
    val model : String,
    val quantity : Int,
    val imageUrl : String? = null // Picasso/Glide kütüphanesi kullanarak görsel yükelemk için url

)
