package de.schnettler.scrobbler.ui.settings.ktx

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.getMediaBrowserServices(): Map<String, String> {
    return withContext(Dispatchers.Default) {
        packageManager.queryIntentServices(
            Intent("android.media.browse.MediaBrowserService"),
            PackageManager.GET_RESOLVED_FILTER
        ).mapNotNull { it.serviceInfo }.associateBy({ it.packageName }, {
            it.loadLabel(packageManager).toString()
        })
    }
}