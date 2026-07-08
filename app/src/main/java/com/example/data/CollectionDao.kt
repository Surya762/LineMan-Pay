package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection_records ORDER BY timestamp DESC")
    fun getAllCollections(): Flow<List<CollectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(record: CollectionRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<CollectionRecord>)

    @Query("DELETE FROM collection_records WHERE id = :id")
    suspend fun deleteCollectionById(id: Int)

    @Query("SELECT COUNT(*) FROM collection_records")
    suspend fun getCount(): Int
}
