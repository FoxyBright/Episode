package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.FilterModel

@Composable
fun Element(
    it: FilterModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            buildAnnotatedString {
                append("${it.name} ")
                withStyle(
                    typography.labelSmall.copy(
                        colorScheme.onTertiary
                    ).toSpanStyle()
                ) { append(it.details ?: "") }
            }, Modifier.padding(bottom = 16.dp),
            style = typography.labelLarge.copy(
                colorScheme.tertiary
            )
        )
        it.content.invoke()
    }
}