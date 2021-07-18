package de.schnettler.scrobbler.submission.map

import de.schnettler.scrobbler.core.map.Mapper
import de.schnettler.scrobbler.submission.model.ScrobbleResponse
import de.schnettler.scrobbler.submission.model.SubmissionFailureEntity
import javax.inject.Inject

class ScrobbleResponseToSubmissionFailureEntityMapper @Inject constructor() :
    Mapper<ScrobbleResponse, SubmissionFailureEntity> {
    override suspend fun map(from: ScrobbleResponse): SubmissionFailureEntity {
        return SubmissionFailureEntity(
            timestamp = from.timestamp,
            failureCode = from.ignoredMessage.code,
            failureReason = from.ignoredMessage.reason
        )
    }
}