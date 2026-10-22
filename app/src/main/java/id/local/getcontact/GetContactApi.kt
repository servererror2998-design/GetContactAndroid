package id.local.getcontact

class GetContactApi(private val credentials: SecureCredentialStore) {
    fun run(mode: String, number: String): String {
        if (mode != "Kuota" && !number.replace(Regex("[ +()\\-]"), "").matches(Regex("^[0-9]{8,15}$"))) {
            return "Format nomor tidak valid. Gunakan angka dengan kode negara bila perlu."
        }
        if (credentials.load() == null) return "Simpan token dan finalKey milik akun Anda melalui menu Akun terlebih dahulu."
        return "API belum dikonfigurasi: repositori sumber menggunakan API privat GetContact. Tambahkan integrasi yang diizinkan dan dokumentasinya sebelum melakukan lookup."
    }
}
