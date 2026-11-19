package id.antasari.uts.kampuscare.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import id.antasari.uts.kampuscare.KampusCareApp
import id.antasari.uts.kampuscare.MainActivity
import id.antasari.uts.kampuscare.R
import id.antasari.uts.kampuscare.adapter.CatatanAdapter
import id.antasari.uts.kampuscare.databinding.FragmentDaftarBinding
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.MainViewModel
import kotlinx.coroutines.launch

class DaftarCatatanFragment : Fragment() {

    private var _binding: FragmentDaftarBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application as KampusCareApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CatatanAdapter { id ->
            vm.setPesanFeedback(null)
            (activity as? MainActivity)?.bukaRincianCatatan(id)
        }

        binding.rvCatatan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCatatan.adapter = adapter

        val aksiTambah = View.OnClickListener {
            vm.setPesanFeedback(null)
            (activity as? MainActivity)?.bukaFormCatatan(0L)
        }
        binding.btnTambahDaftar.setOnClickListener(aksiTambah)
        binding.btnTambahKosong.setOnClickListener(aksiTambah)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    adapter.submitList(state.data)

                    binding.tvInfoUrutan.setText(
                        if (state.terbaru) R.string.urutan_terbaru_label
                        else R.string.urutan_terlama_label
                    )

                    binding.tvStatusDaftar.text = when {
                        state.memuat -> getString(R.string.memuat_data)
                        state.gagal -> state.pesanFeedback
                        else -> getString(R.string.jumlah_catatan_format, state.data.size)
                    }

                    val tampilKosong = !state.memuat && !state.gagal && state.data.isEmpty()
                    binding.layoutKosong.isVisible = tampilKosong
                    binding.rvCatatan.isVisible = !tampilKosong
                    binding.btnTambahDaftar.isVisible = !tampilKosong

                    binding.tvFeedbackDaftar.text = state.pesanFeedback.orEmpty()
                    binding.tvFeedbackDaftar.isVisible = !state.gagal && state.pesanFeedback != null
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
