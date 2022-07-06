package com.ryanjames.composemobileordering.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
    val placeholderColor: Color,
    val textBackground: Color
)

private val lightThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.Black,
    materialColors = LightThemeMaterialColors,
    hintTextColor = HintGray,
    bottomNavBackground = Color.White,
    placeholderColor = PlaceholderGray,
    textBackground = LighterGray
)

private val darkThemeColors = AppColors(
    lightTextColor = LightGray,
    darkTextColor = Color.White,
    materialColors = DarkThemeMaterialColors,
    hintTextColor = HintGray,
    bottomNavBackground = BlueGray,
    placeholderColor = PlaceholderBlueGray,
    textBackground = PlaceholderBlueGray
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
            typography = appTypography,
            shapes = Shapes,
            content = content,
        )
    }
}

val appTypography = Typography(
    h1 = TextStyle(fontSize = 42.sp, fontWeight = FontWeight.Bold),
    h2 = TextStyle(fontSize = 38.sp, fontWeight = FontWeight.Bold),
    h3 = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
    h4 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    h5 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    h6 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    subtitle1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W500),
    subtitle2 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W500),
    body1 = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W500),
)
