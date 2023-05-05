package ru.rikmasters.gilty.bottomsheet.presentation.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportArrowState.ARROW
import ru.rikmasters.gilty.bottomsheet.presentation.ui.reports.ReportArrowState.NONE
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.model.report.*
import ru.rikmasters.gilty.shared.shared.GradientButton

data class ReportsBsState(
    val reports: List<Report>,
    val screen: Report?,
    val selected: ReportSubtype?,
    val description: String,
    val objectType: ReportObjectType,
    val enabled: Boolean,
)

interface ReportsBsCallback {
    
    fun onSendReport()
    fun onDescriptionChange(text: String)
    fun onClearDescription()
    fun onNavigate(screen: Report?)
    fun onSelectReport(report: ReportSubtype?)
    fun onBack()
}

@Composable
fun ReportsBsContent(
    state: ReportsBsState,
    modifier: Modifier = Modifier,
    callback: ReportsBsCallback? = null,
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        state.screen?.let { screen ->
            if(screen is Other)
                Other(
                    screen, state.description,
                    { callback?.onDescriptionChange(it) },
                    { callback?.onClearDescription() }
                ) { callback?.onNavigate(null) }
            else SubReports(
                screen, state.selected,
                { callback?.onNavigate(null) }
            ) {
                if(it == ReportSubtype.OTHER)
                    callback?.onNavigate(
                        Other(state.objectType)
                    )
                else callback?.onSelectReport(it)
            }
        } ?: run {
            MainScreen(
                state.objectType,
                state.reports.map { it.map() },
                { callback?.onBack() }
            ) { callback?.onNavigate(it) }
        }
        
        state.screen?.let {
            Button(
                state.enabled, Modifier
                    .imePadding()
                    .align(BottomCenter)
            ) { callback?.onSendReport() }
        }
    }
}

@Composable
private fun Other(
    screen: Report,
    description: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    ComplainTextBox(
        screen.display(),
        description,
        Modifier.fillMaxWidth(),
        { onTextChange(it) },
        { onClear() }
    ) { onBack() }
}

@Composable
private fun SubReports(
    screen: Report,
    selected: ReportSubtype?,
    onBack: () -> Unit,
    onSelect: (ReportSubtype?) -> Unit,
) {
    ComplainElements(
        screen.display(),
        screen.subTypes, Modifier,
        (null), selected, NONE,
        { onBack() }
    ) { onSelect(it) }
}

@Composable
private fun MainScreen(
    objectType: ReportObjectType,
    list: List<ReportSubtype>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onSelect: (Report) -> Unit,
) {
    ComplainElements(
        stringResource(R.string.complaints_title),
        list, modifier,
        (stringResource(R.string.complaints_description)),
        (null), ARROW, { onBack() },
    ) { onSelect(it.report(objectType)) }
}

@Composable
private fun Button(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    GradientButton(
        modifier.padding(bottom = 48.dp),
        stringResource(R.string.complaints_button),
        enabled
    ) { onClick() }
}