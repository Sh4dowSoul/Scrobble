package de.schnettler.database

import androidx.room.TypeConverter
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.ListType
import de.schnettler.database.models.ScrobbleStatus

class TypeConverter {

    private val typeMap = ListType.values().associateBy(ListType::name)
    private val topListingTypeMap = EntityType.values().associateBy(EntityType::name)
    private val scrobbleStatusTypeMap = ScrobbleStatus.values().associateBy(ScrobbleStatus::name)

    @TypeConverter
    fun chartTypeToString(type: ListType) = type.name

    @TypeConverter
    fun stringToChartType(string: String): ListType = typeMap[string] ?: ListType.UNDEF

    @TypeConverter
    fun topListingTypeToString(type: EntityType) = type.name

    @TypeConverter
    fun stringToTopListingType(id: String): EntityType =
        topListingTypeMap[id] ?: EntityType.UNDEF

    @TypeConverter
    fun stringToList(value: String) = value.split(",").map { it }.filter { it.isNotBlank() }

    @TypeConverter
    fun listToString(list: List<String>) = list.joinToString(",")

    @TypeConverter
    fun scrobbleStatusToString(status: ScrobbleStatus) = status.name

    @TypeConverter
    fun stringToScrobbleStatus(name: String) =
        scrobbleStatusTypeMap[name] ?: ScrobbleStatus.VOLATILE
}