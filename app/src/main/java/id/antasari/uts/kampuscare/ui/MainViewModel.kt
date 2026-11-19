package id.antasari.uts.kampuscare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.antasari.uts.kampuscare.data.CatatanEntity
import id.antasari.uts.kampuscare.data.CatatanRepository
import id.antasari.uts.kampuscare.data.CatatanTidakAdaException
import id.antasari.uts.kampuscare.data.PengaturanPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UtamaState(
    val memuat: Boolean = true,
    val data: List<CatatanEntity> = emptyList(),
    val terbaru: Boolean = true,
    val preferensiSibuk: Boolean = false,
    val hapusSibuk: Boolean = false,
    val pesanFeedback: String? = null,
    val pesanPreferensi: String? = null,
    val gagal: Boolean = false
)

class MainViewModel(
    private val repo: CatatanRepository,
    private val prefs: PengaturanPreferences
) : ViewModel() {

    private val _ui = MutableStateFlow(UtamaState())
    val ui = _ui.asStateFlow()

    private val terbaru = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            try {
                terbaru.value = prefs.bacaTerbaru()
                combine(repo.catatan, terbaru) { list, urutTerbaru ->
                    if (urutTerbaru) {
                        list.sortedWith(
                            compareByDescending<CatatanEntity> { it.waktuDibuat }
                                .thenByDescending { it.id }
                        )
                    } else {
                        list.sortedWith(
                            compareBy<CatatanEntity> { it.waktuDibuat }
                                .thenBy { it.id }
                        )
                    }
                }.collect { terurut ->
                    _ui.update {
                        it.copy(
                            memuat = false,
                            data = terurut,
                            terbaru = terbaru.value,
                            gagal = false
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        memuat = false,
                        gagal = true,
                        pesanFeedback = "Gagal membaca data catatan. Buka ulang aplikasi."
                    )
                }
            }
        }
    }

    fun amatiCatatan(id: Long): Flow<CatatanEntity?> = repo.amatiBerdasarkanId(id)

    fun setPesanFeedback(pesan: String?) {
        _ui.update { it.copy(pesanFeedback = pesan) }
    }

    fun aturUrutan(aktif: Boolean) {
        if (_ui.value.memuat || _ui.value.preferensiSibuk) return
        _ui.update { it.copy(preferensiSibuk = true, pesanPreferensi = "Menyimpan preferensi...") }
        viewModelScope.launch {
            try {
                check(prefs.simpanTerbaru(aktif))
                terbaru.value = aktif
                _ui.update {
                    it.copy(
                        terbaru = aktif,
                        pesanPreferensi = "Preferensi urutan berhasil disimpan."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.update {
                    it.copy(pesanPreferensi = "Gagal menyimpan preferensi urutan.")
                }
            } finally {
                _ui.update { it.copy(preferensiSibuk = false) }
            }
        }
    }

    fun hapusCatatan(id: Long, onBerhasil: () -> Unit) {
        if (_ui.value.hapusSibuk) return
        _ui.update { it.copy(hapusSibuk = true) }
        viewModelScope.launch {
            try {
                repo.hapus(id)
                _ui.update {
                    it.copy(pesanFeedback = "Catatan berhasil dihapus dari perangkat.")
                }
                onBerhasil()
            } catch (e: CancellationException) {
                throw e
            } catch (e: CatatanTidakAdaException) {
                _ui.update {
                    it.copy(pesanFeedback = "Catatan tidak ditemukan atau sudah dihapus.")
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(pesanFeedback = "Gagal menghapus catatan. Coba lagi.")
                }
            } finally {
                _ui.update { it.copy(hapusSibuk = false) }
            }
        }
    }
}
