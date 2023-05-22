package com.chocobi.groot.view.main

data class PlantNamesResponse(
    val msg: String,
    val nameList: MutableList<String>
)

data class RegionNameResponse(
    val msg: String,
    val result: String,
    val regions: MutableList<String>
)

data class CharacterResponse(
    val msg: String,
    val characters: MutableList<Character>
)

data class Character(
    val grwType: String,
    val level: Int,
    val pngPath: String
)