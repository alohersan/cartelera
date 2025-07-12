package com.example.integradora_ut2

data class Peliculas( val id:String,val titulo:String,val año:Int,val clasificacion:String) {
    override fun toString(): String {
        return "Peliculas(id='$id',titulo='$titulo',año='$año',clasificacion='$clasificacion')"
    }
}
