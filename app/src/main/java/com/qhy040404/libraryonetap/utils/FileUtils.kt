package com.qhy040404.libraryonetap.utils

import java.io.File

object FileUtils {
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else {
            deleteFile(file)
        }
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

    fun getFolderSize(file: File): Long {
        var size = 0L
        runCatching {
            val fileList = file.listFiles()
            if (fileList != null) {
                for (i in fileList.indices) {
                    size += if (fileList[i].isDirectory) {
                        getFolderSize(fileList[i])
                    } else {
                        fileList[i].length()
                    }
                }
            }
        }
        return size
    }
}
