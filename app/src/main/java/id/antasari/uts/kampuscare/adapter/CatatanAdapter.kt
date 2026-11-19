package id.antasari.uts.kampuscare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.antasari.uts.kampuscare.R
import id.antasari.uts.kampuscare.data.CatatanEntity
import id.antasari.uts.kampuscare.databinding.ItemCatatanBinding
import id.antasari.uts.kampuscare.ui.formatWaktuLokal

class CatatanAdapter(private val onPilih: (Long) -> Unit) :
    ListAdapter<CatatanEntity, CatatanAdapter.Holder>(Pembanding) {

    class Holder(private val b: ItemCatatanBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(data: CatatanEntity, onPilih: (Long) -> Unit) {
            val waktuTeks = formatWaktuLokal(data.waktuDibuat)
            b.tvItemKategori.text = b.root.context.getString(
                R.string.item_meta_format,
                data.id,
                data.kategori
            )
            b.tvItemJudul.text = data.judul
            b.tvItemLokasi.text = data.lokasi
            b.tvItemWaktu.text = b.root.context.getString(
                R.string.item_waktu_format,
                waktuTeks
            )
            b.root.contentDescription = b.root.context.getString(
                R.string.item_ally_format,
                data.judul,
                data.kategori,
                data.lokasi,
                waktuTeks
            )
            b.root.setOnClickListener { onPilih(data.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemCatatanBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), onPilih)
    }

    companion object {
        private val Pembanding = object : DiffUtil.ItemCallback<CatatanEntity>() {
            override fun areItemsTheSame(a: CatatanEntity, b: CatatanEntity) =
                a.id == b.id

            override fun areContentsTheSame(a: CatatanEntity, b: CatatanEntity) =
                a == b
        }
    }
}
