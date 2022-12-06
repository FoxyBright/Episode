package ru.rikmasters.gilty.addmeet.presentation.ui.detailed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.addmeet.presentation.ui.detailed.DataTimeType.TIME
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.drawable.ic_calendar
import ru.rikmasters.gilty.shared.R.drawable.ic_clock
import ru.rikmasters.gilty.shared.R.string.add_meet_detailed_meet_date_place_holder
import ru.rikmasters.gilty.shared.R.string.add_meet_detailed_meet_duration_place_holder

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
                    if(type == TIME) ic_clock
                    else ic_calendar
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
                            add_meet_detailed_meet_duration_place_holder
                        else add_meet_detailed_meet_date_place_holder
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