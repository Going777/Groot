package com.chocobi.groot.view.chat.adapter

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.model.ChatMessage
import java.util.Locale

class ChatMessageAdapter(private val context: Context, private val messageList: ArrayList<ChatMessage>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val receive = 1 //받는 타입
    private val send = 2 //보내는 타입

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType == 1){ //받는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.fragment_chat_receive, parent, false)
            ReceiveViewHolder(view)

        }else{ //보내는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.fragment_chat_send, parent, false)
            SendViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //현재 메시지
        val currentMessage = messageList[position]

        //보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
            viewHolder.sendTime.text = formatDate(currentMessage.saveTime.toString())

        }else{//받는 데이터
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
            viewHolder.receiveTime.text = formatDate(currentMessage.saveTime.toString())
        }
    }

    private fun formatDate(dateString: String): String {
        Log.d("dateString", dateString)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd a hh:mm", Locale.getDefault())
        Log.d("dateStringInputFormat", inputFormat.toString())
        val outputFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
        Log.d("dateStringOutputFormat", outputFormat.toString())
        val date = inputFormat.parse(dateString)
        Log.d("dateStringDate", date.toString())

        return outputFormat.format(date)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("messageList", messageList.toString())


        //메시지값
        val currentMessage = messageList[position]

        return if(changeRoomNumber(UserData.getUserPK().toString()).toString() == currentMessage.sendId){
            send
        }else{
            receive
        }
    }

    //보낸 쪽
    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sendMessage: TextView = itemView.findViewById(R.id.sendMessageText)
        val sendTime: TextView = itemView.findViewById(R.id.sendTime)
    }

    //받는 쪽
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.receiveMessageText)
        val receiveTime: TextView = itemView.findViewById(R.id.receiveTime)
    }

    private fun changeRoomNumber(number: String?): String {
        val formattedNumber = number?.padStart(6, '0')
        return formattedNumber ?: ""
    }
}
