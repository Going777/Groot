package com.chocobi.groot.view.pot.model

import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.view.community.model.Pageable

data class PotResponse(
    val msg: String,
    val pot: Pot,
    val plant: Plant,
    val waterDate: ComingDate?,
    val pruningDate: ComingDate?,
    val nutrientsDate: ComingDate?
)
data class ComingDate(
    val code: Int,
    val dateTime: DateTime
)

//data class Plan(
//    val code: Int,
//    val dateTime: DateTime,
//)

data class PotImgResponse(
    val msg: String,
    val img: String
)

data class PotListResponse(
    val msg: String,
    val pots: List<Pot>,
)

data class DateDiaryResponse(
    val result: String,
    val msg: String,
    val diary: List<Diary>
)

data class DiaryListResponse(
    val result: String,
    val msg: String,
    val diary: Diaries
)

data class Diaries(
    val total: Int,
    val content: List<ModelDiary>,
    val pageable: Pageable
)
data class Diary(
    val potId: Int,
    val userPK: Int,
    val code: Int,
    val potName: String,
    val imgPath: String?,
    val done: Boolean
)

data class Pot(
    val potId: Int,
    val plantId: Int,
    val potName: String,
    val imgPath: String,
    val plantKrName: String,
    val dates: Int,
    val createdTime: DateTime,
    val waterDate: DateTime?,
    val nutrientsDate: DateTime?,
    val pruningDate: DateTime?,
    val survival: Boolean,
    val level: Int,
    val characterGLBPath: String,
    val characterPNGPath: String,
)

data class DateTime(
    val date: Date,
    val time: Time
)

data class Date(
    val year: Int,
    val month: Int,
    val day: Int
)

data class Time(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nano: Int
)

data class Plant(
    val plantId: Int,
    val krName: String,
    val sciName: String,
    val grwType: String,
    val waterCycle: String,
    val minHumidity: Int,
    val maxHumidity: Int,
    val minGrwTemp: Int,
    val maxGrwTemp: Int,
    val description: String,
    val place: String,
    val mgmtLevel: String,
    val mgmtDemand: String,
    val mgmtTip: String,
    val insectInfo: String,
    val img: String
)

class DiaryRequest internal constructor(
    val potId: Int,
    val content: String?,
    val water: Boolean?,
    val pruning: Boolean?,
    val bug: Boolean?,
    val sun: Boolean?,
    val nutrients: Boolean?
)

class EditDiaryRequest internal constructor(
    val id:Int,
    val potId: Int,
    val content: String?,
    val water: Boolean?,
    val pruning: Boolean?,
    val bug: Boolean?,
    val sun: Boolean?,
    val nutrients: Boolean?,
    val userPK: Int,
    val diaryId: Int
)

data class DiaryCheckStatusResponse(
    val result: String,
    val msg: String,
    val diary: DiaryCheckStatus,
)

data class DiaryCheckStatus(
    val potId: Int,
    val userPK: Int,
    val water: Boolean?,
    val pruning: Boolean?,
    val nutrients: Boolean?,
    val bug: Boolean?,
    val sun: Boolean?,
)