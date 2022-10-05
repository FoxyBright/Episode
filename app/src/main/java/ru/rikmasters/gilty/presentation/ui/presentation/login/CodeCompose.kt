package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.presentation.navigation.NavigationInterface
import ru.rikmasters.gilty.presentation.ui.shared.ActionBar
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme

@Preview
@Composable
private fun CodePreview() {
    GiltyTheme {
        CodeContent()
    }
}

@Composable
fun CodeContent(callback: NavigationInterface? = null) {
    val enteredCode by remember { mutableStateOf("") }
    Surface(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ActionBar(
                stringResource(R.string.confirm_number_title),
                stringResource(R.string.confirm_number_subtitle)
            ) { callback?.onBack() }
            DigitCode(
                Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 50.dp), 4, enteredCode
            ) { if (it.length == 4) callback?.onNext() }
        }
    }
}

@Composable
private fun ButtonTimer(
    modifier: Modifier = Modifier,
    //timer: Observable<Int>,
    onResend: () -> Unit,
) {
    // TODO таймер на повторную отправку кода
    val sec = 0 //by timer.subscribeAsState(180)
    Button(
        onResend,
        modifier
            .fillMaxWidth(),
        sec <= 0,
        MaterialTheme.shapes.large,
        ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(vertical = 18.dp)
    ) {
        Text(
            (if (sec > 0)
                stringResource(R.string.call_again, "${sec / 60}:${sec % 60}")
            else
                stringResource(R.string.call_again)
                    ).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}