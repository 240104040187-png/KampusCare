# KampusCare — Aplikasi Pencatat Temuan Fasilitas Kampus

Aplikasi Android berbasis penyimpanan lokal (*offline-first*) yang dirancang untuk mencatat, memantau, menyunting, dan mengelola temuan kerusakan atau gangguan fasilitas kampus secara langsung di perangkat pengguna tanpa memerlukan koneksi internet.

---

## 1. Identitas Mahasiswa
* **Nama:** Wahyu Maarif
* **NIM:** 240104040187
* **Kelas:** TI 24 B
* **Package Name:** `id.antasari.uts.kampuscare`

---

## 2. Versi Alat & Teknologi
* **IDE:** Android Studio (Ladybug / Meerkat atau lebih baru)
* **Bahasa Pemrograman:** Kotlin
* **Antarmuka Pengguna (UI):** Jetpack Compose (Material Design 3)
* **Database Lokal:** AndroidX Room Persistence Library (SQLite) + KSP
* **Penyimpanan Preferensi:** SharedPreferences / Preferences DataStore
* **Build System:** Gradle (Kotlin DSL) & Gradle Wrapper
* **Target / Compile SDK:** Android API 35–36 (Minimum SDK API 24)
* **Perangkat Pengujian:** Xiaomi 14T (`2406APNFAG`) — Android 16 (API 36) / Xiaomi HyperOS

---

## 3. Cara Menjalankan Proyek
1. **Clone Repository:**
   ```bash
   git clone <url-repository-anda>
   cd KampusCare
   ```
2. **Buka di Android Studio:**
   * Pilih **File → Open**, arahkan ke folder root proyek `KampusCare`, lalu tunggu hingga proses **Gradle Sync** selesai.
3. **Siapkan Perangkat Uji:**
   * Gunakan perangkat Android fisik dengan **USB Debugging** aktif (atau Android Emulator API 35/36).
4. **Jalankan Aplikasi:**
   * Pilih perangkat pada toolbar atas Android Studio, lalu tekan tombol **Run (`▶`)** atau jalankan melalui terminal menggunakan Gradle Wrapper:
     ```bash
     ./gradlew installDebug
     ```

---

## 4. Fitur Utama
1. **Beranda (Ringkasan Lokal):** Menampilkan jumlah total catatan yang tersimpan di perangkat, indikator penyimpanan lokal, pintasan cepat (*Tambah Catatan* & *Lihat Semua Catatan*), serta panduan alur penggunaan.
2. **Daftar Catatan:** Menampilkan seluruh catatan temuan fasilitas beserta kategori, lokasi, dan nomor ID unik dengan dukungan pengurutan dinamis.
3. **Tambah & Ubah Catatan (CRUD Lokal):**
   * Validasi input wajib sebelum menyimpan: **Judul Temuan** (5–100 karakter), **Kategori Fasilitas**, **Lokasi Temuan** (3–80 karakter), dan **Deskripsi Masalah** (20–500 karakter).
   * Penyuntingan catatan tetap mempertahankan **ID Catatan** dan **waktu pembuatan** awal.
4. **Rincian & Penghapusan Aman:** Menampilkan detail lengkap catatan beserta konfirmasi dialog (*Batal* / *Ya, Hapus*) sebelum penghapusan permanen.
5. **Pengaturan & Ketahanan Sistem:**
   * Pengaturan urutan daftar (*Terbaru lebih dahulu* / *Terlama lebih dahulu*) yang tersimpan persisten.
   * Penanganan aman untuk navigasi ID tidak valid (`-99`) tanpa menyebabkan *crash*.
   * Dukungan orientasi layar *Portrait* dan *Landscape* serta operasional penuh secara offline (*Airplane Mode*).

---

