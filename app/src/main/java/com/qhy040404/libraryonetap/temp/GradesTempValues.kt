package com.qhy040404.libraryonetap.temp

object GradesTempValues {
    val semesters: MutableList<Int> = mutableListOf()
    val semestersName: MutableList<String> = mutableListOf()

    val courseName: MutableList<String> = mutableListOf()
    val courseCode: MutableList<String> = mutableListOf()
    val courseCredits: MutableList<Double> = mutableListOf()
    val courseGrade: MutableList<String> = mutableListOf()
    val courseGP: MutableList<Double> = mutableListOf()
    val courseStudyType: MutableList<String> = mutableListOf()

    val courseCountList: MutableList<Int> = mutableListOf()

    var startCount = 0
    var endCount = 0

    val secondSemesters: MutableList<Int> = mutableListOf()
    val secondSemestersName: MutableList<String> = mutableListOf()

    val secondCourseName: MutableList<String> = mutableListOf()
    val secondCourseCode: MutableList<String> = mutableListOf()
    val secondCourseCredits: MutableList<Double> = mutableListOf()
    val secondCourseGrade: MutableList<String> = mutableListOf()
    val secondCourseGP: MutableList<Double> = mutableListOf()
    val secondCourseStudyType: MutableList<String> = mutableListOf()

    val secondCourseCountList: MutableList<Int> = mutableListOf()

    var secondStartCount = 0
    var secondEndCount = 0

    fun clear() {
        semesters.clear()
        semestersName.clear()
        courseName.clear()
        courseCode.clear()
        courseCredits.clear()
        courseGrade.clear()
        courseGP.clear()
        courseStudyType.clear()
        courseCountList.clear()
        startCount = 0
        endCount = 0
        secondSemesters.clear()
        secondSemestersName.clear()
        secondCourseName.clear()
        secondCourseCode.clear()
        secondCourseCredits.clear()
        secondCourseGrade.clear()
        secondCourseGP.clear()
        secondCourseStudyType.clear()
        secondCourseCountList.clear()
        secondStartCount = 0
        secondEndCount = 0
    }
}
