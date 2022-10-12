package ru.rikmasters.gilty.presentation.ui.presentation.main.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.meeting.ListOfFilters
import ru.rikmasters.gilty.presentation.ui.shared.GradientButton
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

@Preview(backgroundColor = 0xFFE8E8E8, showBackground = true)
@Composable
fun MeetingFilterBottomComposePreview() {
    GiltyTheme {
        MeetingFilterBottomCompose(find = "найдено 26 встреч")
    }
}

@Composable
fun MeetingFilterBottomCompose(
    find: String = "",
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(ListOfFilters) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        it.name,
                        Modifier.padding(top = 28.dp, bottom = 18.dp),
                        ThemeExtra.colors.mainTextColor,
                        style = ThemeExtra.typography.H3
                    )
                    it.content.invoke()
                }
            }
            item {
                GradientButton(
                    {},
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 28.dp),
                    stringResource(R.string.save_button), smallText = find
                )
            }
            item {
                Text(
                    stringResource(R.string.meeting_filter_clear),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 28.dp)
                        .clickable {  },
                    ThemeExtra.colors.mainTextColor,
                    textAlign = TextAlign.Center,
                    style = ThemeExtra.typography.Body2Bold
                )
            }
        }
    }
}