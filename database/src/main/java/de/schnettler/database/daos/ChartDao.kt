package de.schnettler.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListTrack
import kotlinx.coroutines.flow.Flow

@Dao
@Suppress("MaxLineLength")
abstract class ChartDao : BaseDao<TopListEntry> {
    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :entityType AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopArtists(entityType: EntityType = EntityType.ARTIST, listType: ListType): Flow<List<TopListArtist>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :type AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopTracks(type: EntityType = EntityType.TRACK, listType: ListType = ListType.USER): Flow<List<TopListTrack>>

    @Transaction
    @Query("SELECT * FROM toplist WHERE entityType = :type AND listType = :listType ORDER BY `index` ASC")
    abstract fun getTopAlbums(type: EntityType = EntityType.ALBUM, listType: ListType = ListType.USER): Flow<List<TopListAlbum>>

    @Query("SELECT * FROM artists INNER JOIN toplist ON artists.id = toplist.id WHERE listType = 'USER' AND imageUrl is NULL ")
    abstract suspend fun getArtistsWithoutImages(): List<LastFmEntity.Artist>

    @Query("SELECT * FROM tracks INNER JOIN toplist ON tracks.id = toplist.id WHERE listType = 'USER' AND imageUrl is NULL ")
    abstract suspend fun getTracksWithoutImages(): List<LastFmEntity.Track>
}