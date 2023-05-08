package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.model.meeting.FilterModel
import ru.rikmasters.gilty.shared.shared.Element

@Composable
fun BsContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Element(
        FilterModel(title) {
            Box {
                Box(Modifier.padding(bottom = 40.dp)) {
                    content.invoke()
                }
            }
        }, modifier.padding(top = 28.dp)
    )
}