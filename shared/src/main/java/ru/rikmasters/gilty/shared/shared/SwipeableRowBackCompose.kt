package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun SwipeableRowBackPreview(){
    GiltyTheme {
        SwipeableRowBack()
    }
}

@Composable
fun SwipeableRowBack(modifier: Modifier = Modifier) {
    Column(
        modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(R.drawable.ic_trash_can),
            null, Modifier, Color.White
        )
        Text(
            stringResource(R.string.meeting_filter_delete_tag_label),
            Modifier.padding(),
            Color.White, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}