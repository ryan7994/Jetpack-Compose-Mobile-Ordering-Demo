package com.ryanjames.composemobileordering.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ryanjames.composemobileordering.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp


val fontRubik = FontFamily(
    Font(R.font.rubik_light, weight = FontWeight.ExtraLight),
    Font(R.font.rubik_light_italic, weight = FontWeight.ExtraLight, style = FontStyle.Italic),
    Font(R.font.rubik_light, weight = FontWeight.Thin),
    Font(R.font.rubik_light_italic, weight = FontWeight.Thin, style = FontStyle.Italic),
    Font(R.font.rubik_light, weight = FontWeight.Light),
    Font(R.font.rubik_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.rubik_regular, weight = FontWeight.Normal),
    Font(R.font.rubik_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.rubik_medium, weight = FontWeight.Medium),
    Font(R.font.rubik_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(R.font.rubik_medium, weight = FontWeight.SemiBold),
    Font(R.font.rubik_medium_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.rubik_bold, weight = FontWeight.Bold),
    Font(R.font.rubik_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(R.font.rubik_bold, weight = FontWeight.ExtraBold),
    Font(R.font.rubik_bold_italic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
    Font(R.font.rubik_black, weight = FontWeight.Black),
    Font(R.font.rubik_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = fontRubik,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)