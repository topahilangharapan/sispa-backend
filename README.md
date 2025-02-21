# B01 - Radiant - BE

SiSPA adalah solusi sistem informasi untuk mengelola dokumentasi event, data administrasi, klien, vendor, dan inventaris secara efisien. Repository ini berisi backend dari SiSPA yang dikembangkan menggunakan **Spring Boot** dengan database **PostgreSQL**.

---

## 📌 **Fitur Utama**
- 🔒 **Autentikasi & Otorisasi** menggunakan JWT.
- 🏗 **Manajemen Data** untuk event, klien, vendor, dan inventaris.
- 🔄 **Environment** untuk pengembangan (*dev*) dan produksi (*prod*).
- 🐳 **Docker** untuk inisialisasi database dengan mudah.

---

## 🛠 **Setup & Konfigurasi**
### 1️⃣ **Clone Repository**
```bash
git clone https://github.com/PROPENSI-RADIANT/SiSPA-backend.git
cd sispa-backend
```

### 2️⃣ **Konfigurasi Environment**
Buat file `.env` berdasarkan contoh `env.example`:
```bash
cp env.example .env
```
Kemudian isi variabel sesuai kebutuhan.

### 3️⃣ **Jalankan Database dengan Docker**
Gunakan skrip `init-db.sh` untuk menginisialisasi database:

#### **📌 macOS & Linux**
```bash
chmod +x init-db.sh
./init-db.sh
```

#### **📌 Windows (Git Bash, WSL, atau PowerShell)**
```bash
sh init-db.sh
```

---

## ⚙ **Konfigurasi Profil Spring Boot**
SiSPA menggunakan profil yang dapat disesuaikan dengan kebutuhan:
- **`dev`** (Development Mode)
- **`prod`** (Production Mode)

Atur profil yang aktif di `application.yaml`:
```yaml
spring:
  profiles:
    active: "dev"  # Ganti ke "prod" jika di lingkungan produksi
```
---
🌸 _Made with luv, Radiant_
