package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.database.AppDatabase
import com.example.model.AccentColorTheme
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.Category
import com.example.model.Product
import com.example.model.SortOption
import com.example.model.ThemeMode
import com.example.utils.BackupRestoreUtils
import com.example.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FabricRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val _appSettings = MutableStateFlow(loadSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    private fun loadSettings(): AppSettings {
        val langName = prefs.getString("language", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name
        val themeName = prefs.getString("themeMode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val colorName = prefs.getString("accentColor", AccentColorTheme.TEAL.name) ?: AccentColorTheme.TEAL.name
        val sortName = prefs.getString("sortOption", SortOption.NEWEST.name) ?: SortOption.NEWEST.name

        val language = try { AppLanguage.valueOf(langName) } catch (e: Exception) { AppLanguage.ENGLISH }
        val themeMode = try { ThemeMode.valueOf(themeName) } catch (e: Exception) { ThemeMode.SYSTEM }
        val accentColor = try { AccentColorTheme.valueOf(colorName) } catch (e: Exception) { AccentColorTheme.TEAL }
        val sortOption = try { SortOption.valueOf(sortName) } catch (e: Exception) { SortOption.NEWEST }

        return AppSettings(
            language = language,
            themeMode = themeMode,
            accentColor = accentColor,
            sortOption = sortOption
        )
    }

    suspend fun updateLanguage(language: AppLanguage) = withContext(Dispatchers.IO) {
        prefs.edit().putString("language", language.name).apply()
        _appSettings.value = _appSettings.value.copy(language = language)
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) = withContext(Dispatchers.IO) {
        prefs.edit().putString("themeMode", themeMode.name).apply()
        _appSettings.value = _appSettings.value.copy(themeMode = themeMode)
    }

    suspend fun updateAccentColor(accentColor: AccentColorTheme) = withContext(Dispatchers.IO) {
        prefs.edit().putString("accentColor", accentColor.name).apply()
        _appSettings.value = _appSettings.value.copy(accentColor = accentColor)
    }

    suspend fun updateSortOption(sortOption: SortOption) = withContext(Dispatchers.IO) {
        prefs.edit().putString("sortOption", sortOption.name).apply()
        _appSettings.value = _appSettings.value.copy(sortOption = sortOption)
    }

    suspend fun getProductById(id: Long): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun saveProduct(product: Product): Long = withContext(Dispatchers.IO) {
        if (product.id == 0L) {
            productDao.insertProduct(product)
        } else {
            productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
            product.id
        }
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProductById(id)
    }

    suspend fun generateNextProductCode(): String = withContext(Dispatchers.IO) {
        val count = productDao.getProductCount() + 1
        String.format("FAB-%04d", count)
    }

    suspend fun isProductCodeDuplicate(code: String, currentProductId: Long = 0L): Boolean = withContext(Dispatchers.IO) {
        if (code.isBlank()) return@withContext false
        val existing = productDao.getProductByCode(code.trim())
        existing != null && existing.id != currentProductId
    }

    suspend fun saveCategory(category: Category): Long = withContext(Dispatchers.IO) {
        if (category.id == 0L) {
            categoryDao.insertCategory(category)
        } else {
            categoryDao.updateCategory(category.copy(updatedAt = System.currentTimeMillis()))
            category.id
        }
    }

    suspend fun deleteCategory(categoryId: Long) = withContext(Dispatchers.IO) {
        val otherId = categoryDao.getOtherCategoryId() ?: 1L
        // Safety reassign products in deleted category to "Other"
        productDao.reassignProductsCategory(categoryId, otherId)
        categoryDao.deleteCategoryById(categoryId)
    }

    suspend fun exportBackupJson(categories: List<Category>, products: List<Product>): String = withContext(Dispatchers.IO) {
        BackupRestoreUtils.exportToJson(categories, products, _appSettings.value)
    }

    suspend fun restoreFromBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        val backup = BackupRestoreUtils.importFromJson(jsonString) ?: return@withContext false
        
        // Restore categories and products
        for (cat in backup.categories) {
            categoryDao.insertCategory(cat)
        }
        for (prod in backup.products) {
            productDao.insertProduct(prod)
        }
        updateLanguage(backup.settings.language)
        updateThemeMode(backup.settings.themeMode)
        updateAccentColor(backup.settings.accentColor)
        true
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        productDao.deleteAllProducts()
        categoryDao.deleteAllCategories()
        categoryDao.insertCategories(Category.DEFAULT_CATEGORIES)
    }

    fun saveSamplePatternImage(patternIndex: Int): String {
        return ImageUtils.saveSamplePatternToStorage(context, patternIndex)
    }
}
