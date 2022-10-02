package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rxjava3.subscribeAsState
import io.reactivex.rxjava3.core.Observable
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra


@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
private fun CodePreview() {

    GiltyTheme {
        CodeContent()
    }
}


@Composable
fun CodeContent() {

    Surface(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {

        Column(Modifier
            .fillMaxSize()) {

            Image(
                painterResource(id =  R.drawable.ic_back),
                contentDescription = "button back",
                Modifier
                    .padding(start = 16.dp, top = 32.dp)
                    .clickable { })

            Text(
                text = stringResource(id = R.string.confirm_number_title),
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                color = ThemeExtra.colors.mainTextColor )

            Text(
                text = stringResource(R.string.confirm_number_subtitle),
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.labelSmall,
                color = ThemeExtra.colors.secondaryTextColor)

            DigitCode(
                Modifier.padding(top = 32.dp)
                .padding(horizontal = 50.dp), 4, { })


        }
    }
}

@Composable
private fun ButtonTimer(
    modifier: Modifier = Modifier,
    timer: Observable<Int>,
    onResend: () -> Unit,
) {

    val sec by timer.subscribeAsState(180)

    Button(
        onResend,
        modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        enabled = sec <= 0,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        contentPadding = PaddingValues(vertical = 18.dp)
    ) {
        Text(
            (
                    if(sec > 0) stringResource(R.string.call_again, "${sec/60}:${sec%60}")
                    else stringResource(R.string.call_again)
                    ).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}