package com.qhy040404.libraryonetap.temp

object ExamsTempValues {
    val courseName: MutableList<String> = mutableListOf()
    val examTime: MutableList<String> = mutableListOf()
    val building: MutableList<String> = mutableListOf()
    val room: MutableList<String> = mutableListOf()

    fun clear() {
        courseName.clear()
        examTime.clear()
        building.clear()
        room.clear()
    }
}
