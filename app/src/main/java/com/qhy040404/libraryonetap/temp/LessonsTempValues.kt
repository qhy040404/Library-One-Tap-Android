package com.qhy040404.libraryonetap.temp

import com.qhy040404.libraryonetap.constant.Constants

class LessonsTempValues {
    var semesterName = Constants.STRING_NULL
    val lessonId: MutableList<Int> = mutableListOf()
    val lessonCode: MutableList<String> = mutableListOf()
    val lessonCompulsory: MutableList<String> = mutableListOf()
    val lessonName: MutableList<String> = mutableListOf()
    val lessonCredit: MutableList<Double> = mutableListOf()
    val lessonExamMode: MutableList<String> = mutableListOf()
    val lessonOpenDepart: MutableList<String> = mutableListOf()
    val lessonTeacher: MutableList<String> = mutableListOf()
    val lessonType: MutableList<String> = mutableListOf()

    fun clear() {
        semesterName = Constants.STRING_NULL
        lessonId.clear()
        lessonCode.clear()
        lessonCompulsory.clear()
        lessonName.clear()
        lessonCredit.clear()
        lessonExamMode.clear()
        lessonOpenDepart.clear()
        lessonTeacher.clear()
        lessonType.clear()
    }
}
