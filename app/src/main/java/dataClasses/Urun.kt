package dataClasses

import java.io.Serializable

data class Urun(
    val urun_id: Int,
    val image_url: String?,
    val ad: String,
    val marka: String,
    val fiyat: Double,
    val ozellikler: String,
    var stoktaVarMi: Boolean
) : Serializable