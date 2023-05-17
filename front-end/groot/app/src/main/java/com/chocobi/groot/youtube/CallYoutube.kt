package com.chocobi.groot.youtube

import android.app.Application
import android.util.Log
import com.chocobi.groot.BuildConfig
import com.google.api.client.json.JsonFactory
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Thumbnail


class CallYoutube(private var application: Application, keyword: String) {

    private lateinit var result: String
    private val videoIdList = mutableListOf<String>()
    private val API_KEY = BuildConfig.YOUTUBE_API_KEY
    private val keyword = keyword
    private val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory()

    fun onCallYoutubeChannel(): String? {
        try {
            val youtube = YouTube.Builder(
                HTTP_TRANSPORT, JSON_FACTORY
            ) { }.setApplicationName("Groot").build()
            val search = youtube.search().list("id,snippet")
            val searchResultList = search.setKey(API_KEY)
                .setQ(keyword)
                .setOrder("relevance")
                .setType("video")
                .setMaxResults(1)
                .execute()
                .items
            if (searchResultList != null) {
                val iteratorSearchResults = searchResultList.iterator()
                if (!iteratorSearchResults.hasNext()) {
                    return null
                }

                while (iteratorSearchResults.hasNext()) {
                    val singleVideo = iteratorSearchResults.next()
                    val rId = singleVideo.id

                    videoIdList.add(rId.videoId)
                }
                Log.d("CallYoutube", "printYoutubeResult() 유튜브 여기sssss $videoIdList")
                return videoIdList.get(0)

//                printYoutubeResult(searchResultList.iterator())
            }
        } catch (e: Throwable) {
            Log.e("Exception Occurred : 유튜브ssss", e.toString())
        }
        return null
    }

//    private fun printYoutubeResult(iteratorSearchResults: Iterator<SearchResult>): String? {
//        if (!iteratorSearchResults.hasNext()) {
//            println(" There aren't any results for your query.")
//        }
//        val sb = StringBuilder()
//
//        while (iteratorSearchResults.hasNext()) {
//            val singleVideo = iteratorSearchResults.next()
//            val rId = singleVideo.id
//
//            videoIdList.add(rId.videoId)
//            val thumbnail = singleVideo.snippet.thumbnails["default"] as Thumbnail?
//            sb.append("ID : " + rId.videoId + " , 제목 : " + singleVideo.snippet.title + " , 썸네일 주소 : " + thumbnail!!.url)
//            sb.append("\n")
//
//        }
//        Log.d("CallYoutube", "printYoutubeResult() 유튜브 여기 $videoIdList")
//        result = sb.toString()
//        Log.d("CallYoutube", "printYoutubeResult() 유튜브 여기 $result")
//        return videoIdList.get(0)
//    }
}