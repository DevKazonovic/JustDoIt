package com.devkazonovic.projects.justdoit.help.util

import androidx.core.text.HtmlCompat
import timber.log.Timber

fun htmlToString(html: String): String {
    return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}

fun booleanToInt(boolean: Boolean): Int {
    return if (boolean) 1 else 0
}


fun log(s: String) {
    Timber.d(s)
}