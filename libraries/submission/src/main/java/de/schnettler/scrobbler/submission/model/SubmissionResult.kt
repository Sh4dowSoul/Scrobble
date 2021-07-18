package de.schnettler.scrobbler.submission.model

sealed class SubmissionResult {
    data class Success(val accepted: List<ScrobbleResponse>, val ignored: List<ScrobbleResponse>) : SubmissionResult()
    data class Error(val message: String, val recoverable: Boolean = false) : SubmissionResult()
}