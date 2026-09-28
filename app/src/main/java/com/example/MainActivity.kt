package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LogMemorySheet
import com.example.ui.screens.JournalArchiveScreen
import com.example.ui.screens.VeoAnimateScreen
import com.example.ui.screens.WorldScreen
import com.example.ui.screens.YearMilestonesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.OdunKanViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: OdunKanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                OdunKanApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OdunKanApp(viewModel: OdunKanViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val memories by viewModel.allMemories.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // Handle back button on sub-screens
    if (uiState.activeScreen != AppScreen.WORLD) {
        BackHandler {
            viewModel.setScreen(AppScreen.WORLD)
        }
    }

    val navBarColor = if (isDark) Color(0xFF14171C) else Color(0xFFFAF7F0)
    val activePillColor = if (isDark) Color(0xFF233227) else Color(0xFFE2EFE7)
    val activeIconColor = Color(0xFF52B788)
    val inactiveIconColor = if (isDark) Color(0xFF7E8798) else Color(0xFF7D776C)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = navBarColor,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = uiState.activeScreen == AppScreen.WORLD,
                    onClick = { viewModel.setScreen(AppScreen.WORLD) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeScreen == AppScreen.WORLD) Icons.Filled.Layers else Icons.Outlined.Layers,
                            contentDescription = "Sketchbook World"
                        )
                    },
                    label = {
                        Text(
                            text = "World",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = activePillColor,
                        selectedIconColor = activeIconColor,
                        selectedTextColor = activeIconColor,
                        unselectedIconColor = inactiveIconColor,
                        unselectedTextColor = inactiveIconColor
                    ),
                    modifier = Modifier.testTag("nav_tab_world")
                )

                NavigationBarItem(
                    selected = uiState.activeScreen == AppScreen.ARCHIVE,
                    onClick = { viewModel.setScreen(AppScreen.ARCHIVE) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeScreen == AppScreen.ARCHIVE) Icons.Filled.Book else Icons.Outlined.Book,
                            contentDescription = "Journal Archive"
                        )
                    },
                    label = {
                        Text(
                            text = "Folio (${memories.size})",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = activePillColor,
                        selectedIconColor = activeIconColor,
                        selectedTextColor = activeIconColor,
                        unselectedIconColor = inactiveIconColor,
                        unselectedTextColor = inactiveIconColor
                    ),
                    modifier = Modifier.testTag("nav_tab_archive")
                )

                NavigationBarItem(
                    selected = uiState.activeScreen == AppScreen.MILESTONES,
                    onClick = { viewModel.setScreen(AppScreen.MILESTONES) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeScreen == AppScreen.MILESTONES) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Milestones"
                        )
                    },
                    label = {
                        Text(
                            text = "Stages",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = activePillColor,
                        selectedIconColor = activeIconColor,
                        selectedTextColor = activeIconColor,
                        unselectedIconColor = inactiveIconColor,
                        unselectedTextColor = inactiveIconColor
                    ),
                    modifier = Modifier.testTag("nav_tab_milestones")
                )

                NavigationBarItem(
                    selected = uiState.activeScreen == AppScreen.VEO_ANIMATE,
                    onClick = { viewModel.setScreen(AppScreen.VEO_ANIMATE) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.activeScreen == AppScreen.VEO_ANIMATE) Icons.Filled.Movie else Icons.Outlined.Movie,
                            contentDescription = "Veo Video"
                        )
                    },
                    label = {
                        Text(
                            text = "Veo",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = activePillColor,
                        selectedIconColor = activeIconColor,
                        selectedTextColor = activeIconColor,
                        unselectedIconColor = inactiveIconColor,
                        unselectedTextColor = inactiveIconColor
                    ),
                    modifier = Modifier.testTag("nav_tab_veo")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (uiState.activeScreen) {
                AppScreen.WORLD -> {
                    WorldScreen(
                        currentDay = uiState.currentDay,
                        saturation = uiState.saturation,
                        isTimelapsePlaying = uiState.isTimelapsePlaying,
                        bloomTrigger = uiState.bloomTrigger,
                        inspectedElementTitle = uiState.inspectedElementTitle,
                        onDayChange = { viewModel.setDay(it) },
                        onToggleTimelapse = { viewModel.toggleTimelapse() },
                        onTriggerBloom = { viewModel.triggerBloom() },
                        onBloomFinished = { viewModel.onBloomFinished() },
                        onElementSelected = { viewModel.setInspectedElement(it) },
                        onDismissElement = { viewModel.setInspectedElement(null) },
                        onOpenLogMemory = { prompt -> viewModel.openLogMemorySheet(prompt) }
                    )
                }

                AppScreen.ARCHIVE -> {
                    JournalArchiveScreen(
                        memories = memories,
                        onSelectDay = { day ->
                            viewModel.setDay(day)
                            viewModel.setScreen(AppScreen.WORLD)
                        },
                        onDeleteMemory = { memory ->
                            viewModel.deleteMemory(memory)
                        }
                    )
                }

                AppScreen.MILESTONES -> {
                    YearMilestonesScreen(
                        currentDay = uiState.currentDay,
                        totalMemories = memories.size,
                        saturation = uiState.saturation,
                        onSelectMilestoneDay = { day ->
                            viewModel.setDay(day)
                            viewModel.setScreen(AppScreen.WORLD)
                        }
                    )
                }

                AppScreen.VEO_ANIMATE -> {
                    VeoAnimateScreen(
                        currentDay = uiState.currentDay,
                        currentUser = uiState.currentUser,
                        onSignInAnonymously = { viewModel.signInAnonymously() },
                        onSignOut = { viewModel.signOut() },
                        onGenerateVeoVideo = { uri, prompt, aspect ->
                            viewModel.generateVeoVideo(context, uri, prompt, aspect)
                        },
                        isGeneratingVideo = uiState.isGeneratingVideo,
                        videoGenerationProgress = uiState.videoGenerationProgress,
                        videoGenerationStatus = uiState.videoGenerationStatus,
                        lastGeneratedVideoUri = uiState.lastGeneratedVideoUri
                    )
                }
            }
        }

        // Modal Bottom Sheet for logging memory
        if (uiState.isLogSheetOpen) {
            LogMemorySheet(
                initialDay = uiState.currentDay,
                initialPrompt = uiState.pendingPromptText,
                onDismiss = { viewModel.closeLogMemorySheet() },
                onSaveMemory = { entry ->
                    viewModel.saveMemory(entry)
                }
            )
        }
    }
}
