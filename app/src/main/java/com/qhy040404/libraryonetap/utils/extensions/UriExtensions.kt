package com.qhy040404.libraryonetap.utils.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Try to start Uri with CustomTabsIntent, if failed, start it with traditional Intent
 *
 * @param context Context
 */
fun Uri.start(
    context: Context,
    onFailure: () -> Unit = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = this@start
        }
        runCatching {
            context.startActivity(intent)
        }
    },
) {
    runCatching {
        CustomTabsIntent.Builder().build()
            .launchUrl(context, this)
    }.onFailure {
        onFailure()
    }
}

