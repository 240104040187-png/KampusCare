package id.antasari.uts.kampuscare.data

class CatatanTidakAdaException : Exception("Catatan tidak ditemukan.")

class CatatanRepository(private val dao: CatatanDao) {
    val catatan = dao.amatiSemua()

    private val kategoriValid = setOf(
        "Penerangan",
        "Kebersihan",
        "Perabot",
        "Jaringan",
        "Lainnya"
    )

    fun amatiBerdasarkanId(id: Long) = dao.amatiBerdasarkanId(id)

    suspend fun baca(id: Long): CatatanEntity? =
        if (id > 0L) dao.baca(id) else null

    suspend fun simpan(
        id: Long,
        judulInput: String,
        kategoriInput: String,
        lokasiInput: String,
        deskripsiInput: String
    ): Long {
        require(id >= 0L) { "ID catatan tidak valid." }

        val judul = judulInput.trim()
        val lokasi = lokasiInput.trim()
        val deskripsi = deskripsiInput.trim()

        require(judul.length in 5..100) {
            "Judul wajib diisi 5 hingga 100 karakter."
        }
        require(kategoriInput in kategoriValid) {
            "Pilih kategori fasilitas yang valid."
        }
        require(lokasi.length in 3..80) {
            "Lokasi wajib diisi 3 hingga 80 karakter."
        }
        require(deskripsi.length in 20..500) {
            "Deskripsi wajib diisi 20 hingga 500 karakter."
        }

        if (id == 0L) {
            val baru = CatatanEntity(
                id = 0L,
                judul = judul,
                kategori = kategoriInput,
                lokasi = lokasi,
                deskripsi = deskripsi,
                waktuDibuat = System.currentTimeMillis()
            )
            return dao.tambah(baru)
        }

        val lama = dao.baca(id) ?: throw CatatanTidakAdaException()
        val diperbarui = lama.copy(
            judul = judul,
            kategori = kategoriInput,
            lokasi = lokasi,
            deskripsi = deskripsi
        )
        if (dao.ubah(diperbarui) != 1) throw CatatanTidakAdaException()
        return id
    }

    suspend fun hapus(id: Long) {
        require(id > 0L) { "ID catatan tidak valid." }
        if (dao.hapus(id) != 1) throw CatatanTidakAdaException()
    }
}
