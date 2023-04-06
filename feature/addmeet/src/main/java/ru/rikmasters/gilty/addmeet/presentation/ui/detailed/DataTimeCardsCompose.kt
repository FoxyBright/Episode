package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DataTimeType.TIME
import ru.rikmasters.gilty.shared.R

enum class DataTimeType { DATE, TIME }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DataTimeCard(
    text: String,
    type: DataTimeType,
    online: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)
) {
    Card(
        onClick, modifier.fillMaxWidth(),
        (true), shapes.large,
        cardColors(colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(18.dp)) {
            Icon(
                painterResource(
                    if(type == TIME) R.drawable.ic_clock
                    else R.drawable.ic_calendar
                ),
                (null), Modifier.size(28.dp),
                if(online) colorScheme.secondary
                else colorScheme.primary
            )
            Text(
                stringResource(
                    if(type == TIME)
                        R.string.add_meet_detailed_meet_duration
                    else R.string.add_meet_detailed_meet_date
                ),
                Modifier.padding(top = 26.dp),
                colorScheme.tertiary,
                style = typography.bodyMedium,
                fontWeight = SemiBold
            )
            Text(
                text.ifEmpty {
                    stringResource(
                        if(type == TIME)
                            R.string.add_meet_detailed_meet_duration_place_holder
                        else R.string.add_meet_detailed_meet_date_place_holder
                    )
                }, Modifier.padding(top = 4.dp),
                if(text.isBlank())
                    colorScheme.onTertiary
                else if(online) colorScheme.secondary
                else colorScheme.primary,
                style = typography.labelSmall,
                fontWeight = SemiBold
            )
        }
    }
}