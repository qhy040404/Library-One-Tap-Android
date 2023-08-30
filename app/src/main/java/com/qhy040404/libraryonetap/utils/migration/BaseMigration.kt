package com.qhy040404.libraryonetap.utils.migration

import jonathanfinerty.once.Once

abstract class BaseMigration(private val commit: String) {
  protected abstract val reason: String
  protected abstract fun migrate()

  fun doMigration() {
    if (Once.beenDone(commit)) return
    migrate()
    Once.markDone(commit)
  }
}
