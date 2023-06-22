package ru.rikmasters.gilty.gallery

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.net.Uri.fromParts
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton

@OptIn(ExperimentalPermissionsApi::class)
val permissionState: @Composable () -> PermissionState = {
    rememberPermissionState(
        if(SDK_INT < TIRAMISU)
            READ_EXTERNAL_STORAGE
        else READ_MEDIA_IMAGES
    )
}

@OptIn(ExperimentalPermissionsApi::class)
fun Context.checkStoragePermission(
    permissions: PermissionState,
    scope: CoroutineScope,
    asm: AppStateModel,
    onAllowed: () -> Unit,
) {
    when {
        permissions.hasPermission -> onAllowed()
        permissions.shouldShowRationale ->
            permissions.launchPermissionRequest()
        else -> scope.launch {
            asm.bottomSheet.expand {
                LaunchedEffect(permissions.hasPermission) {
                    if(permissions.hasPermission) {
                        asm.bottomSheet.collapse()
                        onAllowed()
                    }
                }
                
                val launcher =
                    rememberLauncherForActivityResult(
                        StartActivityForResult(),
                    ) {
                        if(permissions.hasPermission)
                            onAllowed()
                    }
                
                StoragePermissionBs {
                    launcher.launch(
                        Intent(
                            ACTION_APPLICATION_DETAILS_SETTINGS,
                            fromParts(("package"), packageName, (null))
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StoragePermissionBs(
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
) {
    Column(
        modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(16.dp)
            .padding(top = 12.dp),
        SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.profile_gallery_title),
            style = typography.labelLarge
        )
        Column(
            Modifier.fillMaxWidth(),
            Top, CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.ic_image_box
                ),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = colorScheme.primary
            )
            Text(
                text = stringResource(
                    R.string.permissions_settings_label
                ),
                modifier = Modifier.padding(
                    top = 18.dp,
                    bottom = 45.dp
                ),
                style = typography.labelLarge.copy(
                    textAlign = Center,
                    fontWeight = Bold
                )
            )
        }
        GradientButton(
            modifier = Modifier
                .padding(bottom = 31.dp),
            text = stringResource(
                R.string.permissions_settings_button
            )
        ) { onSettingsClick?.let { it() } }
    }
}