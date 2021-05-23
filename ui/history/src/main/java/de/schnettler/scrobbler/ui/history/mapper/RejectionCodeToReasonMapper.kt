package de.schnettler.scrobbler.ui.history.mapper

import androidx.annotation.StringRes
import de.schnettler.repo.mapping.Mapper
import de.schnettler.scrobbler.ui.history.R
import javax.inject.Inject

class RejectionCodeToReasonMapper @Inject constructor() : Mapper<Long, @StringRes Int> {
    override suspend fun map(from: Long) = when (from) {
        1L -> R.string.rejectionArtist
        2L -> R.string.rejectionTrack
        3L -> R.string.rejectionTimestampOld
        4L -> R.string.rejectionTimestampNew
        5L -> R.string.rejectionLimit
        else -> R.string.rejectionUnknown
    }
}