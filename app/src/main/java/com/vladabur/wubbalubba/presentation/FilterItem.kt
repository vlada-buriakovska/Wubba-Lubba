package com.vladabur.wubbalubba.presentation

data class FilterItem(
    val label: String,
    val isEnabled: Boolean
){
    fun toggleEnabled(): FilterItem {
        return this.copy(isEnabled = !this.isEnabled)
    }
}