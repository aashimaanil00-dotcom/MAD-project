package com.campussplit.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.campussplit.data.model.Person

/**
 * Holds all people in-memory using Compose snapshot state.
 * Data resets when the app process is killed.
 */
class PersonViewModel : ViewModel() {

    // Auto-increment ID counter
    private var nextId = 1L

    // Compose-aware observable list — UI recomposes automatically on changes
    private val _people = mutableStateListOf<Person>()
    val people: List<Person> get() = _people

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addPerson(name: String, phone: String = "", email: String = "", avatarColor: Int = 0) {
        _people.add(
            Person(
                id = nextId++,
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                avatarColor = avatarColor
            )
        )
    }

    fun updatePerson(updated: Person) {
        val idx = _people.indexOfFirst { it.id == updated.id }
        if (idx != -1) _people[idx] = updated
    }

    fun deletePerson(personId: Long) {
        _people.removeAll { it.id == personId }
    }

    fun getPersonById(id: Long): Person? = _people.find { it.id == id }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun searchPeople(query: String): List<Person> {
        if (query.isBlank()) return _people.toList()
        val q = query.lowercase()
        return _people.filter {
            it.name.lowercase().contains(q) ||
            it.phone.lowercase().contains(q) ||
            it.email.lowercase().contains(q)
        }
    }

    /** Resolve a list of IDs to Person objects. */
    fun getPeopleByIds(ids: List<Long>): List<Person> =
        ids.mapNotNull { id -> _people.find { it.id == id } }
}
