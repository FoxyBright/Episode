package ru.rikmasters.gilty.login.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.SplashViewModel
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.AnimatedImage

@Composable
fun SplashScreen(vm: SplashViewModel) {
    
    val screen by vm.screen.collectAsState()
    val nav = get<NavState>()
    
    LaunchedEffect(Unit) { vm.getScreen() }
    
    Box(
        Modifier.background(colorScheme.background)) {
        AnimatedImage(
            R.raw.splash,
            Modifier.fillMaxSize(),
            iterations = 1
        ) {
            LaunchedEffect(screen) {
                if(screen.isNotBlank())
                    nav.clearStackNavigation(screen)
            }
        }
    }
}