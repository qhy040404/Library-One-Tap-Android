package com.qhy040404.libraryonetap.constant.enums

enum class OrderModes {
  YANXIUJIAN,
  DETAIL;

  override fun toString(): String {
    return (values().indexOf(this) + 1).toString()
  }
}
