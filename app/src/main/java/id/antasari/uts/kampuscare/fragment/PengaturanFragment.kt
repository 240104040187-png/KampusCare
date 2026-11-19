package id.antasari.uts.kampuscare.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.antasari.uts.kampuscare.KampusCareApp
import id.antasari.uts.kampuscare.MainActivity
import id.antasari.uts.kampuscare.R
import id.antasari.uts.kampuscare.databinding.FragmentPengaturanBinding
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.MainViewModel
import kotlinx.coroutines.launch

class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application as KampusCareApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnUjiRincianInvalid.setOnClickListener {
            (activity as? MainActivity)?.bukaRincianCatatan(-99L)
        }

        binding.btnUjiFormInvalid.setOnClickListener {
            (activity as? MainActivity)?.bukaFormCatatan(-99L)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    binding.rgUrutan.setOnCheckedChangeListener(null)
                    if (state.terbaru) {
                        binding.rbTerbaru.isChecked = true
                    } else {
                        binding.rbTerlama.isChecked = true
                    }
                    binding.rbTerbaru.isEnabled = !state.memuat && !state.preferensiSibuk
                    binding.rbTerlama.isEnabled = !state.memuat && !state.preferensiSibuk

                    binding.tvStatusPreferensi.text = state.pesanPreferensi
                        ?: if (state.terbaru) getString(R.string.urutan_terbaru_label)
                        else getString(R.string.urutan_terlama_label)

                    binding.rgUrutan.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.rbTerbaru -> vm.aturUrutan(true)
                            R.id.rbTerlama -> vm.aturUrutan(false)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
