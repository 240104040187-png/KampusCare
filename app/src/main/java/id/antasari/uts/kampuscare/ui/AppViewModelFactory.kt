package id.antasari.uts.kampuscare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.antasari.uts.kampuscare.KampusCareApp

class AppViewModelFactory(
    private val app: KampusCareApp,
    private val idCatatan: Long = 0L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass == MainViewModel::class.java ->
                MainViewModel(app.repository, app.preferences) as T
            modelClass == FormViewModel::class.java ->
                FormViewModel(app.repository, idCatatan) as T
            else -> throw IllegalArgumentException("ViewModel tidak dikenal.")
        }
    }
}
