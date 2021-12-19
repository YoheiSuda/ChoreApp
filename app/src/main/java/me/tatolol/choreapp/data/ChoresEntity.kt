package me.tatolol.choreapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.tatolol.choreapp.model.*
import java.text.DateFormat
import java.util.*

@Entity(tableName = TABLE_NAME)
data class Chores (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = KEY_CHORE_NAME) var choreName: String?,
    @ColumnInfo(name = KEY_CHORE_ASSIGNED_BY) var choreAssignedBy: String?,
    @ColumnInfo(name = KEY_CHORE_ASSIGNED_TO) var choreAssignedTo: String?,
    @ColumnInfo(name = KEY_CHORE_ASSIGNED_TIME) var choreAssignedTime: Long?
) {
    fun showHumanDate(timeAssigned: Long): String {

        var dateFormat: java.text.DateFormat = DateFormat.getDateInstance()
        var formattedDate: String = dateFormat.format(Date(timeAssigned).time)

        return "${formattedDate}"
    }
}