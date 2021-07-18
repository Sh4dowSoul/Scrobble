package de.schnettler.scrobbler.history.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.schnettler.scrobbler.core.ui.viewmodel.RefreshableStateViewModel
import de.schnettler.scrobbler.history.domain.LocalRepository
import de.schnettler.scrobbler.history.domain.RejectionCodeToReasonMapper
import de.schnettler.scrobbler.history.model.Event
import de.schnettler.scrobbler.history.model.SubmissionEvent
import de.schnettler.scrobbler.history.model.SubmissionResult
import de.schnettler.scrobbler.model.Scrobble
import de.schnettler.scrobbler.submission.domain.SubmissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val repo: LocalRepository,
    private val submissionRepo: SubmissionRepository,
    private val rejectionCodeToReasonMapper: RejectionCodeToReasonMapper
) : RefreshableStateViewModel<String, List<Scrobble>, List<Scrobble>>(store = repo.recentTracksStore, "") {

    val isSubmitting = MutableStateFlow(false)

    val events = MutableLiveData<Event<SubmissionEvent>>()

    val cachedScrobblesCount by lazy {
        repo.getNumberOfCachedScrobbles()
    }

    val ignoredScrobblesCount by lazy {
        repo.getNumberOfIgnoredScrobbles()
    }

    fun scheduleScrobbleSubmission() {
        isSubmitting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val results = submissionRepo.submitCachedScrobbles()

            val errorMessage = results.errors.firstOrNull()?.message
            val mappedResult = SubmissionResult(
                accepted = results.accepted.map { it.timestamp },
                ignored = results.ignored.associateBy({ it.timestamp }, { it.ignoredMessage.code }),
                error = errorMessage
            )
            events.postValue(Event(SubmissionEvent.Success(mappedResult)))
            isSubmitting.value = false
        }
    }

    fun showDetails(submissionResult: SubmissionResult) {
        viewModelScope.launch {
            val accepted = async { repo.getScrobblesById(submissionResult.accepted) }
            val ignored = async { repo.getScrobblesById(submissionResult.ignored.keys.toList()) }
            val ignoredWithReason = ignored.await().associateWith { scrobble ->
                rejectionCodeToReasonMapper.map(
                    submissionResult.ignored.getOrElse(scrobble.timestamp) { 0L }
                )
            }
            events.postValue(
                Event(
                    SubmissionEvent.ShowDetails(
                        accepted.await(),
                        ignoredWithReason,
                        submissionResult.error
                    )
                )
            )
        }
    }

    fun submitScrobble(track: Scrobble) {
        viewModelScope.launch {
            submissionRepo.submitScrobble(track)
        }
    }

    fun deleteScrobble(scrobble: Scrobble) {
        viewModelScope.launch {
            submissionRepo.deleteScrobble(scrobble)
        }
    }

    fun editCachedScrobble(scrobble: Scrobble) {
        viewModelScope.launch {
            submissionRepo.submitScrobbleEdit(scrobble)
        }
    }
}