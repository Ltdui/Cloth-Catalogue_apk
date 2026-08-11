package com.example.utils

import com.example.model.AppSettings
import com.example.model.Category
import com.example.model.Product
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val categories: List<Category>,
    val products: List<Product>,
    val settings: AppSettings
)

object BackupRestoreUtils {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter = moshi.adapter(BackupData::class.java)

    fun exportToJson(
        categories: List<Category>,
        products: List<Product>,
        settings: AppSettings
    ): String {
        val backupData = BackupData(
            categories = categories,
            products = products,
            settings = settings
        )
        return jsonAdapter.indent("  ").toJson(backupData)
    }

    fun importFromJson(jsonString: String): BackupData? {
        return try {
            jsonAdapter.fromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
