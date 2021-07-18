package de.schnettler.scrobbler.submission.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "submission_failure")
data class SubmissionFailureEntity(
    @PrimaryKey val timestamp: Long,
    val failureCode: Long,
    val failureReason: String,
)