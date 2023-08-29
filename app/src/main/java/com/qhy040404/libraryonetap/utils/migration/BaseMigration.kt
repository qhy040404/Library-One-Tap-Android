package com.qhy040404.libraryonetap.utils.migration

import jonathanfinerty.once.Once

abstract class BaseMigration(private val versionName: String) {
  protected abstract val reason: String
  protected abstract fun migrate()

  fun doMigration() {
    if (Once.beenDone(versionName)) return
    migrate()
    Once.markDone(versionName)
  }
}
