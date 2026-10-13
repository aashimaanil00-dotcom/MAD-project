package com.campussplit.ui.screens.people

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campussplit.ui.theme.AvatarColors
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonScreen(personVM: PersonViewModel, onBack: () -> Unit) {
    PersonFormScreen(
        title = "Add Person",
        initialName = "", initialPhone = "", initialEmail = "", initialAvatarColor = 0,
        onSave = { name, phone, email, color ->
            personVM.addPerson(name, phone, email, color)
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonScreen(personId: Long, personVM: PersonViewModel, onBack: () -> Unit) {
    val person = personVM.getPersonById(personId)
    if (person == null) { onBack(); return }

    PersonFormScreen(
        title = "Edit Person",
        initialName = person.name,
        initialPhone = person.phone,
        initialEmail = person.email,
        initialAvatarColor = person.avatarColor,
        onSave = { name, phone, email, color ->
            personVM.updatePerson(person.copy(name = name, phone = phone, email = email, avatarColor = color))
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonFormScreen(
    title: String,
    initialName: String, initialPhone: String, initialEmail: String, initialAvatarColor: Int,
    onSave: (String, String, String, Int) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var email by remember { mutableStateOf(initialEmail) }
    var selectedColor by remember { mutableIntStateOf(initialAvatarColor) }
    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar preview
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(AvatarColors[selectedColor % AvatarColors.size]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            // Color picker
            Text("Avatar Color", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(AvatarColors) { idx, color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp).clip(CircleShape).background(color)
                            .clickable { selectedColor = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == idx)
                            Icon(Icons.Default.Check, null, tint = Color.White,
                                modifier = Modifier.size(20.dp))
                    }
                }
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it; nameError = false },
                label = { Text("Name *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, isError = nameError,
                supportingText = if (nameError) ({ Text("Name is required") }) else null,
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone (optional)") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, leadingIcon = { Icon(Icons.Default.Phone, null) }
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email (optional)") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, leadingIcon = { Icon(Icons.Default.Email, null) }
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    if (name.trim().isEmpty()) nameError = true
                    else onSave(name.trim(), phone.trim(), email.trim(), selectedColor)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Person", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
