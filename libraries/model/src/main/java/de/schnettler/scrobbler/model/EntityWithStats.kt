package de.schnettler.scrobbler.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

sealed class EntityWithStats(
    @Ignore open val entity: LastFmEntity,
    @Ignore open val stats: Stats
) : BaseEntity {

    data class AlbumWithStats(
        @Embedded override val entity: LastFmEntity.Album,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats
    ) : EntityWithStats(entity, stats)

    data class TrackWithStats(
        @Embedded override val entity: LastFmEntity.Track,
        @Relation(parentColumn = "id", entityColumn = "id") override val stats: Stats,
    ) : EntityWithStats(entity, stats)
}