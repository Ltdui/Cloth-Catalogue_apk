package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.localization.AppStrings
import com.example.model.Product
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.ExpressiveBackgroundBlobs
import com.example.ui.components.getLocalizedCategoryName
import com.example.utils.CurrencyFormatter
import com.example.utils.ImageUtils
import com.example.viewmodel.FabricViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: FabricViewModel,
    productId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()
    val currentLanguage = settings.language
    val categories by viewModel.categories.collectAsState()

    var product by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = viewModel.repository.getProductById(productId)
    }

    if (product == null) {
        return
    }

    val prod = product!!
    val categoryName = categories.find { it.id == prod.categoryId }?.name ?: "Other"
    val localizedCategory = getLocalizedCategoryName(categoryName, currentLanguage)

    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateAddedStr = dateFormat.format(Date(prod.createdAt))
    val lastUpdatedStr = dateFormat.format(Date(prod.updatedAt))

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            currentLanguage = currentLanguage,
            onConfirm = {
                viewModel.deleteProduct(prod.id)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        ExpressiveBackgroundBlobs(alpha = 0.2f)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
                    title = {
                        Text(
                            text = AppStrings.get("product_details", currentLanguage),
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val shareTemplate = AppStrings.get("share_product_template", currentLanguage)
                                val priceFormatted = CurrencyFormatter.formatPrice(prod.price)
                                val shareText = String.format(
                                    shareTemplate,
                                    prod.name,
                                    prod.code,
                                    priceFormatted,
                                    localizedCategory
                                )

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, prod.name)
                                    putExtra(Intent.EXTRA_TEXT, shareText)

                                    if (!prod.imagePath.isNullOrEmpty() && prod.imagePath!!.startsWith("/")) {
                                        val file = File(prod.imagePath!!)
                                        if (file.exists()) {
                                            val authority = "${context.packageName}.fileprovider"
                                            val uri = FileProvider.getUriForFile(context, authority, file)
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            type = "image/*"
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                    }
                                }

                                context.startActivity(Intent.createChooser(sendIntent, AppStrings.get("share", currentLanguage)))
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large Hero Image Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.15f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!prod.imagePath.isNullOrEmpty()) {
                            val imageModel = if (prod.imagePath!!.startsWith("/")) {
                                File(prod.imagePath!!)
                            } else if (prod.imagePath!!.startsWith("sample_pattern_")) {
                                val idx = prod.imagePath!!.removePrefix("sample_pattern_").toIntOrNull() ?: 0
                                ImageUtils.createSampleFabricBitmap(idx)
                            } else {
                                prod.imagePath
                            }

                            AsyncImage(
                                model = imageModel,
                                contentDescription = prod.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Checkroom,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        // Floating Category Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = localizedCategory,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Header Card: Name, SKU, Price
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SKU: ${prod.code}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = CurrencyFormatter.formatPrice(prod.price),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Description Card
                if (prod.description.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = AppStrings.get("description", currentLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = prod.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Notes Card
                if (prod.notes.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = AppStrings.get("notes", currentLanguage),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = prod.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Timestamps Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${AppStrings.get("date_added", currentLanguage)}: $dateAddedStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${AppStrings.get("last_updated", currentLanguage)}: $lastUpdatedStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action Buttons: Edit & Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onEditClick(prod.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("btn_detail_edit"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("edit", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("btn_detail_delete"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("delete", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
