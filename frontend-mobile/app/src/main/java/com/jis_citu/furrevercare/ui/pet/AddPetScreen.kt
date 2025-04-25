package com.jis_citu.furrevercare.ui.pet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var petName by remember { mutableStateOf("") }
    var petAge by remember { mutableStateOf("") }
    var petWeight by remember { mutableStateOf("") }

    var selectedSpecies by remember { mutableStateOf("") }
    var speciesExpanded by remember { mutableStateOf(false) }
    val speciesList = listOf("Dog", "Cat", "Bird", "Rabbit", "Hamster", "Fish", "Other")

    var selectedBreed by remember { mutableStateOf("") }
    var breedExpanded by remember { mutableStateOf(false) }
    val breedList = when (selectedSpecies) {
        "Dog" -> listOf("Labrador", "German Shepherd", "Bulldog", "Poodle", "Beagle", "Mixed", "Other")
        "Cat" -> listOf("Persian", "Siamese", "Maine Coon", "Ragdoll", "Bengal", "Mixed", "Other")
        else -> listOf("Select species first")
    }

    var selectedGender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderList = listOf("Male", "Female")

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Add New Pet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pet Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(2.dp, PrimaryGreen, CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(data = imageUri)
                                    .build()
                            ),
                            contentDescription = "Pet Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Photo",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Add Photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pet Name
                OutlinedTextField(
                    value = petName,
                    onValueChange = { petName = it },
                    label = { Text("Pet Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Species Dropdown
                ExposedDropdownMenuBox(
                    expanded = speciesExpanded,
                    onExpandedChange = { speciesExpanded = !speciesExpanded }
                ) {
                    TextField(
                        value = selectedSpecies,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                        placeholder = { Text("Select Species") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = speciesExpanded,
                        onDismissRequest = { speciesExpanded = false }
                    ) {
                        speciesList.forEach { species ->
                            DropdownMenuItem(
                                text = { Text(text = species) },
                                onClick = {
                                    selectedSpecies = species
                                    speciesExpanded = false
                                    // Reset breed when species changes
                                    selectedBreed = ""
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breed Dropdown
                ExposedDropdownMenuBox(
                    expanded = breedExpanded,
                    onExpandedChange = {
                        if (selectedSpecies.isNotEmpty()) {
                            breedExpanded = !breedExpanded
                        }
                    }
                ) {
                    TextField(
                        value = selectedBreed,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedSpecies.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                        placeholder = { Text("Select Breed") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = breedExpanded,
                        onDismissRequest = { breedExpanded = false }
                    ) {
                        breedList.forEach { breed ->
                            DropdownMenuItem(
                                text = { Text(text = breed) },
                                onClick = {
                                    selectedBreed = breed
                                    breedExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    TextField(
                        value = selectedGender,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        placeholder = { Text("Select Gender") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderList.forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(text = gender) },
                                onClick = {
                                    selectedGender = gender
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Age
                OutlinedTextField(
                    value = petAge,
                    onValueChange = { petAge = it },
                    label = { Text("Age (years)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weight
                OutlinedTextField(
                    value = petWeight,
                    onValueChange = { petWeight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        // Save pet logic would go here
                        navController.navigateUp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Pet")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPetScreenPreview() {
    FurrEverCareTheme {
        AddPetScreen(rememberNavController())
    }
}
