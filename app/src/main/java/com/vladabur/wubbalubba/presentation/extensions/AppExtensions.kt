package com.vladabur.wubbalubba.presentation.extensions

import com.vladabur.wubbalubba.presentation.FilterItem

fun List<FilterItem>.toggleFilterItem(item: FilterItem): List<FilterItem> {
    return map { filterItem ->
        if (filterItem == item) {
            filterItem.toggleEnabled()
        } else {
            filterItem
        }
    }
}