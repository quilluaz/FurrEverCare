package com.jis_citu.furrevercare.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Theme preference manager
object ThemePreferenceManager {
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme

    fun setDarkTheme(isDark: Boolean?) {
        _isDarkTheme.value = isDark
    }
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    secondary = DarkSecondaryGreen,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

@Composable
fun FurrEverCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val themePreference by ThemePreferenceManager.isDarkTheme.collectAsState()

    val useDarkTheme = when (themePreference) {
        true -> true
        false -> false
        else -> darkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                window.statusBarColor = colorScheme.background.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
