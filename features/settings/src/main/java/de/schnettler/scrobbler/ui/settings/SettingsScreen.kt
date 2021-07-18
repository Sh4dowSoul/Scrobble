package de.schnettler.scrobbler.ui.settings

import android.content.Intent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsOverscan
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.schnettler.datastore.compose.model.Preference.PreferenceGroup
import de.schnettler.datastore.compose.model.Preference.PreferenceItem.ListPreference
import de.schnettler.datastore.compose.model.Preference.PreferenceItem.MultiSelectListPreference
import de.schnettler.datastore.compose.model.Preference.PreferenceItem.SeekBarPreference
import de.schnettler.datastore.compose.model.Preference.PreferenceItem.SwitchPreference
import de.schnettler.datastore.compose.model.Preference.PreferenceItem.TextPreference
import de.schnettler.datastore.compose.model.PreferenceIcon
import de.schnettler.datastore.compose.ui.PreferenceScreen
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.scrobbler.compose.model.MediaCardSize
import de.schnettler.scrobbler.persistence.PreferenceRequestStore
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.scrobbler.persistence.PreferenceRequestStore.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.scrobbler.settings.R
import de.schnettler.scrobbler.ui.settings.ktx.getMediaBrowserServices
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Suppress("LongMethod")
@Composable
fun SettingsScreen(
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mediaServices = remember { mutableStateMapOf<String, String>() }
    val constraints: Map<String, Int> = remember {
        mapOf(
            SCROBBLE_CONSTRAINTS_NETWORK to R.string.setting_list_scrobbleconstraints_network,
            SCROBBLE_CONSTRAINTS_BATTERY to R.string.setting_list_scrobbleconstraints_battery,
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(dataStoreManager) {
        launch { mediaServices.putAll(context.getMediaBrowserServices()) }
    }

    val submissionGroup = PreferenceGroup(
        title = stringResource(id = R.string.setting_group_submission),
        enabled = true,
        preferenceItems = listOf(
            SwitchPreference(
                PreferenceRequestStore.autoScrobble,
                title = stringResource(id = R.string.setting_switch_autoscrobble_title),
                summary = stringResource(id = R.string.setting_switch_autoscrobble_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.CloudUpload) },
            ),
            SwitchPreference(
                PreferenceRequestStore.submitNowPlaying,
                title = stringResource(id = R.string.setting_switch_submitnp_title),
                summary = stringResource(id = R.string.setting_switch_submitnp_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.MusicNote) },
            ),
        )
    )

    val scrobbleGroup = PreferenceGroup(
        title = stringResource(id = R.string.settings_group_scrobble),
        enabled = true,
        preferenceItems = listOf(
            MultiSelectListPreference(
                PreferenceRequestStore.scrobbleSources,
                title = stringResource(id = R.string.setting_list_scrobblesource_title),
                summary = stringResource(id = R.string.setting_list_scrobblesource_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.Speaker) },
                entries = mediaServices
            ),
            SeekBarPreference(
                PreferenceRequestStore.scrobblePoint,
                title = stringResource(id = R.string.setting_seek_scrobblepoint_title),
                summary = stringResource(id = R.string.setting_seek_scrobblepoint_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.Speed) },
                steps = 4,
                valueRange = 0.5F..1F,
                valueRepresentation = { "${(it * 100).roundToInt()} %" }
            ),
            MultiSelectListPreference(
                PreferenceRequestStore.scrobbleConstraints,
                title = stringResource(id = R.string.setting_list_scrobbleconstraints_title),
                summary = stringResource(id = R.string.setting_list_scrobbleconstraints_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.SettingsOverscan) },
                entries = constraints.mapValues { stringResource(id = it.value) },
            ),
        )
    )

    val uiGroup = PreferenceGroup(
        title = stringResource(id = R.string.setting_group_ui),
        enabled = true,
        preferenceItems = listOf(
            ListPreference(
                PreferenceRequestStore.mediaCardSize,
                title = stringResource(id = R.string.setting_mediacard_title),
                summary = stringResource(id = R.string.setting_mediacard_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.Palette) },
                entries = MediaCardSize.values().associate { it.name to stringResource(id = it.nameRes) },
            )
        )
    )

    val miscGroup = PreferenceGroup(
        title = stringResource(id = R.string.setting_group_misc),
        enabled = true,
        preferenceItems = listOf(
            TextPreference(
                title = stringResource(id = R.string.setting_notifications_title),
                summary = stringResource(id = R.string.setting_notifications_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.Notifications) },
                onClick = {
                    val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS")
                        .putExtra("app_package", context.packageName) // Android 5-7
                        .putExtra("app_uid", context.applicationInfo.uid)
                        .putExtra("android.provider.extra.APP_PACKAGE", context.packageName) // Android 8+
                    context.startActivity(intent, null)
                }
            ),
            TextPreference(
                title = stringResource(id = R.string.setting_reset_title),
                summary = stringResource(id = R.string.setting_reset_description),
                singleLineTitle = true,
                icon = { PreferenceIcon(icon = Icons.Outlined.DeleteForever) },
                onClick = {
                    scope.launch {
                        dataStoreManager.clearPreferences()
                    }
                }

            ),
        )
    )

    PreferenceScreen(
        modifier = modifier,
        statusBarPadding = true,
        dataStoreManager = dataStoreManager,
        items = listOf(
            submissionGroup,
            scrobbleGroup,
            uiGroup,
            miscGroup
        )
    )
}