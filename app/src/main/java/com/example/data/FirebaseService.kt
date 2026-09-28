package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.model.MemoryEntry
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = try {
            auth.currentUser
        } catch (e: Exception) {
            null
        }

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        try {
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(firebaseAuth.currentUser)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        } catch (e: Exception) {
            trySend(null)
            awaitClose { }
        }
    }

    suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: throw IllegalStateException("User was null after sign in")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Anonymous sign in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredentialManager(context: Context, webClientId: String): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(context, request)
            val credential = response.credential

            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw IllegalStateException("Auth result user null")
                Result.success(user)
            } else {
                Result.failure(IllegalArgumentException("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialException) {
            Log.e("FirebaseAuthManager", "Credential manager failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Google sign in failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign out error", e)
        }
    }
}

class FirestoreSyncManager(private val repository: MemoryRepository) {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val syncScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob())
    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningForUserMemories(userId: String) {
        listenerRegistration?.remove()
        try {
            listenerRegistration = firestore.collection("users")
                .document(userId)
                .collection("memories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    for (doc in snapshot.documents) {
                        try {
                            val day = (doc.getLong("dayNumber") ?: 1L).toInt()
                            val date = doc.getLong("date") ?: System.currentTimeMillis()
                            val content = doc.getString("content") ?: ""
                            val growthPhase = doc.getString("growth_phase") ?: MemoryEntry.growthPhaseForDay(day)
                            val title = doc.getString("title") ?: "Day $day"
                            val moodTag = doc.getString("moodTag") ?: "Bloom"
                            val palette = doc.getString("paletteColorHex") ?: "#52B788"
                            val tags = doc.getString("tags") ?: ""
                            val prompt = doc.getString("promptQuestion") ?: ""
                            val imageUrl = doc.getString("imageUrl")
                            val videoUrl = doc.getString("videoUrl")

                            val entry = MemoryEntry(
                                dayNumber = day,
                                date = date,
                                content = content,
                                growth_phase = growthPhase,
                                title = title,
                                moodTag = moodTag,
                                paletteColorHex = palette,
                                tags = tags,
                                promptQuestion = prompt,
                                imageUrl = imageUrl,
                                videoUrl = videoUrl
                            )
                            syncScope.launch {
                                repository.saveMemory(entry)
                            }
                        } catch (e: Exception) {
                            Log.e("FirestoreSyncManager", "Error parsing remote entry", e)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Failed to start listener", e)
        }
    }

    suspend fun uploadMemoryToCloud(userId: String, memory: MemoryEntry) {
        try {
            val docData = hashMapOf(
                "dayNumber" to memory.dayNumber,
                "date" to memory.date,
                "content" to memory.content,
                "growth_phase" to memory.growth_phase,
                "title" to memory.title,
                "moodTag" to memory.moodTag,
                "paletteColorHex" to memory.paletteColorHex,
                "tags" to memory.tags,
                "promptQuestion" to memory.promptQuestion,
                "imageUrl" to memory.imageUrl,
                "videoUrl" to memory.videoUrl,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("memories")
                .document("day_${memory.dayNumber}")
                .set(docData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Upload to Firestore failed", e)
        }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}
