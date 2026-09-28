package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FirebaseAuthManager
import com.example.data.FirestoreSyncManager
import com.example.data.MemoryRepository
import com.example.data.VeoService
import com.example.model.MemoryEntry
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    WORLD,
    ARCHIVE,
    MILESTONES,
    VEO_ANIMATE
}

data class OdunKanUiState(
    val currentDay: Int = 1,
    val saturation: Float = 0.15f,
    val isTimelapsePlaying: Boolean = false,
    val bloomTrigger: Boolean = false,
    val activeScreen: AppScreen = AppScreen.WORLD,
    val isLogSheetOpen: Boolean = false,
    val pendingPromptText: String = "",
    val inspectedElementTitle: String? = null,
    val currentUser: FirebaseUser? = null,
    val isSyncingToCloud: Boolean = false,
    val isGeneratingVideo: Boolean = false,
    val videoGenerationProgress: Float = 0f,
    val videoGenerationStatus: String = "",
    val lastGeneratedVideoUri: String? = null
)

class OdunKanViewModel(
    application: Application,
    private val repository: MemoryRepository = try {
        org.koin.core.context.GlobalContext.get().get<MemoryRepository>()
    } catch (e: Throwable) {
        MemoryRepository(AppDatabase.getInstance(application).memoryDao())
    }
) : AndroidViewModel(application) {
    val authManager = FirebaseAuthManager()
    private val firestoreSync = FirestoreSyncManager(repository)
    private val veoService = VeoService()

    val allMemories: StateFlow<List<MemoryEntry>> = repository.allMemories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(OdunKanUiState())
    val uiState: StateFlow<OdunKanUiState> = _uiState.asStateFlow()

    private var timelapseJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }

        // Listen for auth state changes
        viewModelScope.launch {
            authManager.authStateFlow().collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
                if (user != null) {
                    firestoreSync.startListeningForUserMemories(user.uid)
                } else {
                    firestoreSync.stopListening()
                }
            }
        }

        // Keep saturation updated according to currentDay + memory count
        viewModelScope.launch {
            allMemories.collect { memories ->
                updateCalculatedSaturation(_uiState.value.currentDay, memories.size)
            }
        }
    }

    private fun updateCalculatedSaturation(day: Int, memoryCount: Int) {
        val dayFactor = (day.toFloat() / 365f) * 0.70f
        val memoryFactor = (memoryCount * 0.035f).coerceAtMost(0.35f)
        val totalSat = (0.05f + dayFactor + memoryFactor).coerceIn(0.05f, 1.0f)

        _uiState.value = _uiState.value.copy(
            saturation = totalSat
        )
    }

    fun setDay(day: Int) {
        val clamped = day.coerceIn(1, 365)
        _uiState.value = _uiState.value.copy(currentDay = clamped)
        updateCalculatedSaturation(clamped, allMemories.value.size)
    }

    fun setScreen(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(activeScreen = screen)
    }

    fun openLogMemorySheet(promptText: String = "") {
        _uiState.value = _uiState.value.copy(
            isLogSheetOpen = true,
            pendingPromptText = promptText
        )
    }

    fun closeLogMemorySheet() {
        _uiState.value = _uiState.value.copy(
            isLogSheetOpen = false,
            pendingPromptText = ""
        )
    }

    fun triggerBloom() {
        _uiState.value = _uiState.value.copy(bloomTrigger = true)
    }

    fun onBloomFinished() {
        _uiState.value = _uiState.value.copy(bloomTrigger = false)
    }

    fun setInspectedElement(title: String?) {
        _uiState.value = _uiState.value.copy(inspectedElementTitle = title)
    }

    fun toggleTimelapse() {
        val willPlay = !_uiState.value.isTimelapsePlaying
        _uiState.value = _uiState.value.copy(isTimelapsePlaying = willPlay)

        if (willPlay) {
            timelapseJob?.cancel()
            timelapseJob = viewModelScope.launch {
                var nextDay = _uiState.value.currentDay
                if (nextDay >= 365) nextDay = 1

                while (_uiState.value.isTimelapsePlaying) {
                    setDay(nextDay)
                    delay(120)
                    nextDay++
                    if (nextDay > 365) {
                        nextDay = 1
                    }
                }
            }
        } else {
            timelapseJob?.cancel()
            timelapseJob = null
        }
    }

    fun saveMemory(entry: MemoryEntry) {
        viewModelScope.launch {
            repository.saveMemory(entry)
            setDay(entry.dayNumber)
            closeLogMemorySheet()
            triggerBloom()

            // Cloud sync to Firestore if user is authenticated
            _uiState.value.currentUser?.let { user ->
                _uiState.value = _uiState.value.copy(isSyncingToCloud = true)
                firestoreSync.uploadMemoryToCloud(user.uid, entry)
                _uiState.value = _uiState.value.copy(isSyncingToCloud = false)
            }
        }
    }

    fun deleteMemory(entry: MemoryEntry) {
        viewModelScope.launch {
            repository.deleteMemory(entry)
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            authManager.signInAnonymously()
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun generateVeoVideo(context: Context, photoUri: Uri?, prompt: String, aspectRatio: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingVideo = true,
                videoGenerationProgress = 0.1f,
                videoGenerationStatus = "Initializing Veo video generation..."
            )

            val result = veoService.generateVideoFromPhotoOrPrompt(
                context = context,
                prompt = prompt,
                photoUri = photoUri,
                aspectRatio = aspectRatio,
                onProgress = { status, progress ->
                    _uiState.value = _uiState.value.copy(
                        videoGenerationStatus = status,
                        videoGenerationProgress = progress
                    )
                }
            )

            result.onSuccess { uri ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingVideo = false,
                    lastGeneratedVideoUri = uri,
                    videoGenerationStatus = "Video generated successfully!"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingVideo = false,
                    videoGenerationStatus = "Generation failed: ${err.message}"
                )
            }
        }
    }
}
