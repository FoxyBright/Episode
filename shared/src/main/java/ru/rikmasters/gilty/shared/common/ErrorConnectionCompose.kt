package ru.rikmasters.gilty.shared.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.dmoral.toasty.Toasty
import es.dmoral.toasty.Toasty.custom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.R.color.toast_day
import ru.rikmasters.gilty.shared.R.color.toast_night
import ru.rikmasters.gilty.shared.R.drawable.ic_bad_connection
import ru.rikmasters.gilty.shared.common.extentions.toSp

@Preview
@Composable
private fun ErrorPReview() {
    Box(Modifier.fillMaxSize()) {
        ErrorConnection()
    }
}

@Keep
@Composable
fun ErrorConnection(
    errorMessage: String = stringResource(
        R.string.bad_connection
    ),
) {
    Box(
        Modifier
            .fillMaxHeight(0.85f)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        BottomCenter
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xCC0E0E0E),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Row(
                Modifier.padding(16.dp),
                Start, CenterVertically
            ) {
                val size = 28.dp
                Icon(
                    painter = painterResource(
                        ic_bad_connection
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = White
                )
                Text(
                    text = errorMessage,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    style = typography.labelSmall.copy(
                        White, 15.dp.toSp(),
                    ),
                )
            }
        }
    }
}

private fun Context.textTypeface() =
    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) this.resources
        .getFont(R.font.gilroy_medium)
    else null

fun Context.isDarkTheme() = if(Build.VERSION.SDK_INT >= 29)
    (this.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_NO
else Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1

@SuppressLint("CheckResult")
suspend fun Context.errorToast(
    message: String?,
    @DrawableRes icon: Int = ic_bad_connection,
    @ColorRes dayColor: Int = toast_day,
    @ColorRes nightColor: Int = toast_night,
    duration: Int = LENGTH_LONG,
    hideIcon: Boolean = false,
    hideTint: Boolean = false,
) {
    if(message.isNullOrBlank()) return
    withContext(Dispatchers.Main) {
        Toasty.Config
            .getInstance()
            ?.let {
                it.tintIcon(true)
                it.setTextSize(15)
                this@errorToast.textTypeface()
                    ?.let { tf ->
                        it.setToastTypeface(tf)
                    }
            }?.apply()
        custom(
            this@errorToast, message, icon,
            if(this@errorToast.isDarkTheme())
                nightColor else dayColor,
            duration, !hideIcon, !hideTint
        ).show()
    }
}
