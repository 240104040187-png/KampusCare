package id.antasari.uts.kampuscare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.uts.kampuscare.data.CatatanEntity
import id.antasari.uts.kampuscare.data.CatatanRepository
import id.antasari.uts.kampuscare.data.CatatanTidakAdaException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormState(
    val memuat: Boolean = false,
    val data: CatatanEntity? = null,
    val dapatEdit: Boolean = true,
    val sibuk: Boolean = false,
    val pesan: String? = null,
    val selesai: Boolean = false
)

class FormViewModel(
    private val repo: CatatanRepository,
    private val id: Long
) : ViewModel() {

    private val _ui = MutableStateFlow(
        FormState(
            memuat = id != 0L,
            dapatEdit = id == 0L
        )
    )
    val ui = _ui.asStateFlow()

    init {
        if (id != 0L) {
            viewModelScope.launch {
                try {
                    val data = repo.baca(id)
                    _ui.value = FormState(
                        data = data,
                        dapatEdit = data != null,
                        pesan = if (data == null) "Catatan tidak ditemukan." else null
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _ui.value = FormState(
                        dapatEdit = false,
                        pesan = "Gagal membaca catatan dari database."
                    )
                }
            }
        }
    }

    fun simpan(judul: String, kategori: String, lokasi: String, deskripsi: String) {
        if (!_ui.value.dapatEdit || _ui.value.sibuk) return
        _ui.update { it.copy(sibuk = true, pesan = "Menyimpan catatan...") }
        viewModelScope.launch {
            try {
                repo.simpan(id, judul, kategori, lokasi, deskripsi)
                _ui.update { it.copy(selesai = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalArgumentException) {
                _ui.update { it.copy(pesan = e.message ?: "Periksa kembali isian formulir.") }
            } catch (e: CatatanTidakAdaException) {
                _ui.update { it.copy(pesan = "Catatan tidak ditemukan.") }
            } catch (e: Exception) {
                _ui.update { it.copy(pesan = "Gagal menyimpan catatan. Coba lagi.") }
            } finally {
                _ui.update { it.copy(sibuk = false) }
            }
        }
    }
}
