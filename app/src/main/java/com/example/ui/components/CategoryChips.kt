package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.AppLanguage
import com.example.model.Category

@Composable
fun CategoryFilterChips(
    categories: List<Category>,
    selectedCategoryId: Long,
    onCategorySelected: (Long) -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val allText = AppStrings.get("all", currentLanguage)

    LazyRow(
        modifier = modifier.testTag("category_filter_chips"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        // "ALL" Chip
        item {
            val isSelected = selectedCategoryId == 0L
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(0L) },
                shape = RoundedCornerShape(20.dp),
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                label = {
                    Text(
                        text = allText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Category items
        items(categories, key = { it.id }) { category ->
            val isSelected = selectedCategoryId == category.id
            val localizedCatName = getLocalizedCategoryName(category.name, currentLanguage)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category.id) },
                shape = RoundedCornerShape(20.dp),
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                label = {
                    Text(
                        text = localizedCatName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

fun getLocalizedCategoryName(categoryName: String, language: AppLanguage): String {
    val key = when (categoryName.lowercase()) {
        "cotton" -> "cat_cotton"
        "silk" -> "cat_silk"
        "georgette" -> "cat_georgette"
        "linen" -> "cat_linen"
        "printed" -> "cat_printed"
        "dress material" -> "cat_dress_material"
        "nighty fabric" -> "cat_nighty_fabric"
        "other" -> "cat_other"
        else -> null
    }
    return if (key != null) AppStrings.get(key, language) else categoryName
}