## 5. Struktur Aplikasi
```text
id.antasari.uts.kampuscare/
├── data/
│   ├── local/
│   │   ├── entity/          # Entitas tabel Room (CatatanEntity)
│   │   ├── dao/             # Data Access Object (query CRUD & sorting)
│   │   └── database/        # Konfigurasi RoomDatabase & export skema JSON
│   ├── preferences/         # Penyimpanan preferensi urutan daftar
│   └── repository/          # Abstraksi sumber data lokal
├── ui/
│   ├── navigation/          # Pengaturan rute navigasi & Bottom Navigation Bar
│   ├── screens/
│   │   ├── home/            # Layar 1: Beranda (Ringkasan Catatan Lokal)
│   │   ├── list/            # Layar 2: Daftar Semua Catatan
│   │   ├── form/            # Layar 3: Tambah & Ubah Catatan
│   │   ├── detail/          # Layar 4: Rincian Catatan & Dialog Hapus
│   │   └── settings/        # Layar 5: Pengaturan & Uji ID Tidak Valid
│   ├── components/          # Komponen UI reusable (kartu, tombol, input form)
│   └── theme/               # Konfigurasi warna, tipografi, dan tema Material 3
├── viewmodel/               # State management (UI State & validasi formulir)
└── MainActivity.kt          # Entry point aplikasi
```

*(Pastikan folder `app/schemas/` yang berisi file JSON skema Room, `gradle/wrapper/`, serta folder `screenshots/` ikut di-commit ke repository).*

---

## 6. Ringkasan Hasil Pengujian (U01–U15)
Pengujian fungsional dilakukan pada perangkat **Xiaomi 14T (Android 16)** dalam mode offline:

| Kode | Skenario Pengujian | Status |
| :--- | :--- | :---: |
| **U01** | Kondisi awal kosong pada Beranda (`0` catatan) dan Daftar | **Lulus** |
| **U02** | Validasi formulir gagal saat kolom wajib kosong / di bawah batas karakter | **Lulus** |
| **U03** | Tambah catatan valid pertama ke database lokal | **Lulus** |
| **U04** | Simpan 5 catatan valid dari minimal 3 kategori berbeda | **Lulus** |
| **U05** | Tampilan halaman Rincian Catatan sesuai ID terpilih | **Lulus** |
| **U06** | Ubah catatan tanpa mengubah ID dan waktu pembuatan awal | **Lulus** |
| **U07** | Batal menghapus catatan melalui dialog konfirmasi | **Lulus** |
| **U08** | Konfirmasi hapus catatan permanen dan pembaruan ringkasan Beranda | **Lulus** |
| **U09** | Perubahan preferensi urutan daftar (*Terlama* / *Terbaru*) di Pengaturan | **Lulus** |
| **U10** | Ketahanan data dan pengaturan setelah *Force Stop* aplikasi | **Lulus** |
| **U11** | Rotasi layar (*Portrait* ↔ *Landscape*) pada Beranda & Daftar | **Lulus** |
| **U12** | Rotasi layar pada form Tambah Catatan tanpa kehilangan input | **Lulus** |
| **U13** | Rotasi layar pada form Ubah Catatan tanpa kehilangan data | **Lulus** |
| **U14** | Operasional penuh seluruh fitur CRUD dalam Mode Pesawat (offline) | **Lulus** |
| **U15** | Penanganan aman saat pengujian navigasi ID tidak valid (`-99`) | **Lulus** |

---

## 7. Keterbatasan Aplikasi
1. **Penyimpanan Lokal Tunggal:** Seluruh data hanya tersimpan secara lokal di database SQLite perangkat, sehingga belum mendukung sinkronisasi *cloud* atau pencadangan lintas perangkat.
2. **Belum Mendukung Lampiran Foto:** Laporan temuan fasilitas saat ini hanya berbasis teks (judul, kategori, lokasi, deskripsi) dan belum menyertakan unggahan foto bukti fisik.
3. **Tanpa Manajemen Multi-Pengguna:** Aplikasi belum memiliki sistem autentikasi akun maupun pembagian peran antara pelapor (mahasiswa) dan teknisi/admin kampus.
4. **Belum Ada Pencarian & Filter Kategori:** Halaman daftar saat ini mendukung pengurutan berdasarkan waktu pembuatan, namun belum dilengkapi bilah pencarian kata kunci maupun filter per kategori fasilitas.