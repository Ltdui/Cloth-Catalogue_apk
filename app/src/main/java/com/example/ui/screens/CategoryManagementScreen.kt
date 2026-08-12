package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.Category
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.ExpressiveBackgroundBlobs
import com.example.ui.components.getLocalizedCategoryName
import com.example.viewmodel.FabricViewModel

@Composable
fun CategoryManagementScreen(
    viewModel: FabricViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val currentLanguage = settings.language
    val categories by viewModel.categories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    var categoryNameInput by remember { mutableStateOf("") }

    if (showAddDialog || editingCategory != null) {
        val dialogTitle = if (editingCategory != null) {
            AppStrings.get("edit_category", currentLanguage)
        } else {
            AppStrings.get("add_category", currentLanguage)
        }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingCategory = null
                categoryNameInput = ""
            },
            title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = categoryNameInput,
                    onValueChange = { categoryNameInput = it },
                    label = { Text(AppStrings.get("category_name", currentLanguage)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_category_name"),
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (categoryNameInput.isNotBlank()) {
                            val catId = editingCategory?.id ?: 0L
                            viewModel.saveCategory(catId, categoryNameInput)
                            showAddDialog = false
                            editingCategory = null
                            categoryNameInput = ""
                        }
                    },
                    modifier = Modifier.testTag("btn_save_category"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(AppStrings.get("save", currentLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingCategory = null
                    categoryNameInput = ""
                }) {
                    Text(AppStrings.get("cancel", currentLanguage))
                }
            }
        )
    }

    if (deletingCategory != null) {
        ConfirmDeleteDialog(
            titleKey = "delete_category",
            messageKey = "confirm_delete_category",
            currentLanguage = currentLanguage,
            onConfirm = {
                viewModel.deleteCategory(deletingCategory!!.id)
                deletingCategory = null
            },
            onDismiss = { deletingCategory = null }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        ExpressiveBackgroundBlobs(alpha = 0.2f)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        categoryNameInput = ""
                        showAddDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(24.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(AppStrings.get("add_category", currentLanguage), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("fab_add_category")
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Screen Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = AppStrings.get("category_management", currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${categories.size} ${AppStrings.get("total_categories", currentLanguage)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Categories List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories, key = { it.id }) { category ->
                        val displayName = getLocalizedCategoryName(category.name, currentLanguage)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("category_item_${category.id}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row {
                                    IconButton(
                                        onClick = {
                                            editingCategory = category
                                            categoryNameInput = category.name
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = AppStrings.get("edit", currentLanguage),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    if (!category.isDefault) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { deletingCategory = category },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = AppStrings.get("delete", currentLanguage),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
