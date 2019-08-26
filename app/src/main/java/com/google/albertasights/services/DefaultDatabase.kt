package com.google.albertasights.services

import android.content.Context
import androidx.room.*

@Database(entities = [DataToSave::class], version = 1, exportSchema = false)
abstract class DefaultDatabase: RoomDatabase() {

    abstract fun cacheDataDao(): DaoActions

    companion object {
        private var INSTANCE: DefaultDatabase? = null

        fun getDatabase(context: Context): DefaultDatabase? {
            if (INSTANCE == null) {
                synchronized(DefaultDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, DefaultDatabase::class.java, "database.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

@Entity(tableName = "cache")
data class DataToSave(@PrimaryKey var key: String, @ColumnInfo(name = "value") var value: String)

@Dao
interface DaoActions {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: DataToSave)

    @Query("SELECT * FROM cache WHERE [key] = :key")
    fun get(key: String): DataToSave?

    @Query("DELETE FROM cache")
    fun deleteAll()
}