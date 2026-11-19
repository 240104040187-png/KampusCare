package id.antasari.uts.kampuscare.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "catatan")
data class CatatanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val judul: String,
    val kategori: String,
    val lokasi: String,
    val deskripsi: String,
    val waktuDibuat: Long
)
