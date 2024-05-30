package com.example.waitless_p1.Data
import java.io.Serializable

data class Reserva(
    var numero: Int = 0,
    var rParque: String = "",
    var rAId: Int = 0,
    var rAtraccion: String = "",
    var rEstado: Boolean = false,
    var hora: String = "",
    var fecha: String = "",
    var asientos: Int = 0,
    var titular: String = ""
) : Serializable {
    // Constructor sin argumentos necesario para Firebase Database
    constructor() : this(0, "", 0, "", false, "", "", 0, "")
}

