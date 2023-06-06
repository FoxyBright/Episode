package ru.rikmasters.gilty.translation.shared.util

import android.Manifest
import android.content.Context
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
val mediaPermissionState: @Composable () -> MultiplePermissionsState = {
    rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )
}

@Suppress("unused")
@OptIn(ExperimentalPermissionsApi::class)
fun Context.checkMediaPermissions(
    permission: MultiplePermissionsState,
    onAllowed: () -> Unit
) {
    when {
        permission.allPermissionsGranted -> onAllowed()
        permission.shouldShowRationale -> permission.launchMultiplePermissionRequest()
        else -> {
            // TODO: Макет
//            errorToast(this,
//                "Предоставьте разрешение на использование камеры и микрофона (тестовый тост)")
        }
    }
}

