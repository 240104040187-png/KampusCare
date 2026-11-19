package id.antasari.uts.kampuscare.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun aturAreaAman(root: View, sertakanBottomBar: Boolean = true) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.displayCutout()
        )
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val bottom = if (sertakanBottomBar) maxOf(bars.bottom, ime.bottom) else ime.bottom
        view.setPadding(bars.left, bars.top, bars.right, bottom)
        insets
    }
    ViewCompat.requestApplyInsets(root)
}

fun formatWaktuLokal(epochMillis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
    return formatter.format(Date(epochMillis))
}
