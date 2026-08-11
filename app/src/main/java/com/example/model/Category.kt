package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category(name = "Cotton", isDefault = true),
            Category(name = "Silk", isDefault = true),
            Category(name = "Georgette", isDefault = true),
            Category(name = "Linen", isDefault = true),
            Category(name = "Printed", isDefault = true),
            Category(name = "Dress Material", isDefault = true),
            Category(name = "Nighty Fabric", isDefault = true),
            Category(name = "Other", isDefault = true)
        )
    }
}
