package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.SortOption
import com.example.ui.components.AyushTextileLogo
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.EmptyProductState
import com.example.ui.components.ExpressiveBackgroundBlobs
import com.example.ui.components.ExpressiveShowcaseHeader
import com.example.ui.components.FabricSearchBar
import com.example.ui.components.ProductCard
import com.example.ui.components.getLocalizedCategoryName
import com.example.viewmodel.FabricViewModel

@Composable
fun HomeScreen(
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
    val currentSort by viewModel.sortOption.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showShowcaseBanner by remember { mutableStateOf(false) }

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
                    modifier = Modifier.testTag("home_fab_add")
                )
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val gridColumnCount = if (maxWidth > 600.dp) 3 else 2

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Unified Header Surface (Brand Logo + Actions + Search Bar)
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_unified_header"),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        tonalElevation = 3.dp,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Top Row: Logo & Action Menu
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AyushTextileLogo(
                                    logoSize = 40.dp,
                                    tintColor = MaterialTheme.colorScheme.primary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { showShowcaseBanner = !showShowcaseBanner },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Showcase",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box {
                                        IconButton(
                                            onClick = { showSortMenu = true },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .testTag("sort_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Sort,
                                                contentDescription = AppStrings.get("sort_by", currentLanguage),
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false }
                                        ) {
                                            SortOption.values().forEach { option ->
                                                val label = when (option) {
                                                    SortOption.NEWEST -> AppStrings.get("sort_newest", currentLanguage)
                                                    SortOption.OLDEST -> AppStrings.get("sort_oldest", currentLanguage)
                                                    SortOption.NAME_ASC -> AppStrings.get("sort_name_asc", currentLanguage)
                                                    SortOption.NAME_DESC -> AppStrings.get("sort_name_desc", currentLanguage)
                                                    SortOption.PRICE_LOW_HIGH -> AppStrings.get("sort_price_low_high", currentLanguage)
                                                    SortOption.PRICE_HIGH_LOW -> AppStrings.get("sort_price_high_low", currentLanguage)
                                                }
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = label,
                                                            fontWeight = if (option == currentSort) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (option == currentSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    },
                                                    onClick = {
                                                        viewModel.updateSortOption(option)
                                                        showSortMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Search Bar logically grouped directly inside top surface
                            FabricSearchBar(
                                query = searchQuery,
                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                placeholderText = AppStrings.get("search_placeholder", currentLanguage),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Optional Showcase Header (Collapsible)
                    AnimatedVisibility(
                        visible = showShowcaseBanner && searchQuery.isEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ExpressiveShowcaseHeader(
                            currentLanguage = currentLanguage,
                            onExploreClick = { showShowcaseBanner = false },
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compact Dashboard Summary Pill Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        androidx.compose.material3.Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checkroom,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${products.size} ${AppStrings.get("total_products", currentLanguage)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        androidx.compose.material3.Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${categories.size} ${AppStrings.get("total_categories", currentLanguage)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Chips
                    CategoryFilterChips(
                        categories = categories,
                        selectedCategoryId = selectedCatId,
                        onCategorySelected = { viewModel.selectCategoryFilter(it) },
                        currentLanguage = currentLanguage
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Products Grid
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
