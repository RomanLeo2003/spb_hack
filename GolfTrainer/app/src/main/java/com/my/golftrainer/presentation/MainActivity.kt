package com.my.golftrainer.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.my.golftrainer.presentation.screen.analysisresult.AnalysisResultScreen
import com.my.golftrainer.presentation.screen.camera.CameraScreen
import com.my.golftrainer.presentation.screen.history.HistoryScreen
import com.my.golftrainer.presentation.screen.home.HomeScreen
import com.my.golftrainer.presentation.screen.videoprocessing.VideoProcessingScreen
import com.my.golftrainer.presentation.screen.videospicker.VideosPickerScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
//            startCamera()
        } else {
            requestPermissions()
        }

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                GolfTrainerApp()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                startCamera()
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GolfTrainerApp() {

    val navController = rememberNavController()
    fun navigateToScreen(screen: String) {
        val route = screen
        val navOptions = NavOptions.Builder()
            .setPopUpTo(route, true, saveState = true)
            .setLaunchSingleTop(true)
            .build()

        navController.navigate(route, navOptions)
    }

    Scaffold(
        bottomBar = { BottomBar(navController, ::navigateToScreen) },
        containerColor = Color.White
    ) {

        GolfTrainerNavHost(
            navController = navController,
            modifier = Modifier.padding(it),
            navigateToScreen = ::navigateToScreen,
        )
    }
}

@Composable
fun GolfTrainerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigateToScreen: (String) -> Unit,
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.name,
        modifier = modifier
    ) {

        composable(Screen.Home.name) {
            HomeScreen(
                navigateToCamera = {
                    navigateToScreen(Screen.Camera.name)
                },
                navigateToVideosPicker = {
                    navigateToScreen(Screen.VideosPicker.name)
                }
            )
        }
        composable(Screen.Camera.name) {
            CameraScreen(
                navigateBack = { navigateToScreen(Screen.Home.name) },
                navigateToVideoProcessing = { uri ->
                    navigateToScreen(
                        Screen.VideoProcessing.name.addArgs(ARGUMENT_URI, uri)
                    )
                }
            )
        }

        composable(Screen.History.name) {
            HistoryScreen(
                navigateToVideoProcessing = { uri ->
                    navigateToScreen(
                        Screen.VideoProcessing.name.addArgs(ARGUMENT_URI, uri)
                    )
                }
            )
        }

        composable(Screen.VideosPicker.name) {
            VideosPickerScreen(
                navigateBack = { navController.popBackStack() },
                navigateToVideoProcessing = { uri ->
                    navigateToScreen(
                        Screen.VideoProcessing.name.addArgs(ARGUMENT_URI, uri)
                    )
                }
            )
        }

        composable(
            Screen.VideoProcessing.name.addPathArgs(ARGUMENT_URI),
            arguments = listOf(navArgument(ARGUMENT_URI) {
                type = NavType.StringType
            })
        ) {
            VideoProcessingScreen(
                navigateBack = {
                    navigateToScreen(Screen.Home.name)
                },
                navigateToAnalysisResult = { uri ->
                    navigateToScreen(
                        Screen.AnalysisResult.name.addArgs(ARGUMENT_URI, uri)
                    )
                }
            )
        }

        composable(
            Screen.AnalysisResult.name.addPathArgs(ARGUMENT_URI),
            arguments = listOf(navArgument(ARGUMENT_URI) {
                type = NavType.StringType
            })
        ) {
            AnalysisResultScreen(
                navigateBack = {
                    navigateToScreen(Screen.Home.name)
                }
            )
        }
    }

}


private const val TAG = "CameraXApp"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private val REQUIRED_PERMISSIONS =
    mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
//        .apply {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//    }
        .toTypedArray()

const val INPUT_WIDTH = 224
const val INPUT_HEIGHT = 224