package dataClasses

import java.io.Serializable

data class Urun(
    val urun_id: Int,
    val resim: Int,
    val ad: String,
    val marka: String,
    val fiyat: Double,
    val ozellikler: String,
    val stoktaVarMi: Boolean
) : Serializable