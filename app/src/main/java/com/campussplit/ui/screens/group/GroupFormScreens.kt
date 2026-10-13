package com.campussplit.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.data.model.Group
import com.campussplit.ui.components.GroupIcon
import com.campussplit.ui.components.GroupIcons
import com.campussplit.ui.components.PersonAvatar
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(groupVM: GroupViewModel, personVM: PersonViewModel, onBack: () -> Unit) {
    GroupFormScreen(
        title = "New Group",
        initialName = "", initialDescription = "", initialIconIndex = 0,
        initialSelectedIds = emptySet(),
        allPeople = personVM.people,
        onSave = { name, desc, icon, memberIds ->
            groupVM.addGroup(name, desc, icon, memberIds)
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupScreen(
    groupId: Long,
    groupVM: GroupViewModel,
    personVM: PersonViewModel,
    onBack: () -> Unit
) {
    val group = groupVM.getGroupById(groupId)
    if (group == null) { onBack(); return }

    GroupFormScreen(
        title = "Edit Group",
        initialName = group.name,
        initialDescription = group.description,
        initialIconIndex = group.iconIndex,
        initialSelectedIds = group.memberIds.toSet(),
        allPeople = personVM.people,
        onSave = { name, desc, icon, memberIds ->
            groupVM.updateGroup(group.copy(name = name, description = desc,
                iconIndex = icon, memberIds = memberIds))
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupFormScreen(
    title: String,
    initialName: String, initialDescription: String, initialIconIndex: Int,
    initialSelectedIds: Set<Long>,
    allPeople: List<com.campussplit.data.model.Person>,
    onSave: (String, String, Int, List<Long>) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedIcon by remember { mutableIntStateOf(initialIconIndex) }
    var selectedIds by remember { mutableStateOf(initialSelectedIds) }
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
            // Icon preview
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                GroupIcon(iconIndex = selectedIcon, size = 72)
            }

            // Icon picker
            Text("Choose Icon", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(GroupIcons) { idx, icon ->
                    val selected = selectedIcon == idx
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedIcon = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon, contentDescription = null,
                            tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            // Name
            OutlinedTextField(
                value = name, onValueChange = { name = it; nameError = false },
                label = { Text("Group Name *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, isError = nameError,
                supportingText = if (nameError) ({ Text("Name is required") }) else null,
                leadingIcon = { Icon(Icons.Default.Group, null) }
            )

            // Description
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(), maxLines = 3,
                leadingIcon = { Icon(Icons.Default.Description, null) }
            )

            // Members
            Text(
                "Members (${selectedIds.size} selected)",
                style = MaterialTheme.typography.labelLarge
            )
            if (allPeople.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                        Text("No people yet. Add people first!", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allPeople.forEach { person ->
                        val isSelected = person.id in selectedIds
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedIds = if (isSelected) selectedIds - person.id
                                             else selectedIds + person.id
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                PersonAvatar(person = person, size = 36)
                                Text(person.name, modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                                if (isSelected)
                                    Icon(Icons.Default.CheckCircle, null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    if (name.trim().isEmpty()) nameError = true
                    else onSave(name.trim(), description.trim(), selectedIcon, selectedIds.toList())
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Group", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
