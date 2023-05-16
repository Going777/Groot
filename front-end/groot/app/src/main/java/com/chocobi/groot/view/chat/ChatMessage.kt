package com.chocobi.groot.view.chat

data class ChatMessage(
    var message: String?,
    var sendId: String?,
    var saveTime: String?
){
    constructor():this("", "", "")
}
