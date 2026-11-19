package id.antasari.uts.kampuscare.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [CatatanEntity::class],
    version = 1,
    exportSchema = true
)
abstract class KampusCareDatabase : RoomDatabase() {
    abstract fun catatanDao(): CatatanDao
}
