package de.schnettler.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import de.schnettler.database.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM session LIMIT 1")
    fun getSession(): LiveData<Session?>

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)
}


@Dao
interface TopListDao {
    @Transaction
    @Query("SELECT * FROM table_charts WHERE type = :type ORDER BY `index` ASC")
    fun getTopArtists(type: String): Flow<List<ListEntryWithArtist>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopList(entries: List<ListEntry>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artist: List<Artist>)

    @Transaction
    suspend fun insertTopArtists(artistEntry: List<ListEntryWithArtist>) {
        insertTopList(artistEntry.map { it.listing })
        insertArtists(artistEntry.map { it.artist })
    }
}