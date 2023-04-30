package com.chocobi.groot.data

data class ModelDiary(
    val id: Number,
    val potId: Number,
    val potName: String,
    val image: String,
    val content: String,
    val water: Boolean,
    val nutrients: Boolean,
    val pruning: Boolean,
    val bug: Boolean,
    val sun: Boolean,
    val createDate: String,
)
