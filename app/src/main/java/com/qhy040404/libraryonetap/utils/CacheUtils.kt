package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.LibraryOneTapApp
import java.io.File
import java.text.DecimalFormat

object CacheUtils {
  private val externalCache = LibraryOneTapApp.app.externalCacheDir
  private val cache = LibraryOneTapApp.app.cacheDir
  private val codeCache = LibraryOneTapApp.app.codeCacheDir
  private val df = DecimalFormat("#.00")

  fun getCacheSize() = formatFileSize(
    FileUtils.getFolderSize(externalCache!!) +
      FileUtils.getFolderSize(cache) +
      FileUtils.getFolderSize(codeCache)
  )

  private fun formatFileSize(fileSize: Long) = if (fileSize == 0L) {
    "0.00 K"
  } else if (fileSize < 1024) {
    df.format(fileSize.toDouble()) + " B"
  } else if (fileSize < 1024 * 1024) {
    df.format(fileSize.toDouble() / 1024) + " K"
  } else if (fileSize < 1024 * 1024 * 1024) {
    df.format(fileSize.toDouble() / (1024 * 1024)) + " M"
  } else {
    df.format(fileSize.toDouble() / (1024 * 1024 * 1024)) + " G"
  }

  fun trimCaches() {
    trimCache(cache)
    trimCache(codeCache)
    trimCache(externalCache)
  }

  private fun trimCache(file: File?) = runCatching {
    if (file != null && file.isDirectory && file.listFiles()!!.isNotEmpty()) {
      FileUtils.delete(file)
    }
  }
}
