package com.campussplit.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.campussplit.data.model.Group

/**
 * Holds all groups in-memory using Compose snapshot state.
 */
class GroupViewModel : ViewModel() {

    private var nextId = 1L

    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addGroup(
        name: String,
        description: String = "",
        iconIndex: Int = 0,
        memberIds: List<Long> = emptyList()
    ) {
        _groups.add(
            Group(
                id = nextId++,
                name = name.trim(),
                description = description.trim(),
                iconIndex = iconIndex,
                memberIds = memberIds.distinct()
            )
        )
    }

    fun updateGroup(updated: Group) {
        val idx = _groups.indexOfFirst { it.id == updated.id }
        if (idx != -1) _groups[idx] = updated
    }

    fun deleteGroup(groupId: Long) {
        _groups.removeAll { it.id == groupId }
    }

    fun getGroupById(id: Long): Group? = _groups.find { it.id == id }

    // ── Member helpers ────────────────────────────────────────────────────────

    fun addMemberToGroup(groupId: Long, personId: Long) {
        val idx = _groups.indexOfFirst { it.id == groupId }
        if (idx != -1) {
            val g = _groups[idx]
            if (personId !in g.memberIds) {
                _groups[idx] = g.copy(memberIds = g.memberIds + personId)
            }
        }
    }

    fun removeMemberFromGroup(groupId: Long, personId: Long) {
        val idx = _groups.indexOfFirst { it.id == groupId }
        if (idx != -1) {
            val g = _groups[idx]
            _groups[idx] = g.copy(memberIds = g.memberIds - personId)
        }
    }

    fun setGroupMembers(groupId: Long, memberIds: List<Long>) {
        val idx = _groups.indexOfFirst { it.id == groupId }
        if (idx != -1) {
            _groups[idx] = _groups[idx].copy(memberIds = memberIds.distinct())
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    fun searchGroups(query: String): List<Group> {
        if (query.isBlank()) return _groups.toList()
        val q = query.lowercase()
        return _groups.filter { it.name.lowercase().contains(q) }
    }
}
