package com.qhy040404.libraryonetap.temp

object LessonsTempValues {
    var semesterName = ""
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
        semesterName = ""
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
