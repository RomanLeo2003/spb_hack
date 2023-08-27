package com.my.golftrainer.presentation.screen.analysisresult

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.golftrainer.presentation.ARGUMENT_URI
import com.my.golftrainer.presentation.utils.log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalysisResultViewModel(
    savedStateHandle: SavedStateHandle,
    ) : ViewModel() {

    val uri = Uri.parse(savedStateHandle.get<String>(ARGUMENT_URI))

    val swingStates = listOf(
        700 to SwingState.Address,
        1100 to SwingState.ToeUp,
        1233 to SwingState.MidBackswing,
        1533 to SwingState.Top,
        1667 to SwingState.MidDownswing,
        1800 to SwingState.Impact,
        1867 to SwingState.MidFollowThrough,
        2233 to SwingState.Finish,
        1000000 to SwingState.Finish,// just for windowed last item
    )

    init {
        log(swingStates.windowed(2).toString())
    }


    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    fun onEvent(event: Event) {
        when (event) {
            Event.PauseTapped -> onPauseTapped()
            Event.PlayTapped -> onPlayTapped()

            is Event.OnProgress -> onProgress(event.progress)
            Event.Prepared -> onPrepared()
            Event.Completed -> onCompleted()
        }
    }

    private fun onPauseTapped() {
        viewModelScope.launch {
            _effect.emit(Effect.Pause)
        }
        _state.update { it.copy(playbackStatus = PlaybackStatus.Idle) }
    }

    private fun onPlayTapped() {
        viewModelScope.launch {
            _effect.emit(Effect.Play)
        }
    }

    fun onProgress(progress: Int) {
        log("progress $progress")
        _state.update { it.copy(playbackPosition = progress, playbackStatus = PlaybackStatus.InProgress) }
    }

    fun updateProgress(progress: Int) {
        _state.update { it.copy(playbackPosition = progress) }
    }

    private fun onPrepared() {
        _state.update { it.copy(playbackStatus = PlaybackStatus.Idle) }
    }

    private fun onCompleted() {
        _state.update { it.copy(playbackStatus = PlaybackStatus.Idle, playbackPosition = 0) }
    }

    data class State(
        val filePath: String? = null,
        val playbackStatus: PlaybackStatus? = PlaybackStatus.Idle,
        val playbackPosition: Int = 0
    )

    sealed class Effect {
        object NavigateUp : Effect()
        object Pause : Effect()
        object Play : Effect()
    }

    sealed class Event {
        object PlayTapped : Event()
        object PauseTapped : Event()

        object Prepared : Event()
        object Completed : Event()
        data class OnProgress(val progress: Int) : Event()
    }

    sealed class PlaybackStatus {
        object Idle : PlaybackStatus()
        object InProgress : PlaybackStatus()
    }


    enum class SwingState(val title: String) {
        Address("Address"),
        ToeUp("Toe-up"),
        MidBackswing("Mid-backswing"),
        Top("Top"),
        MidDownswing("Mid-downswing"),
        Impact("Impact"),
        MidFollowThrough("Mid-follow-through"),
        Finish("Finish"),
    }
}
