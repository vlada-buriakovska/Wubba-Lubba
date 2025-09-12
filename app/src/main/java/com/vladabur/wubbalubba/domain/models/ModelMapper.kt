package com.vladabur.wubbalubba.domain.models

fun interface ModelMapper<in FROM, out INTO> {
    fun map(model: FROM): INTO
}