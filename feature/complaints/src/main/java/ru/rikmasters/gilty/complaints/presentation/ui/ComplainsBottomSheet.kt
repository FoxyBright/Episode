package ru.rikmasters.gilty.complaints.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainType.*
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.shared.GradientButton

private enum class ComplainType { LIE, CHEAT, SPAM, OTHER }

enum class ReportsType { USER, MEET }

@Composable
fun ComplainsContent(id: String, send: () -> Unit) {
    var tag by remember { mutableStateOf<ComplainType?>(null) }
    if(tag == null) {
        ComplainElements(
            (stringResource(R.string.complaints_title)), listOf(
                stringResource(R.string.complaints_lie_title),
                stringResource(R.string.complaints_spam_title),
                stringResource(R.string.complaints_cheater_title),
                stringResource(R.string.others_sex),
            ), Modifier.padding(16.dp),
            (stringResource(R.string.complaints_description)),
            onBack = null
        ) {
            tag = when(it) {
                0 -> LIE
                1 -> SPAM
                2 -> CHEAT
                else -> OTHER
            }
        }
        Box(Modifier.fillMaxSize())
        { Spacer(Modifier.align(BottomCenter)) }
    } else {
        var text by remember { mutableStateOf("") }
        val itemList = list(tag!!)
        val select =
            remember { mutableStateListOf<Boolean>() }
        repeat(itemList.second.size)
        { select.add(false) }
        if(tag == OTHER) ComplainTextBox(
            stringResource(R.string.others_sex),
            text, Modifier
                .fillMaxWidth()
                .padding(16.dp),
            { text = it }, { text = "" })
        { tag = null }
        else {
            ComplainElements(
                itemList.first, itemList.second,
                Modifier.padding(16.dp), (null),
                select, { tag = null }
            ) {
                repeat(select.size)
                { item -> select[item] = it == item }
            }
        }
        Box(Modifier.fillMaxSize()) {
            GradientButton(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 48.dp)
                    .align(BottomCenter),
                stringResource(R.string.complaints_button),
            ) {
                tag = null
                send()
            }
        }
    }
}

@Composable
private fun list(tag: ComplainType): Pair<String, List<String>> {
    return when(tag) {
        LIE -> Pair(
            stringResource(R.string.complaints_lie_title), listOf(
                stringResource(R.string.complaints_lie_anything_photo_label),
                stringResource(R.string.complaints_lie_celebrity_label),
                stringResource(R.string.complaints_lie_bad_category_label),
                stringResource(R.string.complaints_lie_suspicious_label),
                stringResource(R.string.others_sex)
            )
        )
        
        CHEAT -> Pair(
            stringResource(R.string.complaints_cheater_title), listOf(
                stringResource(R.string.complaints_cheater_fraud_label),
                stringResource(R.string.complaints_cheater_deception_label),
                stringResource(R.string.complaints_cheater_delusion_label)
            )
        )
        
        SPAM -> Pair(
            stringResource(R.string.complaints_spam_title), listOf(
                stringResource(R.string.complaints_spam_advertisement_label),
                stringResource(R.string.complaints_spam_sending_label),
            )
        )
        
        else -> Pair(stringResource(R.string.others_sex), listOf())
    }
}