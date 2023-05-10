package com.chocobi.groot.data

import com.chocobi.groot.view.pot.model.DateTime

data class ModelDiary(
    val userPK: Int,
    val nickName: String,
    val id: Int,
    val potId: Int,
    val potName: String,
    val image: String,
    val content: String,
    val water: Boolean,
    val nutrients: Boolean,
    val pruning: Boolean,
    val bug: Boolean,
    val sun: Boolean,
    val createTime: DateTime,
    val updateTime: DateTime,
    val isPotLast: Boolean,
    val isUserLast: Boolean
)
