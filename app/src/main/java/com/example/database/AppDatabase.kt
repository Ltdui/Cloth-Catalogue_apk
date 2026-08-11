package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Category
import com.example.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fabric_collection_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            populateDefaults(getInstance(context))
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateDefaults(database: AppDatabase) {
            val categoryDao = database.categoryDao()
            val productDao = database.productDao()

            val defaultCategories = listOf(
                Category(name = "Cotton", isDefault = true),
                Category(name = "Silk", isDefault = true),
                Category(name = "Georgette", isDefault = true),
                Category(name = "Linen", isDefault = true),
                Category(name = "Printed", isDefault = true),
                Category(name = "Dress Material", isDefault = true),
                Category(name = "Nighty Fabric", isDefault = true),
                Category(name = "Other", isDefault = true)
            )

            val categoryIds = mutableListOf<Long>()
            for (cat in defaultCategories) {
                val id = categoryDao.insertCategory(cat)
                categoryIds.add(id)
            }

            // Seed initial sample fabric products for initial shop launch preview
            val cottonId = categoryIds.getOrNull(0) ?: 1L
            val silkId = categoryIds.getOrNull(1) ?: 2L
            val printedId = categoryIds.getOrNull(4) ?: 5L
            val dressMatId = categoryIds.getOrNull(5) ?: 6L

            val initialProducts = listOf(
                Product(
                    name = "Premium Jaipur Printed Cotton",
                    code = "CT-0001",
                    price = 450.0,
                    categoryId = cottonId,
                    imagePath = "sample_pattern_1",
                    description = "100% pure organic breathable cotton with vibrant block print designs.",
                    notes = "Very popular for summer kurtis and dresses."
                ),
                Product(
                    name = "Banarasi Brocade Silk",
                    code = "SLK-0002",
                    price = 1850.0,
                    categoryId = silkId,
                    imagePath = "sample_pattern_2",
                    description = "Rich zari woven Banarasi silk fabric for grand occasion wear.",
                    notes = "Best seller for festive wedding attire."
                ),
                Product(
                    name = "Floral Digital Printed Chiffon",
                    code = "PRT-0003",
                    price = 320.0,
                    categoryId = printedId,
                    imagePath = "sample_pattern_3",
                    description = "Soft lightweight drape with modern watercolor floral prints.",
                    notes = "Available in 5 color shades."
                ),
                Product(
                    name = "Unstitched Salwar Suit Set",
                    code = "DRS-0004",
                    price = 1250.0,
                    categoryId = dressMatId,
                    imagePath = "sample_pattern_4",
                    description = "3-piece cotton dress material with embroidered neck border.",
                    notes = "Includes top, bottom, and dupatta."
                )
            )

            productDao.insertProducts(initialProducts)
        }
    }
}
