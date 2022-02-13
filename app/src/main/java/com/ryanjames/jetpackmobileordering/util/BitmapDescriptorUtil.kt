package com.ryanjames.jetpackmobileordering.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.util.TypedValue
import androidx.compose.ui.unit.Dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


fun getBitmapDescriptor(context: Context, id: Int, width: Dp, height: Dp): BitmapDescriptor {

    val vectorDrawable = context.getDrawable(id) as VectorDrawable
    val widthInPixels = width.toPixelsInt(context)
    val heightInPixels = height.toPixelsInt(context)
    vectorDrawable.setBounds(0, 0, widthInPixels, heightInPixels)
    val bm = Bitmap.createBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun Dp.toPixels(context: Context): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
}

fun Dp.toPixelsInt(context: Context): Int {
    return this.toPixels(context).toInt()
}