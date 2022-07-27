package com.qhy040404.libraryonetap.temp

object GradesTempValues {
    var secondStuId = 0

    var semesters: MutableList<Int> = mutableListOf()
    var semestersName: MutableList<String> = mutableListOf()

    var courseName: MutableList<String> = mutableListOf()
    var courseCode: MutableList<String> = mutableListOf()
    var courseCredits: MutableList<Double> = mutableListOf()
    var courseGrade: MutableList<String> = mutableListOf()
    var courseGP: MutableList<Double> = mutableListOf()
    var courseStudyType: MutableList<String> = mutableListOf()

    var courseCountList: MutableList<Int> = mutableListOf()

    var startCount = 0
    var endCount = 0

    var secondSemesters: MutableList<Int> = mutableListOf()
    var secondSemestersName: MutableList<String> = mutableListOf()

    var secondCourseName: MutableList<String> = mutableListOf()
    var secondCourseCode: MutableList<String> = mutableListOf()
    var secondCourseCredits: MutableList<Double> = mutableListOf()
    var secondCourseGrade: MutableList<String> = mutableListOf()
    var secondCourseGP: MutableList<Double> = mutableListOf()
    var secondCourseStudyType: MutableList<String> = mutableListOf()

    var secondCourseCountList: MutableList<Int> = mutableListOf()

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