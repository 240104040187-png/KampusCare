package id.antasari.uts.kampuscare

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import id.antasari.uts.kampuscare.databinding.ActivityMainBinding
import id.antasari.uts.kampuscare.fragment.BerandaFragment
import id.antasari.uts.kampuscare.fragment.DaftarCatatanFragment
import id.antasari.uts.kampuscare.fragment.PengaturanFragment
import id.antasari.uts.kampuscare.fragment.RincianCatatanFragment
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.MainViewModel
import id.antasari.uts.kampuscare.ui.aturAreaAman

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels {
        AppViewModelFactory(application as KampusCareApp)
    }

    private val formLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val pesan = result.data?.getStringExtra(FormActivity.EXTRA_PESAN_HASIL)
                ?: "Catatan berhasil disimpan ke perangkat."
            vm.setPesanFeedback(pesan)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aturAreaAman(binding.mainRoot, sertakanBottomBar = false)

        if (savedInstanceState == null) {
            gantiTabUtama(BerandaFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            supportFragmentManager.popBackStack(
                BACKSTACK_RINCIAN,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            when (item.itemId) {
                R.id.menu_beranda -> {
                    gantiTabUtama(BerandaFragment())
                    true
                }
                R.id.menu_daftar -> {
                    gantiTabUtama(DaftarCatatanFragment())
                    true
                }
                R.id.menu_pengaturan -> {
                    gantiTabUtama(PengaturanFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun gantiTabUtama(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun pindahKeTabDaftar() {
        binding.bottomNav.selectedItemId = R.id.menu_daftar
    }

    fun bukaRincianCatatan(id: Long) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RincianCatatanFragment.newInstance(id))
            .addToBackStack(BACKSTACK_RINCIAN)
            .commit()
    }

    fun bukaFormCatatan(id: Long) {
        val intent = Intent(this, FormActivity::class.java)
            .putExtra(FormActivity.EXTRA_ID, id)
        formLauncher.launch(intent)
    }

    companion object {
        private const val BACKSTACK_RINCIAN = "rincian_catatan"
    }
}
