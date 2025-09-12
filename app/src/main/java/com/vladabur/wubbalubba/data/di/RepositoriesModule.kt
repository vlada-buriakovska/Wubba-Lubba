package com.vladabur.wubbalubba.data.di

import com.vladabur.wubbalubba.data.repositories.CharacterRepositoryImpl
import com.vladabur.wubbalubba.domain.repositories.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {
    @Binds
    fun bindCharactersRepository(
        characterRepository: CharacterRepositoryImpl
    ): CharacterRepository
}