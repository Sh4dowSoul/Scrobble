package de.schnettler.scrobbler.core.ktx

import androidx.core.text.HtmlCompat
import java.util.*

const val PACKAGE_YT_MUSIC = "com.google.android.apps.youtube.music"
const val GENERIC_FLAG_EMOJI = "U+1F38C"

/**
 * Tries to generate a flag emoji based on a country name.
 * The provided String has to be an ISO Country name.
 * If no valid country name is supplied, a generic flag emoji will be returned.
 *
 * Based on
 * https://stackoverflow.com/a/38588988/12743428
 * https://stackoverflow.com/a/50963795/12743428
 *
 */
fun String.toFlagEmoji(): String {
    // 1. Get Country Code
    val countryCode = Locale.getISOCountries().find {
        Locale("", it).getDisplayCountry(Locale.ENGLISH) == this.capitalizeAll()
    }?.uppercase() ?: return GENERIC_FLAG_EMOJI

    // 2. Generate Emoji
    return String(Character.toChars(Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6)) +
            String(Character.toChars(Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6))
}

fun String.capitalizeAll(): String = split(" ").joinToString(" ") { word: String ->
    word.replaceFirstChar { it.uppercase() }
}

fun String.firstLetter() = this.first { it.isLetter() }.toString()

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
fun String.fromHtmlLastFm() = this.fromHtml().removeSuffix("Read more on Last.fm.")

fun packageNameToAppName(packageName: String) = when (packageName) {
    PACKAGE_YT_MUSIC -> "YouTube Music"
    else -> packageName
}