package com.qhy040404.libraryonetap.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object FileUtils {
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        if (!dir.exists()) return true
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    fun getFileSize(file: File): Long {
        return getFileSize(file.path)
    }

    private fun getFileSize(path: String): Long {
        return runCatching {
            Files.size(Paths.get(path))
        }.getOrDefault(0L)
    }
}