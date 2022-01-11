package com.ryanjames.jetpackmobileordering.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val DarkThemeMaterialColors = darkColors(
    primary = PrimaryDark,
    primaryVariant = DarkBlueGray,
    secondary = CoralRed,
    background = DarkBlueGray,
    surface = LightBlueGray
)

private val LightThemeMaterialColors = lightColors(
    primary = CoralRed,
    primaryVariant = CoralRed,
    secondary = CoralRed,
    background = Porcelain,
    surface = Color.White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

private val LocalAppColors = staticCompositionLocalOf {
    lightThemeColors
}

@Stable
data class AppColors(
    val lightTextColor: Color,
    val darkTextColor: Color,
    val materialColors: Colors,
    val hintTextColor: Color,
    val bottomNavBackground: Color,
    val placeholderColor: Color
)

private val lightThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.Black,
    materialColors = LightThemeMaterialColors,
    hintTextColor = HintGray,
    bottomNavBackground = Color.White,
    placeholderColor = PlaceholderGray
)

private val darkThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.White,
    materialColors = DarkThemeMaterialColors,
    hintTextColor = HintGray,
    bottomNavBackground = BlueGray,
    placeholderColor = PlaceholderBlueGray
)

@Composable
fun MyComposeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) darkThemeColors else lightThemeColors

    CompositionLocalProvider(
        LocalAppColors provides colors,
    ) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}

