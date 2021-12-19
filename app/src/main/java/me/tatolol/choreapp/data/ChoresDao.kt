package me.tatolol.choreapp.data

import androidx.room.*

@Dao
interface ChoresDao {
    @Insert
    suspend fun insert(chores: Chores)

    @Query("SELECT * FROM Chores")
    suspend fun selectAll(): List<Chores>

    @Query("SELECT * FROM Chores WHERE id =:id")
    suspend fun select(id: Int): List<Chores>

    @Update
    suspend fun update(chores: Chores)

    @Delete
    suspend fun delete(chores: Chores)

}