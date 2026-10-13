package com.campussplit.ui.screens.expense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.data.model.Person
import com.campussplit.ui.components.ExpenseCategories
import com.campussplit.ui.components.PersonAvatar
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    groupId: Long,
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    personVM: PersonViewModel,
    onBack: () -> Unit
) {
    val group = groupVM.getGroupById(groupId)
    if (group == null) { onBack(); return }
    val members = personVM.getPeopleByIds(group.memberIds)

    ExpenseFormScreen(
        title = "Add Expense",
        members = members,
        memberCount = members.size,
        initialTitle = "", initialAmount = "", initialPaidBy = null,
        initialCategory = "General", initialNote = "",
        onSave = { title, amount, paidById, category, note ->
            expenseVM.addExpense(groupId, title, amount, paidById, category, note)
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expenseId: Long,
    groupId: Long,
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    personVM: PersonViewModel,
    onBack: () -> Unit
) {
    val group   = groupVM.getGroupById(groupId)
    val expense = expenseVM.getExpenseById(expenseId)
    if (group == null || expense == null) { onBack(); return }
    val members = personVM.getPeopleByIds(group.memberIds)

    ExpenseFormScreen(
        title = "Edit Expense",
        members = members,
        memberCount = members.size,
        initialTitle    = expense.title,
        initialAmount   = expense.amount.toString(),
        initialPaidBy   = members.find { it.id == expense.paidByPersonId },
        initialCategory = expense.category,
        initialNote     = expense.note,
        onSave = { title, amount, paidById, category, note ->
            expenseVM.updateExpense(
                expense.copy(title = title, amount = amount, paidByPersonId = paidById,
                    category = category, note = note)
            )
            onBack()
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseFormScreen(
    title: String,
    members: List<Person>,
    memberCount: Int,
    initialTitle: String,
    initialAmount: String,
    initialPaidBy: Person?,
    initialCategory: String,
    initialNote: String,
    onSave: (String, Double, Long?, String, String) -> Unit,
    onBack: () -> Unit
) {
    var expTitle  by remember { mutableStateOf(initialTitle) }
    var amount    by remember { mutableStateOf(initialAmount) }
    var paidBy    by remember { mutableStateOf(initialPaidBy) }
    var category  by remember { mutableStateOf(initialCategory) }
    var note      by remember { mutableStateOf(initialNote) }
    var titleErr  by remember { mutableStateOf(false) }
    var amountErr by remember { mutableStateOf(false) }

    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
    val sharePerPerson = if (memberCount > 0) parsedAmount / memberCount else 0.0

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Title
            OutlinedTextField(
                value = expTitle, onValueChange = { expTitle = it; titleErr = false },
                label = { Text("Expense Title *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, isError = titleErr,
                supportingText = if (titleErr) ({ Text("Title is required") }) else null,
                leadingIcon = { Icon(Icons.Default.Receipt, null) }
            )

            // Amount
            OutlinedTextField(
                value = amount, onValueChange = { amount = it; amountErr = false },
                label = { Text("Amount (₹) *") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, isError = amountErr,
                supportingText = if (amountErr) ({ Text("Enter a valid positive amount") }) else null,
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) }
            )

            // Live equal split preview
            if (parsedAmount > 0 && memberCount > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Equal Split", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("÷ $memberCount members", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                        Text("₹${String.format("%.2f", sharePerPerson)} / person",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            // Category chips
            Text("Category", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExpenseCategories) { (name, icon) ->
                    FilterChip(
                        selected = category == name,
                        onClick = { category = name },
                        label = { Text(name) },
                        leadingIcon = { Icon(icon, name, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // Paid by
            Text("Paid By", style = MaterialTheme.typography.labelLarge)
            if (members.isEmpty()) {
                Text("No members in this group. Edit the group to add people.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.forEach { person ->
                        val isSelected = paidBy?.id == person.id
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                paidBy = if (isSelected) null else person
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.secondaryContainer
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
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                if (isSelected)
                                    Icon(Icons.Default.CheckCircle, null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }

            // Note
            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth(),
                maxLines = 3, leadingIcon = { Icon(Icons.Default.Notes, null) }
            )

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    when {
                        expTitle.trim().isEmpty() -> titleErr = true
                        amt == null || amt <= 0   -> amountErr = true
                        else -> onSave(expTitle.trim(), amt, paidBy?.id, category, note.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Expense", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
