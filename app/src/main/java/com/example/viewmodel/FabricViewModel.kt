package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AccentColorTheme
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.Category
import com.example.model.Product
import com.example.model.SortOption
import com.example.model.ThemeMode
import com.example.repository.FabricRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FabricViewModel(application: Application) : AndroidViewModel(application) {
    val repository = FabricRepository(application)

    val appSettings: StateFlow<AppSettings> = repository.appSettings

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long>(0L) // 0L means ALL
    val selectedCategoryId: StateFlow<Long> = _selectedCategoryId.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            appSettings.collect { settings ->
                _sortOption.value = settings.sortOption
            }
        }
    }

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        categories,
        searchQuery,
        selectedCategoryId,
        sortOption
    ) { products, catList, query, catId, sort ->
        val categoryMap = catList.associateBy { it.id }

        products.filter { product ->
            // Category filter
            val matchesCategory = if (catId == 0L) true else product.categoryId == catId

            // Search filter (Name, Code, or Category Name)
            val catName = categoryMap[product.categoryId]?.name ?: ""
            val q = query.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                    product.name.lowercase().contains(q) ||
                    product.code.lowercase().contains(q) ||
                    catName.lowercase().contains(q)

            matchesCategory && matchesSearch
        }.let { list ->
            when (sort) {
                SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
                SortOption.OLDEST -> list.sortedBy { it.createdAt }
                SortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
                SortOption.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
                SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
                SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategoryFilter(categoryId: Long) {
        _selectedCategoryId.value = categoryId
    }

    fun updateSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
        viewModelScope.launch {
            repository.updateSortOption(sortOption)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.updateLanguage(language)
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(themeMode)
        }
    }

    fun updateAccentColor(accentColor: AccentColorTheme) {
        viewModelScope.launch {
            repository.updateAccentColor(accentColor)
        }
    }

    fun updateUseDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            repository.updateUseDynamicColor(useDynamicColor)
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    suspend fun autoGenerateProductCode(): String {
        return repository.generateNextProductCode()
    }

    suspend fun saveProduct(
        id: Long,
        name: String,
        code: String,
        priceStr: String,
        categoryId: Long,
        imagePath: String?,
        description: String,
        notes: String
    ): Boolean {
        if (name.isBlank()) {
            return false
        }
        val price = priceStr.toDoubleOrNull() ?: return false

        val finalCode = if (code.isBlank()) repository.generateNextProductCode() else code.trim()

        if (repository.isProductCodeDuplicate(finalCode, id)) {
            return false
        }

        val product = Product(
            id = id,
            name = name.trim(),
            code = finalCode,
            price = price,
            categoryId = categoryId,
            imagePath = imagePath,
            description = description.trim(),
            notes = notes.trim()
        )

        repository.saveProduct(product)
        return true
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun saveCategory(id: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.saveCategory(Category(id = id, name = name.trim()))
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
        }
    }

    suspend fun exportBackupJson(): String {
        return repository.exportBackupJson(categories.value, allProducts.value)
    }

    suspend fun importBackupJson(jsonString: String): Int {
        val success = repository.restoreFromBackupJson(jsonString)
        return if (success) allProducts.value.size else -1
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun generateSamplePatternImage(index: Int): String {
        return repository.saveSamplePatternImage(index)
    }
}
