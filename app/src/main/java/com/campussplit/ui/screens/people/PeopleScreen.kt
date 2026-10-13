package com.campussplit.ui.screens.people

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.ui.components.EmptyState
import com.campussplit.ui.components.PersonCard
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    personVM: PersonViewModel,
    onBack: () -> Unit,
    onAddPerson: () -> Unit,
    onEditPerson: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val displayedPeople = personVM.searchPeople(searchQuery)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("People", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddPerson,
                icon = { Icon(Icons.Default.PersonAdd, null) },
                text = { Text("Add Person") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search people...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty())
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                },
                shape = MaterialTheme.shapes.large,
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            if (displayedPeople.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Default.PersonOff,
                        title = if (searchQuery.isBlank()) "No people yet" else "No results",
                        subtitle = if (searchQuery.isBlank())
                            "Tap the button below to add someone"
                        else "Try a different search term"
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(displayedPeople, key = { it.id }) { person ->
                        PersonCard(
                            person = person,
                            onClick = { onEditPerson(person.id) },
                            onEdit = { onEditPerson(person.id) },
                            onDelete = { personVM.deletePerson(person.id) }
                        )
                    }
                }
            }
        }
    }
}
