package de.schnettler.scrobbler.submission.model

data class SubmissionResults(
    val accepted: List<ScrobbleResponse>,
    val ignored: List<ScrobbleResponse>,
    val errors: List<SubmissionResult.Error>,
)