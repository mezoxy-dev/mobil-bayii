package dataClasses

data class Request (
    val requestId: Int,
    var orderConf: Boolean,
    var urunList: MutableList<Urun>
)
