package com.chocobi.groot.view.chat

data class ChatMessage(
    var message: String?,
    var sendId: String?
){
    constructor():this("", "")
}
