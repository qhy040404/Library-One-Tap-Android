package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

@Suppress("unused") // Will remove when 3.6.0
object UpdateUtils {
    fun checkUpdate() {
    }

    fun installApk(ctx: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(
            FileProvider.getUriForFile(ctx,
                "com.qhy040404.libraryonetap.fileprovider",
                File(ctx.cacheDir, "test.apk")),
            "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(intent)
    }
}
