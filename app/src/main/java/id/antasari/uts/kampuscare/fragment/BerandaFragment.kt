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
import id.antasari.uts.kampuscare.databinding.FragmentBerandaBinding
import id.antasari.uts.kampuscare.ui.AppViewModelFactory
import id.antasari.uts.kampuscare.ui.MainViewModel
import kotlinx.coroutines.launch

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application as KampusCareApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambahBeranda.setOnClickListener {
            (activity as? MainActivity)?.bukaFormCatatan(0L)
        }

        binding.btnLihatDaftar.setOnClickListener {
            (activity as? MainActivity)?.pindahKeTabDaftar()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.ui.collect { state ->
                    binding.tvJumlahCatatan.text = state.data.size.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
