package id.antasari.uts.kampuscare

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.antasari.uts.kampuscare.databinding.ActivityFormBinding
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.FormState
import id.antasari.uts.kampuscare.ui.FormViewModel
import id.antasari.uts.kampuscare.ui.aturAreaAman
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var sudahIsi = false
    private val idCatatan get() = intent.getLongExtra(EXTRA_ID, 0L)

    private val vm: FormViewModel by viewModels {
        AppViewModelFactory(application as KampusCareApp, idCatatan)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturAreaAman(binding.formRoot, sertakanBottomBar = true)

        sudahIsi = savedInstanceState?.getBoolean(KEY_TERISI) ?: false

        binding.tvJudulForm.setText(
            if (idCatatan == 0L) R.string.judul_form_tambah else R.string.judul_form_ubah
        )
        binding.tvPetunjukForm.setText(
            if (idCatatan == 0L) R.string.info_form_tambah else R.string.info_form_ubah
        )

        binding.btnSimpanForm.setOnClickListener { validasiDanSimpan() }
        binding.btnKembaliForm.setOnClickListener {
            if (!vm.ui.value.sibuk) finish()
        }

        onBackPressedDispatcher.addCallback(this) {
            if (!vm.ui.value.sibuk) finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state -> tampilkan(state) }
            }
        }
    }

    private fun tampilkan(state: FormState) {
        if (state.selesai) {
            val pesanHasil = if (idCatatan == 0L) {
                "Catatan baru berhasil disimpan ke perangkat."
            } else {
                "Perubahan catatan berhasil disimpan."
            }
            setResult(
                RESULT_OK,
                Intent().putExtra(EXTRA_PESAN_HASIL, pesanHasil)
            )
            finish()
            return
        }

        if (!state.memuat && state.dapatEdit && !sudahIsi) {
            state.data?.let { data ->
                binding.etJudul.setText(data.judul)
                binding.etLokasi.setText(data.lokasi)
                binding.etDeskripsi.setText(data.deskripsi)
                val opsi = resources.getStringArray(R.array.kategori_fasilitas)
                binding.spinnerKategori.setSelection(
                    opsi.indexOf(data.kategori).coerceAtLeast(0)
                )
            }
            sudahIsi = true
        }

        val aktif = !state.memuat && state.dapatEdit && !state.sibuk
        binding.etJudul.isEnabled = aktif
        binding.spinnerKategori.isEnabled = aktif
        binding.etLokasi.isEnabled = aktif
        binding.etDeskripsi.isEnabled = aktif
        binding.btnSimpanForm.isEnabled = aktif
        binding.btnKembaliForm.isEnabled = !state.sibuk

        binding.tvStatusForm.text = when {
            state.memuat -> getString(R.string.memuat_data)
            else -> state.pesan ?: getString(R.string.status_lokal)
        }
    }

    private fun validasiDanSimpan() {
        binding.etJudul.error = null
        binding.etLokasi.error = null
        binding.etDeskripsi.error = null

        val judul = binding.etJudul.text.toString().trim()
        val posisiKategori = binding.spinnerKategori.selectedItemPosition
        val lokasi = binding.etLokasi.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()

        when {
            judul.length !in 5..100 -> {
                binding.etJudul.error = getString(R.string.error_judul)
                binding.etJudul.requestFocus()
            }
            posisiKategori <= 0 -> {
                binding.tvStatusForm.setText(R.string.error_kategori)
                binding.spinnerKategori.requestFocus()
            }
            lokasi.length !in 3..80 -> {
                binding.etLokasi.error = getString(R.string.error_lokasi)
                binding.etLokasi.requestFocus()
            }
            deskripsi.length !in 20..500 -> {
                binding.etDeskripsi.error = getString(R.string.error_deskripsi)
                binding.etDeskripsi.requestFocus()
            }
            else -> {
                vm.simpan(
                    judul = judul,
                    kategori = binding.spinnerKategori.selectedItem.toString(),
                    lokasi = lokasi,
                    deskripsi = deskripsi
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_TERISI, sudahIsi)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val EXTRA_ID = "catatan_id"
        const val EXTRA_PESAN_HASIL = "pesan_hasil"
        private const val KEY_TERISI = "form_terisi"
    }
}
