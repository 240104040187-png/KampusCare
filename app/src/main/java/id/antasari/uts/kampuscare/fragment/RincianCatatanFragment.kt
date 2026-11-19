package id.antasari.uts.kampuscare.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.antasari.uts.kampuscare.KampusCareApp
import id.antasari.uts.kampuscare.MainActivity
import id.antasari.uts.kampuscare.R
import id.antasari.uts.kampuscare.databinding.FragmentRincianBinding
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.MainViewModel
import id.antasari.uts.kampuscare.ui.formatWaktuLokal
import kotlinx.coroutines.launch

class RincianCatatanFragment : Fragment() {

    private var _binding: FragmentRincianBinding? = null
    private val binding get() = _binding!!

    private val catatanId: Long
        get() = arguments?.getLong(ARG_ID, -1L) ?: -1L

    private val vm: MainViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application as KampusCareApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRincianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnKembaliRincian.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnUbahRincian.setOnClickListener {
            if (catatanId > 0L) {
                (activity as? MainActivity)?.bukaFormCatatan(catatanId)
            }
        }

        binding.btnHapusRincian.setOnClickListener {
            if (catatanId <= 0L) return@setOnClickListener
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.konfirmasi_hapus)
                .setPositiveButton(R.string.ya_hapus) { _, _ ->
                    vm.hapusCatatan(catatanId) {
                        if (isAdded) {
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
                .setNegativeButton(R.string.batal, null)
                .show()
        }

        if (catatanId <= 0L) {
            tampilkanTidakDitemukan()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.amatiCatatan(catatanId).collect { data ->
                    if (data == null) {
                        tampilkanTidakDitemukan()
                    } else {
                        binding.tvStatusRincian.isVisible = false
                        binding.cardDetail.isVisible = true
                        binding.btnUbahRincian.isEnabled = true
                        binding.btnHapusRincian.isEnabled = true

                        binding.tvRincianKategori.text = data.kategori
                        binding.tvRincianId.text = getString(R.string.label_id_catatan, data.id)
                        binding.tvRincianJudul.text = data.judul
                        binding.tvRincianLokasi.text = data.lokasi
                        binding.tvRincianWaktu.text = getString(
                            R.string.item_waktu_format,
                            formatWaktuLokal(data.waktuDibuat)
                        )
                        binding.tvRincianDeskripsi.text = data.deskripsi
                    }
                }
            }
        }
    }

    private fun tampilkanTidakDitemukan() {
        binding.tvStatusRincian.setText(R.string.catatan_tidak_ditemukan)
        binding.tvStatusRincian.isVisible = true
        binding.cardDetail.isVisible = false
        binding.btnUbahRincian.isEnabled = false
        binding.btnHapusRincian.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID = "catatan_id"

        fun newInstance(id: Long): RincianCatatanFragment {
            return RincianCatatanFragment().apply {
                arguments = bundleOf(ARG_ID to id)
            }
        }
    }
}
