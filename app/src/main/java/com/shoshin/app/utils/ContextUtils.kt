package com.Shoshin.app.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/** Decodes a gallery/camera result [Uri] into a [Bitmap], same as the picker flow in EditProfileScreen. */
fun Context.decodeBitmapFromUri(uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION")
        android.provider.MediaStore.Images.Media.getBitmap(contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }
}
