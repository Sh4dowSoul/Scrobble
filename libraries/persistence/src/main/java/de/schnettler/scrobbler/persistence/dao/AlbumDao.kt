package de.schnettler.scrobbler.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import de.schnettler.scrobbler.model.LastFmEntity.Album

@Dao
abstract class AlbumDao : BaseDao<Album> {
    @Query("SELECT * FROM albums WHERE name = :name and artist = :artist")
    abstract fun getAlbumByName(name: String, artist: String): Album?
}