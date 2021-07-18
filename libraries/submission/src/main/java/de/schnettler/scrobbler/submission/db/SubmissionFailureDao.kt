package de.schnettler.scrobbler.submission.db

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.persistence.dao.BaseDao
import de.schnettler.scrobbler.submission.model.SubmissionFailureEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubmissionFailureDao : BaseDao<SubmissionFailureEntity> {

    @Query("SELECT COUNT(timestamp) FROM submission_failure")
    abstract fun getNumberOfIgnoredScrobbles(): Flow<Int>

    @Query("DELETE FROM submission_failure WHERE timestamp in (:timestamps)")
    abstract fun deleteEntries(timestamps: List<Long>)
}