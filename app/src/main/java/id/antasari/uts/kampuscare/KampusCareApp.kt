package id.antasari.uts.kampuscare

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import id.antasari.uts.kampuscare.data.CatatanRepository
import id.antasari.uts.kampuscare.data.KampusCareDatabase
import id.antasari.uts.kampuscare.data.PengaturanPreferences

class KampusCareApp : Application() {
    private val database by lazy {
        Room.databaseBuilder<KampusCareDatabase>(this, "kampuscare.db")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    val repository by lazy { CatatanRepository(database.catatanDao()) }
    val preferences by lazy { PengaturanPreferences(this) }
}
