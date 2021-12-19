package me.tatolol.choreapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.tatolol.choreapp.model.DATABASE_NAME
import me.tatolol.choreapp.model.DATABASE_VERSION

@Database(entities = [Chores::class], version = DATABASE_VERSION)
abstract class ChoresDatabase : RoomDatabase() {
    abstract fun choresDao(): ChoresDao

    companion object {
        private var instance: ChoresDatabase? = null

        fun getInstance(context: Context): ChoresDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, ChoresDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return requireNotNull(instance)
        }
    }
}