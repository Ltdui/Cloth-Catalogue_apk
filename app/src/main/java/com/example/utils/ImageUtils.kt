package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {

    fun getImagesDir(context: Context): File {
        val dir = File(context.filesDir, "fabric_images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun copyUriToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val fileName = "fabric_${System.currentTimeMillis()}.jpg"
            val destFile = File(getImagesDir(context), fileName)
            val outputStream = FileOutputStream(destFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createCameraImageUri(context: Context): Pair<Uri, String>? {
        return try {
            val fileName = "fabric_cam_${System.currentTimeMillis()}.jpg"
            val file = File(getImagesDir(context), fileName)
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            Pair(uri, file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveSamplePatternToStorage(context: Context, patternIndex: Int): String {
        val fileName = "sample_fabric_${patternIndex}_${System.currentTimeMillis()}.jpg"
        val file = File(getImagesDir(context), fileName)
        val bitmap = createSampleFabricBitmap(patternIndex)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    fun createSampleFabricBitmap(patternIndex: Int): Bitmap {
        val width = 600
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (patternIndex % 4) {
            0 -> {
                // Jaipur Floral Cotton Pattern
                canvas.drawColor(Color.parseColor("#FFF7ED")) // Warm ivory
                paint.color = Color.parseColor("#C2410C") // Rich terracotta maroon
                val step = 100
                for (x in 50..width step step) {
                    for (y in 50..height step step) {
                        canvas.drawCircle(x.toFloat(), y.toFloat(), 25f, paint)
                        paint.color = Color.parseColor("#047857") // Emerald leaf accent
                        canvas.drawCircle((x + 20).toFloat(), (y - 20).toFloat(), 10f, paint)
                        paint.color = Color.parseColor("#C2410C")
                    }
                }
            }
            1 -> {
                // Banarasi Silk Zari Pattern
                canvas.drawColor(Color.parseColor("#881337")) // Deep Royal Magenta
                paint.color = Color.parseColor("#FACC15") // Gold Zari
                paint.strokeWidth = 6f
                paint.style = Paint.Style.STROKE
                val step = 80
                for (i in -width..width * 2 step step) {
                    canvas.drawLine(i.toFloat(), 0f, (i + width).toFloat(), height.toFloat(), paint)
                    canvas.drawLine(i.toFloat(), height.toFloat(), (i + width).toFloat(), 0f, paint)
                }
            }
            2 -> {
                // Printed Chiffon Wave
                canvas.drawColor(Color.parseColor("#E0F2FE")) // Light cyan sky
                paint.color = Color.parseColor("#0284C7")
                paint.style = Paint.Style.FILL
                val path = Path()
                for (y in 0..height step 80) {
                    path.reset()
                    path.moveTo(0f, y.toFloat())
                    path.cubicTo(200f, (y - 40).toFloat(), 400f, (y + 40).toFloat(), width.toFloat(), y.toFloat())
                    path.lineTo(width.toFloat(), (y + 40).toFloat())
                    path.cubicTo(400f, (y + 80).toFloat(), 200f, (y).toFloat(), 0f, (y + 40).toFloat())
                    path.close()
                    canvas.drawPath(path, paint)
                }
            }
            else -> {
                // Indigo Ikat Weave
                canvas.drawColor(Color.parseColor("#1E1B4B")) // Indigo dark
                paint.color = Color.parseColor("#818CF8") // Indigo light
                paint.strokeWidth = 12f
                for (x in 20..width step 40) {
                    for (y in 20..height step 40) {
                        canvas.drawRect(x.toFloat(), y.toFloat(), (x + 20).toFloat(), (y + 20).toFloat(), paint)
                    }
                }
            }
        }

        return bitmap
    }
}
