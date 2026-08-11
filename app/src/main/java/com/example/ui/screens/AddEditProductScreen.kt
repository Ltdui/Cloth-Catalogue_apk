package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.localization.AppStrings
import com.example.ui.components.getLocalizedCategoryName
import com.example.utils.ImageUtils
import com.example.viewmodel.FabricViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: FabricViewModel,
    productId: Long = 0L,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settings by viewModel.appSettings.collectAsState()
    val currentLanguage = settings.language
    val categories by viewModel.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long>(0L) }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Pair<Uri, String>?>(null) }

    // Load existing product if editing
    LaunchedEffect(productId) {
        if (productId != 0L) {
            val existing = viewModel.repository.getProductById(productId)
            if (existing != null) {
                name = existing.name
                code = existing.code
                priceStr = existing.price.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() }
                selectedCategoryId = existing.categoryId
                imagePath = existing.imagePath
                description = existing.description
                notes = existing.notes
            }
        } else {
            // New product default category
            if (categories.isNotEmpty() && selectedCategoryId == 0L) {
                selectedCategoryId = categories.first().id
            }
            // Auto generate initial code
            code = viewModel.autoGenerateProductCode()
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            imagePath = tempCameraUri?.second
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = ImageUtils.copyUriToInternalStorage(context, it)
            if (savedPath != null) {
                imagePath = savedPath
            }
        }
    }

    val screenTitle = if (productId == 0L) AppStrings.get("add_product", currentLanguage) else AppStrings.get("edit_product", currentLanguage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = screenTitle,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Banner if validation fails
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Photo Selection Box
            Text(
                text = AppStrings.get("product_details", currentLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!imagePath.isNull_or_empty()) {
                    val imageModel = if (imagePath!!.startsWith("/")) {
                        File(imagePath!!)
                    } else if (imagePath!!.startsWith("sample_pattern_")) {
                        val idx = imagePath!!.removePrefix("sample_pattern_").toIntOrNull() ?: 0
                        ImageUtils.createSampleFabricBitmap(idx)
                    } else {
                        imagePath
                    }

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Fabric Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Remove / Change Overlay
                    IconButton(
                        onClick = { imagePath = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Photo",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AppStrings.get("take_photo", currentLanguage) + " / " + AppStrings.get("choose_gallery", currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Image Action Options (Camera / Gallery / Sample)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val cameraInfo = ImageUtils.createCameraImageUri(context)
                        if (cameraInfo != null) {
                            tempCameraUri = cameraInfo
                            cameraLauncher.launch(cameraInfo.first)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_camera"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = AppStrings.get("take_photo", currentLanguage), style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_gallery"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = AppStrings.get("choose_gallery", currentLanguage), style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = {
                        val sampleIdx = (0..3).random()
                        imagePath = viewModel.generateSamplePatternImage(sampleIdx)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_sample"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = AppStrings.get("use_sample_pattern", currentLanguage), style = MaterialTheme.typography.labelSmall)
                }
            }

            // Fields: Product Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("product_name", currentLanguage) + " *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_product_name"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Fields: Code / SKU with Auto Generate button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.get("product_code", currentLanguage)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_product_code"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        scope.launch {
                            code = viewModel.autoGenerateProductCode()
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("btn_auto_code")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = AppStrings.get("auto_generate_code", currentLanguage),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Fields: Price (Numeric)
            OutlinedTextField(
                value = priceStr,
                onValueChange = {
                    priceStr = it
                    errorMessage = null
                },
                label = { Text(AppStrings.get("price", currentLanguage) + " (₹) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_product_price"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCat = categories.find { it.id == selectedCategoryId }
                val displayCatName = selectedCat?.let { getLocalizedCategoryName(it.name, currentLanguage) } ?: ""

                OutlinedTextField(
                    value = displayCatName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(AppStrings.get("category", currentLanguage)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("dropdown_category"),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(getLocalizedCategoryName(cat.name, currentLanguage)) },
                            onClick = {
                                selectedCategoryId = cat.id
                                isCategoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(AppStrings.get("description", currentLanguage)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_description"),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(AppStrings.get("notes", currentLanguage)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notes"),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save & Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(AppStrings.get("cancel", currentLanguage))
                }

                Button(
                    onClick = {
                        scope.launch {
                            if (name.isBlank()) {
                                errorMessage = AppStrings.get("err_name_required", currentLanguage)
                                return@launch
                            }
                            val priceVal = priceStr.toDoubleOrNull()
                            if (priceVal == null) {
                                errorMessage = AppStrings.get("err_price_invalid", currentLanguage)
                                return@launch
                            }

                            val activeCatId = if (selectedCategoryId != 0L) selectedCategoryId else categories.firstOrNull()?.id ?: 1L

                            val success = viewModel.saveProduct(
                                id = productId,
                                name = name,
                                code = code,
                                priceStr = priceStr,
                                categoryId = activeCatId,
                                imagePath = imagePath,
                                description = description,
                                notes = notes
                            )

                            if (success) {
                                onNavigateBack()
                            } else {
                                errorMessage = AppStrings.get("err_code_duplicate", currentLanguage)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("btn_save_product"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = AppStrings.get("save", currentLanguage),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
