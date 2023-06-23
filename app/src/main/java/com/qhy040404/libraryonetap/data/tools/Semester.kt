package com.qhy040404.libraryonetap.data.tools

data class Semester(
    val id: Int,
    val name: String,
    val courses: List<Grade>,
) {
    var needsEvaluationTasks = mutableListOf<String>()
}
