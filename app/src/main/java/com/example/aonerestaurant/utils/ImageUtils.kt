package com.example.aonerestaurant.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun uriToBase64Compressed(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            inputStream.close()

            // Scale down to max 500x500 px for ultra-fast Firestore sync
            val maxDimension = 500
            val width = originalBitmap.width
            val height = originalBitmap.height

            if (width <= 0 || height <= 0) return null

            val targetWidth: Int
            val targetHeight: Int
            if (width > height) {
                targetWidth = maxDimension
                targetHeight = ((maxDimension.toFloat() / width) * height).toInt()
            } else {
                targetHeight = maxDimension
                targetWidth = ((maxDimension.toFloat() / height) * width).toInt()
            }

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth.coerceAtLeast(1), targetHeight.coerceAtLeast(1), true)

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error compressing image: ${e.message}")
            null
        }
    }

    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val pureBase64 = if (base64Str.contains(",")) base64Str.substringAfter(",") else base64Str
            val decodedBytes = Base64.decode(pureBase64.trim(), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error decoding base64 image: ${e.message}")
            null
        }
    }
}

@Composable
fun ItemImageThumbnail(
    imageUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackIcon: ImageVector = Icons.Default.Restaurant
) {
    val bitmap = remember(imageUri) {
        if (!imageUri.isNullOrBlank() && (imageUri.startsWith("data:image") || imageUri.length > 100)) {
            ImageUtils.base64ToBitmap(imageUri)
        } else {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else if (!imageUri.isNullOrBlank()) {
        AsyncImage(
            model = imageUri,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFF6A040F)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = null,
                tint = Color(0xFFFFB703),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
