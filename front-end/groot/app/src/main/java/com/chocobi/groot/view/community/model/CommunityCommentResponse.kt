package com.chocobi.groot.view.community.model

// output을 만든다 : response

data class CommunityCommentResponse(
    val comment: List<Comment>,
    val result: String,
    val msg: String,
)

data class CommunityCommentPostResponse(
    val comment: Comment,
    val result: String,
    val msg: String,
)

data class Comment (
    val id: Int,
    val userPK: Int,
    val nickName: String,
    val content: String,
    val profile: String?,
    val createTime: CreateTime,
    val updateTime: UpdateTime
)


