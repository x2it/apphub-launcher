package com.apphub.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM pinned_entries ORDER BY orderIdx ASC, name ASC")
    fun observeAll(): Flow<List<EntryEntity>>

    @Query("SELECT * FROM pinned_entries ORDER BY orderIdx ASC, name ASC")
    suspend fun getAll(): List<EntryEntity>

    @Query("SELECT * FROM pinned_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): EntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EntryEntity)

    @Update
    suspend fun update(entity: EntryEntity)

    @Query("DELETE FROM pinned_entries WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM pinned_entries")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<EntryEntity>)
}
