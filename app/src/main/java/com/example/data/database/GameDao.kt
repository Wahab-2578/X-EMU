package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY lastPlayed DESC, dateAdded DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: Long): Flow<Game?>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameByIdDirect(id: Long): Game?

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGameCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Query("UPDATE games SET lastPlayed = :timestamp WHERE id = :id")
    suspend fun updateLastPlayed(id: Long, timestamp: Long)

    @Query("UPDATE games SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, favorite: Boolean)
}
