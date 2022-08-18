package com.qhy040404.libraryonetap.utils

import com.qhy040404.libraryonetap.LibraryOneTapApp
import java.text.DecimalFormat

object CacheUtils {
    private val externalCache = LibraryOneTapApp.app.externalCacheDir
    private val cache = LibraryOneTapApp.app.cacheDir
    private val codeCache = LibraryOneTapApp.app.codeCacheDir
    private val df = DecimalFormat("#.00")

    fun getCacheSize() = formatFileSize(
        FileUtils.getFileSize(externalCache!!) +
                FileUtils.getFileSize(cache) +
                FileUtils.getFileSize(codeCache)
    )

    private fun formatFileSize(fileSize: Long) = if (fileSize == 0L) {
        "0.00 K"
    } else if (fileSize < 1024) {
        df.format(fileSize.toDouble()) + " B"
    } else if (fileSize < 1024 * 1024) {
        df.format(fileSize.toDouble() / 1024) + " K"
    } else if (fileSize < 1024 * 1024 * 1024) {
        df.format(fileSize.toDouble() / 1024 * 1024) + " M"
    } else {
        df.format(fileSize.toDouble() / 1024 * 1024 * 1024) + " G"
    }

    fun trimCaches() {
        trimCache()
        trimCodeCache()
        trimExternalCache()
    }

    private fun trimCache() = try {
        if (cache != null && cache.isDirectory && cache.listFiles()!!.isNotEmpty()) {
            FileUtils.delete(cache)
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }

    private fun trimCodeCache() = try {
        if (codeCache != null && codeCache.isDirectory && codeCache.listFiles()!!
                .isNotEmpty()
        ) {
            FileUtils.delete(codeCache)
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }

    private fun trimExternalCache() = try {
        if (externalCache != null && externalCache.isDirectory && externalCache.listFiles()!!
                .isNotEmpty()
        ) {
            FileUtils.delete(externalCache)
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }
}
