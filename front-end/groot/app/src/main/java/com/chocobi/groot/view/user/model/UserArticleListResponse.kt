package com.chocobi.groot.view.user.model

import com.chocobi.groot.view.community.model.Articles

data class UserArticleListResponse(
    val result: String,
    val msg: String,
    val articles: Articles
)


