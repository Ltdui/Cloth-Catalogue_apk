package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.EmptyProductState
import com.example.ui.components.ExpressiveBackgroundBlobs
import com.example.ui.components.FabricSearchBar
import com.example.ui.components.ProductCard
import com.example.ui.components.getLocalizedCategoryName
import com.example.viewmodel.FabricViewModel

@Composable
fun CollectionScreen(
    viewModel: FabricViewModel,
    onProductClick: (Long) -> Unit,
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val currentLanguage = settings.language

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCatId by viewModel.selectedCategoryId.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()

    val categoryMap = remember(categories) {
        categories.associateBy { it.id }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ExpressiveBackgroundBlobs(alpha = 0.2f)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(24.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = {
                        Text(
                            text = AppStrings.get("add_product", currentLanguage),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.testTag("collection_fab_add")
                )
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val gridColumnCount = if (maxWidth > 600.dp) 3 else 2

                Column(modifier = Modifier.fillMaxSize()) {
                    // Screen Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checkroom,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("collection", currentLanguage),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${products.size} ${AppStrings.get("total_products", currentLanguage)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Search Bar
                    FabricSearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        placeholderText = AppStrings.get("search_by_name_code", currentLanguage),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Chips
                    CategoryFilterChips(
                        categories = categories,
                        selectedCategoryId = selectedCatId,
                        onCategorySelected = { viewModel.selectCategoryFilter(it) },
                        currentLanguage = currentLanguage
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Grid Content
                    if (products.isEmpty()) {
                        EmptyProductState(
                            currentLanguage = currentLanguage,
                            onAddProductClick = onAddProductClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridColumnCount),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(products, key = { it.id }) { product ->
                                val rawCatName = categoryMap[product.categoryId]?.name ?: "Other"
                                val catName = getLocalizedCategoryName(rawCatName, currentLanguage)

                                ProductCard(
                                    product = product,
                                    categoryName = catName,
                                    onClick = { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
