package com.vladabur.wubbalubba.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vladabur.wubbalubba.data.database.entities.CharacterEntity

@Dao
interface CharacterDao {

    @Query(
        """
        SELECT * FROM ${CharacterEntity.ENTITY_NAME}
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:status IS NULL OR status = :status)
        AND (:species IS NULL OR species = :species)
        AND (:gender IS NULL OR gender = :gender)
        ORDER BY id ASC
        LIMIT 20
        OFFSET :page*20
    """
    )
    fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        gender: String?
    ): List<CharacterEntity>

    @Query(
        """
        SELECT COUNT(*) FROM ${CharacterEntity.ENTITY_NAME}
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
          AND (:status IS NULL OR status = :status)
          AND (:species IS NULL OR species = :species)
          AND (:gender IS NULL OR gender = :gender)
    """
    )
    suspend fun countCharacters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ): Int

    @Query("SELECT * FROM ${CharacterEntity.ENTITY_NAME} WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM ${CharacterEntity.ENTITY_NAME}")
    suspend fun clearAll()
}