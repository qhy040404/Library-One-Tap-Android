package com.qhy040404.libraryonetap.temp

object ExamsTempValues {
    var courseName: MutableList<String> = mutableListOf()
    var examTime: MutableList<String> = mutableListOf()
    var building: MutableList<String> = mutableListOf()
    var room: MutableList<String> = mutableListOf()

    fun clear() {
        courseName.clear()
        examTime.clear()
        building.clear()
        room.clear()
    }
}
