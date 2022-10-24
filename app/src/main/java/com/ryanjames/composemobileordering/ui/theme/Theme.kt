package com.ryanjames.composemobileordering.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = CoralRed,
    background = DarkBlueGray,
    surface = DarkBlueGray,
    onSurface = Color.White,
    surfaceVariant = LightBlueGray,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CoralRed,
    secondary = CoralRed,
    background = Porcelain,
    surface = Porcelain,
    onSurface = Color.Black,
    surfaceVariant = Color.White,
    onBackground = Color.Black

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
    val hintTextColor: Color,
    val bottomNavBackground: Color,
    val placeholderColor: Color,
    val textBackground: Color,
    val materialColors: ColorScheme
)

private val lightThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.Black,
    materialColors = LightColorScheme,
    hintTextColor = HintGray,
    bottomNavBackground = Color.White,
    placeholderColor = PlaceholderGray,
    textBackground = LighterGray
)

private val darkThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.White,
    hintTextColor = HintGray,
    bottomNavBackground = BlueGray,
    placeholderColor = PlaceholderBlueGray,
    textBackground = PlaceholderBlueGray,
    materialColors = DarkColorScheme
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
        androidx.compose.material3.MaterialTheme(
            colorScheme = colors.materialColors,
            typography = Typography,
            content = content,
        )
    }
}
