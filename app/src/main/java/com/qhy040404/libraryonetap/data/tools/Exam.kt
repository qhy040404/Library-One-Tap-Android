package com.qhy040404.libraryonetap.data.tools

import com.qhy040404.datetime.Datetime

data class Exam(
  val name: String,
  val time: String,
  val startTime: Datetime,
  val endTime: Datetime,
  val room: String
)
