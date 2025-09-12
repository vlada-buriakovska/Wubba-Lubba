package com.vladabur.wubbalubba.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vladabur.wubbalubba.data.database.converters.Converters
import com.vladabur.wubbalubba.data.database.dao.CharacterDao
import com.vladabur.wubbalubba.data.database.entities.CharacterEntity

@Database(
    entities = [CharacterEntity::class],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "wbbalubba.db"
    }

    abstract fun characterDao(): CharacterDao
}