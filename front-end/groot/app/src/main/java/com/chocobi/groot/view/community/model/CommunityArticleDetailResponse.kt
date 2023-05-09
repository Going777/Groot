package com.chocobi.groot.view.community.model

data class CommunityArticleDetailResponse(
    val article: Article,
)

data class Article (
    val category: String,
    val imgs: List<String>?,
    val userPK: Int,
    val nickName: String,
    val title: String,
    val tags: List<String>?,
    val views: Int,
    val commentCnt: Int,
    val bookmark: Boolean,
    val content: String,
    val shareRegion: String?,
    val shareStatus: Boolean?,
    val createTime: CreateTime,
    val updateTime: UpdateTime,
)

data class BookmarkResponse(
    val result: String,
    val msg: String,
)

data class CommunityShareItemResponse(
    val result: String,
    val msg: String,
    val articles: List<ShareArticles>
)

data class ShareArticles(
    val userPK: Int,
    val nickname: String,
    val articleId: Int,
    val title: String,
    val img: String
)