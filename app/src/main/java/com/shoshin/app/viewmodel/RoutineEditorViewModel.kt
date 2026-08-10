package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.RoutineRepository
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.data.db.entities.RoutineCheckpointEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutineEditorViewModel(
    private val routineRepository: RoutineRepository,
    shoshinRepository: ShoshinRepository,
    private val userId: String?
) : ViewModel() {

    val templateKey: StateFlow<String> = shoshinRepository.template
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "walk")

    private val _checkpoints = MutableStateFlow<List<RoutineCheckpointEntity>>(emptyList())
    val checkpoints: StateFlow<List<RoutineCheckpointEntity>> = _checkpoints.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        viewModelScope.launch {
            templateKey.collectLatest { key ->
                val uid = userId ?: return@collectLatest
                routineRepository.ensureSeeded(uid, key)
                routineRepository.getRoutineFlow(uid, key).collectLatest { rows ->
                    _checkpoints.value = rows.sortedBy { it.displayOrder }
                }
            }
        }
    }

    fun updateLabel(slotIndex: Int, newLabel: String) {
        _saved.value = false
        _checkpoints.value = _checkpoints.value.map {
            if (it.slotIndex == slotIndex) it.copy(label = newLabel) else it
        }
    }

    fun moveUp(displayOrder: Int) = swap(displayOrder, displayOrder - 1)

    fun moveDown(displayOrder: Int) = swap(displayOrder, displayOrder + 1)

    private fun swap(a: Int, b: Int) {
        val current = _checkpoints.value
        val rowA = current.find { it.displayOrder == a } ?: return
        val rowB = current.find { it.displayOrder == b } ?: return
        _saved.value = false
        _checkpoints.value = current.map {
            when (it.slotIndex) {
                rowA.slotIndex -> it.copy(displayOrder = b)
                rowB.slotIndex -> it.copy(displayOrder = a)
                else -> it
            }
        }
    }

    fun save() {
        val uid = userId ?: return
        val key = templateKey.value
        viewModelScope.launch {
            routineRepository.saveRoutine(uid, key, _checkpoints.value)
            _saved.value = true
        }
    }
}
