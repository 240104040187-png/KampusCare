package id.antasari.uts.kampuscare.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CatatanDao {
    @Query("SELECT * FROM catatan ORDER BY waktuDibuat DESC, id DESC")
    fun amatiSemua(): Flow<List<CatatanEntity>>

    @Query("SELECT * FROM catatan WHERE id = :id LIMIT 1")
    fun amatiBerdasarkanId(id: Long): Flow<CatatanEntity?>

    @Query("SELECT * FROM catatan WHERE id = :id LIMIT 1")
    suspend fun baca(id: Long): CatatanEntity?

    @Insert
    suspend fun tambah(data: CatatanEntity): Long

    @Update
    suspend fun ubah(data: CatatanEntity): Int

    @Query("DELETE FROM catatan WHERE id = :id")
    suspend fun hapus(id: Long): Int
}
