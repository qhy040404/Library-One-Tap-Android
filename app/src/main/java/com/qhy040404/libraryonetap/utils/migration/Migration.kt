package com.qhy040404.libraryonetap.utils.migration

import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.Constants
import java.io.File

object Migration {
    fun checkAndMigrate() {
        MIGRATION_CHANGELOG.doMigration()
    }

    private val MIGRATION_CHANGELOG = object : BaseMigration("4.2.8") {
        override val reason = "Local changelog was saved as HTML string, new version uses " +
            "MarkdownParser to parse changelog. As a result, an Exception will be thrown because " +
            "of MarkdownParser cannot parse a HTML string."

        override fun migrate() {
            File(LibraryOneTapApp.app.dataDir, Constants.CHANGELOG_INACTIVE).delete()
            File(LibraryOneTapApp.app.dataDir, Constants.CHANGELOG_ACTIVE).delete()
        }
    }
}
