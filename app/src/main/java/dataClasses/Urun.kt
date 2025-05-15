package dataClasses

import java.io.Serializable

data class Urun(
    val resim: Int,
    val ad: String,
    val marka: String,
    val ozellikler: String,
    val stoktaVarMi: Boolean
) : Serializable