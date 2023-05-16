package com.chocobi.groot.view.community.model

data class PopularTagResponse (
    val result: String,
    val msg: String,
    val tags: ArrayList<Tag>
    )

data class Tag (
    val tag: String,
    val count: Int
        )