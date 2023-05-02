package com.chocobi.groot.view.user.model

data class UserArticleListResponse(
    val result: String,
    val msg: String,
    val articles: Articles,
)

data class Articles (
    val total: Int,
    val content: List<Content>,
    val pageable: Pageable,
    val totalElements: Int
)

data class Content (
    val articleId: Int,
    val category: String,
    val userPK: Int,
    val img: String?,
    val nickName: String,
    val profile: String?,
    val title: String,
    val tags: List<String>,
    val views: Int,
    val commentCnt: Int,
    val bookmark: Boolean,
    val createTime: String,
    val updateTime: String
)

data class Pageable (
    val pageNumber: Int,
    val pageSize: Int
)



