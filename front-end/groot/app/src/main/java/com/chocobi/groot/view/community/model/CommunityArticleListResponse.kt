package com.chocobi.groot.view.community.model

data class CommunityArticleListResponse(
    val result: String,
    val msg: String,
    val articles: Articles,
)

data class Articles (
    val total: Int,
    var content: List<Content>,
    val pageable: Pageable
)

data class Content (
    val articleId: Int,
    val category: String,
    val userPK: Int,
    val img: String?,
    val nickName: String,
    val title: String,
    val tags: List<String>,
    val views: Int,
    val commentCnt: Int,
    val bookmark: Boolean,
    val createTime: CreateTime,
    val updateTime: UpdateTime
)

data class CreateTime (
    val date: Date,
    val time: Time
)

data class UpdateTime (
    val date: Date,
    val time: Time
)

data class Date (
    val year: Int,
    val month: Int,
    val day: Int
)

data class Time (
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nano: Int
)

data class Pageable (
    val sort: Sort,
    val page: Int,
    val size: Int
)

data class Sort (
    val orders: List<Order>
)

data class Order (
    val direction: String,
    val property: String,
    val ignoreCase: Boolean,
    val nullHandling: String
)