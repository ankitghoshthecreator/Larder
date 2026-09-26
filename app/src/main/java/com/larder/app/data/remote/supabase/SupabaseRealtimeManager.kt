package com.larder.app.data.remote.supabase

import com.larder.app.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class RealtimeItemEvent {
    data class Inserted(val item: Item) : RealtimeItemEvent()
    data class Updated(val item: Item) : RealtimeItemEvent()
    data class Deleted(val itemId: String) : RealtimeItemEvent()
}

class SupabaseRealtimeManager {

    private val _realtimeEvents = MutableSharedFlow<RealtimeItemEvent>()
    val realtimeEvents: Flow<RealtimeItemEvent> = _realtimeEvents.asSharedFlow()

    private var activeChannelHouseholdId: String? = null

    fun subscribeToHouseholdChannel(householdId: String) {
        activeChannelHouseholdId = householdId
    }

    fun unsubscribe() {
        activeChannelHouseholdId = null
    }

    suspend fun emitSimulatedRealtimeEvent(event: RealtimeItemEvent) {
        _realtimeEvents.emit(event)
    }
}
