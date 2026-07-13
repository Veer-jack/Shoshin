package com.example.shoshinapp.sync

import com.example.shoshinapp.data.db.entities.ReflectionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ConflictResolution {
    data class UseLocal(val local: ReflectionEntity) : ConflictResolution()
    data class UseRemote(val remote: ReflectionEntity) : ConflictResolution()
    data class Merge(val merged: ReflectionEntity) : ConflictResolution()
}

class ConflictResolver {
    
    private val _conflictDialog = MutableStateFlow<ReflectionConflict?>(null)
    val conflictDialog: StateFlow<ReflectionConflict?> = _conflictDialog

    data class ReflectionConflict(
        val local: ReflectionEntity,
        val remote: ReflectionEntity,
        val onResolved: (ConflictResolution) -> Unit
    )

    fun resolveReflectionConflict(
        local: ReflectionEntity,
        remote: ReflectionEntity,
        onResolved: (ConflictResolution) -> Unit
    ) {
        _conflictDialog.value = ReflectionConflict(
            local = local,
            remote = remote,
            onResolved = onResolved
        )
    }

    fun resolveWithLocal(local: ReflectionEntity) {
        _conflictDialog.value?.onResolved?.invoke(ConflictResolution.UseLocal(local))
        _conflictDialog.value = null
    }

    fun resolveWithRemote(remote: ReflectionEntity) {
        _conflictDialog.value?.onResolved?.invoke(ConflictResolution.UseRemote(remote))
        _conflictDialog.value = null
    }

    fun resolveWithMerge(local: ReflectionEntity, remote: ReflectionEntity) {
        val merged = local.copy(
            content = "${local.content}\n\n[Merged with cloud version]\n\n${remote.content}",
            timestamp = System.currentTimeMillis(),
            version = maxOf(local.version, remote.version) + 1
        )
        _conflictDialog.value?.onResolved?.invoke(ConflictResolution.Merge(merged))
        _conflictDialog.value = null
    }

    fun cancelConflict() {
        _conflictDialog.value = null
    }
}