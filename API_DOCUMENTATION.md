# Library Management — API Dokümantasyonu

> **Base URL:** `http://localhost:8080`
> **Kimlik Doğrulama:** JWT Bearer Token
> **İçerik Tipi:** `application/json`

---

## Genel Bilgiler

### Kimlik Doğrulama

Auth endpointleri dışındaki tüm isteklerde `Authorization` header'ı zorunludur:

```
Authorization: Bearer <token>
```

### Kullanıcı Rolleri ve Erişim Alanları

| Rol | Açıklama | Endpoint Prefix |
|---|---|---|
| `ADMIN` | Sistem yöneticisi | `/api/admin/**` |
| `KOORDINATOR` | İl koordinatörü — yalnızca kendi ilini yönetir | `/api/koordinator/**` |
| `DEPO_SORUMLUSU` | Depo görevlisi — yalnızca kendi deposunu yönetir | `/api/depo-gorevlisi/**` |
| `USER` | Yardım talep eden vatandaş | `/api/user/**` |

### HTTP Durum Kodları

| Kod | Açıklama |
|---|---|
| `200 OK` | İstek başarılı |
| `400 Bad Request` | Validation hatası / geçersiz istek |
| `401 Unauthorized` | Token eksik veya geçersiz |
| `403 Forbidden` | Yetkisiz erişim (rol uyuşmazlığı) |
| `404 Not Found` | Kaynak bulunamadı |

---

## Enum Değerleri

İstek body'lerinde string olarak gönderilmelidir.

### `Role`
```
ADMIN | KOORDINATOR | DEPO_SORUMLUSU | USER
```

### `AfetTuru`
```
DEPREM | SEL | YANGIN | FIRTINA | HEYELAN
```

### `DepoModeli`
```
FIZIKSEL | MERKEZİ | SANAL
```

### `MalzemeKategori`
```
GIDA | GIYSI | ISINMA | HIJYEN | BARINMA | SAGLIK | BEBEK
```

### `KaynakTuru`
```
BAGIS | TRANSFER
```

### `Oncelik`
```
DUSUK | ORTA | ACIL
```

### `PinTuru`
```
YARDIM | YEMEK | CADIR | SU
```

### `TransferDurumu`
```
BEKLEMEDE | ONAYLANDI | REDDEDILDI
```

### `KargoDurumu` _(talep kargo takibi)_
```
HAZIRLANIYOR | YOLDA | TESLİM_EDİLDİ
```

---

## 1. Auth Endpoints

> Token gerekmez — herkese açık.

---

### `POST /api/auth/login`

TC kimlik numarasına göre sisteme giriş veya kayıt başlatır.

- **Yeni kullanıcı** (`tc` sistemde yoksa): Hesap oluşturulur, `telNo` zorunludur, TC algoritma doğrulamasından geçirilir, SMS ile 6 haneli doğrulama kodu gönderilir.
- **Mevcut kullanıcı** (`tc` sistemde varsa): Şifre ve telefon numarası doğrulanır.

Her iki durumda da token bu endpoint'ten **dönmez** — token almak için `/api/auth/verify` çağrılmalıdır.

**Request Body:**
```json
{
  "tc": "12345678901",
  "password": "Sifre123!",
  "telNo": "05301234567"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `tc` | string | Evet | Tam 11 karakter, TC algoritmasına uygun |
| `password` | string | Evet | 8–16 karakter, en az 1 büyük harf, 1 küçük harf, 1 rakam, 1 özel karakter (`@$!%*?&.`) |
| `telNo` | string | Yeni kullanıcıda zorunlu | `05XXXXXXXXX` formatı |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `400` — Geçersiz TC, geçersiz telefon formatı, eksik `telNo` (yeni kullanıcı)
- `400` — Hatalı şifre veya TC / telefon uyuşmazlığı (mevcut kullanıcı)

---

### `POST /api/auth/verify`

SMS ile gönderilen 6 haneli doğrulama kodunu onaylar ve JWT token döner.

**Request Body:**
```json
{
  "tc": "12345678901",
  "telNo": "05301234567",
  "kod": "481523"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `tc` | string | Evet | Tam 11 karakter |
| `telNo` | string | Evet | `05XXXXXXXXX` formatı |
| `kod` | string | Evet | 6 haneli SMS kodu |

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5...",
  "role": "USER",
  "ilkGiris": false
}
```

> `ilkGiris: true` ise kullanıcı sisteme ilk kez giriyordur — frontend şifre değiştirme ekranına yönlendirebilir.

**Hata Durumları:**
- `400` — TC / telefon uyuşmazlığı
- `400` — Geçersiz kod
- `400` — Kodun geçerlilik süresi dolmuş (10 dakika)

---

### `PUT /api/auth/passwordRegen`

TC ve telefon numarasını doğrulayarak yeni bir SMS doğrulama kodu gönderir (şifre sıfırlama akışı başlatır).

**Request Body:**
```json
{
  "tc": "12345678901",
  "telNo": "05301234567"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `tc` | string | Evet | Tam 11 karakter |
| `telNo` | string | Evet | `05XXXXXXXXX` formatı |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `400` — TC / telefon uyuşmazlığı

---

## 2. User Endpoints

> **Rol:** `USER`
> **Header:** `Authorization: Bearer <token>`

---

### `POST /api/user/talep`

Yardım talebi oluşturur. Her kullanıcının aynı anda en fazla **3** aktif pin hakkı vardır.

**Request Body:**
```json
{
  "malzemeId": [1, 3, 7],
  "kisiSayısı": 4,
  "pinRequestDTO": {
    "latitude": 39.9334,
    "longitude": 32.8597,
    "pinTuru": "YARDIM"
  }
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `malzemeId` | number[] | Evet | En az 1 malzeme ID'si |
| `kisiSayısı` | number | Evet | 1–10 arası |
| `pinRequestDTO.latitude` | number | Evet | Türkiye sınırları: 35.0–43.0 |
| `pinRequestDTO.longitude` | number | Evet | Türkiye sınırları: 25.0–45.0 |
| `pinRequestDTO.pinTuru` | string | Evet | Bkz. PinTuru enum |

**Response `200 OK`:**
```json
"Kayıt Başarılı bir şekilde gercekleşti"
```

**Hata Durumları:**
- `400` — Pin limiti (3) aşıldıysa
- `404` — Geçersiz `malzemeId`

---

### `GET /api/user/pinler`

Kullanıcının aktif pinlerini listeler.

**Response `200 OK`:**
```json
[
  {
    "id": 12,
    "latitude": 39.9334,
    "longitude": 32.8597,
    "pinTuru": "YARDIM"
  }
]
```

---

### `GET /api/user/talepler/aktif`

Kullanıcının aktif (devam eden) taleplerini listeler.

**Response `200 OK`:**
```json
[
  {
    "talep_id": 5,
    "durum": "BEKLEMEDE",
    "malzemeAdi": ["Battaniye", "Su"]
  }
]
```

---

### `GET /api/user/talepler/tamamlanan`

Kullanıcının tamamlanmış taleplerini listeler. Response formatı aktif taleplerle aynıdır.

---

### `PUT /api/user/talep/{talepId}/iptal`

Kullanıcı kendi talebini iptal eder.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İptal edilecek talebin ID'si |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `talepId`

---

### `PUT /api/user/sifre-yenileme`

Giriş yapmış kullanıcının şifresini günceller. İlk girişte şifre değişikliği için kullanılır.

**Request Body:**
```json
{
  "sifre": "YeniSifre1!"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `sifre` | string | Evet | 8–16 karakter, en az 1 büyük harf, 1 küçük harf, 1 rakam, 1 özel karakter (`@$!%*?&.`) |

**Response `200 OK`:** _(boş body)_

---

## 3. Koordinatör Endpoints

> **Rol:** `KOORDINATOR`
> **Header:** `Authorization: Bearer <token>`
> Koordinatör yalnızca **kendi ilindeki** verileri görür ve yönetir.

---

### Talep Yönetimi

#### `GET /api/koordinator/talepler/aktif`

İldeki aktif kullanıcı taleplerini listeler.

**Response `200 OK`:**
```json
[
  {
    "talepId": 7,
    "kisiSayisi": 4,
    "malzemeler": ["Battaniye", "Su"],
    "pinResponseDTO": {
      "id": 12,
      "latitude": 39.9334,
      "longitude": 32.8597,
      "pinTuru": "YARDIM"
    }
  }
]
```

---

#### `GET /api/koordinator/talepler/gecmis`

İldeki geçmiş (tamamlanmış) talepleri listeler. Response formatı aktif taleplerle aynıdır.

---

#### `GET /api/koordinator/talep/{talepId}/depo-onerisi`

Belirli bir talep için en yakın depo önerilerini mesafeye göre sıralı döner.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Öneri istenilen talebin ID'si |

**Response `200 OK`:**
```json
[
  {
    "depoId": 2,
    "depoAdi": "Ankara Merkez Deposu",
    "mesafeKm": 3.7,
    "malzemeler": [
      {
        "id": 1,
        "malzemeAdi": "Battaniye",
        "malzemeKategori": "ISINMA",
        "stok": 150
      }
    ]
  }
]
```

> Sonuçlar `mesafeKm` değerine göre küçükten büyüğe sıralıdır.

**Hata Durumları:**
- `404` — Geçersiz `talepId`

---

#### `PUT /api/koordinator/talep/{talepId}/onayla/{depoId}`

Bir talebi onaylar ve hangi depodan karşılanacağını belirtir.

**Path Parameters:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Onaylanacak talebin ID'si |
| `depoId` | number | Karşılayacak deponun ID'si |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `talepId` veya `depoId`

---

#### `PUT /api/koordinator/talep/{talepId}/reddet`

Bir talebi reddeder.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Reddedilecek talebin ID'si |

**Request Body:**
```json
{
  "aciklama": "Stok yetersiz, daha sonra tekrar başvurun."
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `aciklama` | string | Hayır | Red gerekçesi |

**Response `200 OK`:** _(boş body)_

---

### Depo Yönetimi

#### `GET /api/koordinator/depolar`

Koordinatörün ilindeki aktif fiziksel / merkezi depoları listeler.

**Response `200 OK`:**
```json
[
  {
    "depoId": 2,
    "depoAdi": "Ankara Merkez Deposu",
    "depoModeli": "MERKEZİ",
    "malzemeler": [
      {
        "id": 1,
        "malzemeAdi": "Battaniye",
        "malzemeKategori": "ISINMA",
        "stok": 150
      }
    ]
  }
]
```

---

#### `PUT /api/koordinator/depo/{depoId}/pasife-al`

Depoyu pasif duruma alır.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `depoId` | number | Pasife alınacak deponun ID'si |

**Response `200 OK`:** _(boş body)_

---

#### `PUT /api/koordinator/depo/{depoId}/aktifleştir`

Pasif durumdaki depoyu tekrar aktif hale getirir.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `depoId` | number | Aktifleştirilecek deponun ID'si |

**Response `200 OK`:** _(boş body)_

---

### Sanal Depo Yönetimi

#### `GET /api/koordinator/sanal-depolar`

İldeki sanal depoları listeler.

**Response `200 OK`:** _(Depo listesi — `/koordinator/depolar` ile aynı format)_

---

#### `GET /api/koordinator/sanal-depo/gorevliler`

Sanal depoya atanabilecek görevlileri listeler.

**Response `200 OK`:**
```json
[
  {
    "gorevliId": 3,
    "gorevliAdi": "Mehmet Demir",
    "email": "mehmet@ornek.com"
  }
]
```

---

#### `PUT /api/koordinator/sanal-depo/{depoId}/gorevli/{gorevliId}`

Belirtilen sanal depoya bir görevli atar.

**Path Parameters:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `depoId` | number | Sanal deponun ID'si |
| `gorevliId` | number | Atanacak görevlinin ID'si |

**Response `200 OK`:** _(boş body)_

---

### Depolar Arası Yardım Talebi

#### `POST /api/koordinator/depo/{depoId}/yardim-talebi`

Bir depo adına başka depodan malzeme transfer talebi oluşturur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `depoId` | number | Yardım talep eden deponun ID'si |

**Request Body:**
```json
{
  "oncelik": "ACIL",
  "aciklama": "Acil battaniye ihtiyacımız var",
  "kalemler": [
    {
      "malzemeId": 1,
      "miktar": 200
    },
    {
      "malzemeId": 5,
      "miktar": 100
    }
  ]
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `oncelik` | string | Evet | Bkz. Oncelik enum |
| `aciklama` | string | Hayır | Maks. 100 karakter |
| `kalemler` | array | Evet | En az 1 kalem |
| `kalemler[].malzemeId` | number | Evet | Pozitif tamsayı |
| `kalemler[].miktar` | number | Evet | 1–10000 arası |

**Response `200 OK`:** _(boş body)_

---

#### `GET /api/koordinator/depo-yardim-talepleri/aktif`

İlgili koordinatörün aktif depolar arası transfer taleplerini listeler.

**Response `200 OK`:**
```json
[
  {
    "talepId": 15,
    "oncelik": "ACIL",
    "transferDurumu": "BEKLEMEDE",
    "adminNotu": null,
    "malzemeler": [
      {
        "malzemeAdi": "Battaniye",
        "miktar": 200
      }
    ]
  }
]
```

---

#### `GET /api/koordinator/depo-yardim-talepleri/gecmis`

Geçmiş (sonuçlanmış) transfer taleplerini listeler. Response formatı aktif taleplerle aynıdır.

---

#### `PUT /api/koordinator/depo-yardim-talepleri/{talepId}/iptal`

Beklemedeki bir transfer talebini iptal eder.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İptal edilecek talebin ID'si |

**Response `200 OK`:** _(boş body)_

---

### Afet Yönetimi

#### `GET /api/koordinator/afet/aktif`

İldeki aktif afetleri listeler.

**Response `200 OK`:**
```json
[
  {
    "afetTuru": "DEPREM",
    "baslangicTarihi": "2026-05-01T08:30:00",
    "bitisTarihi": null
  }
]
```

---

#### `GET /api/koordinator/afet/gecmis`

İldeki geçmiş afetleri listeler.

**Response `200 OK`:**
```json
[
  {
    "afetTuru": "SEL",
    "baslangicTarihi": "2025-10-10T12:00:00",
    "bitisTarihi": "2025-10-20T18:00:00"
  }
]
```

---

## 4. Depo Görevlisi Endpoints

> **Rol:** `DEPO_SORUMLUSU`
> **Header:** `Authorization: Bearer <token>`
> Depo görevlisi yalnızca **kendi deposundaki** verileri görür ve yönetir.

---

### `GET /api/depo-gorevlisi/malzeme`

Görevlinin bağlı olduğu depodaki tüm malzemeleri listeler.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "malzemeAdi": "Battaniye",
    "malzemeKategori": "ISINMA",
    "stok": 150
  },
  {
    "id": 3,
    "malzemeAdi": "Ekmek",
    "malzemeKategori": "GIDA",
    "stok": 500
  }
]
```

---

### `POST /api/depo-gorevlisi/stok`

Depoya stok girişi yapar.

**Request Body:**
```json
{
  "miktar": 100,
  "kaynakTuru": "BAGIS",
  "malzemeId": 1,
  "bagıscıAdı": "Ahmet Yılmaz"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `miktar` | number | Evet | 1–10000 arası |
| `kaynakTuru` | string | Evet | Bkz. KaynakTuru enum |
| `malzemeId` | number | Evet | Pozitif tamsayı |
| `bagıscıAdı` | string | Hayır | 8–30 karakter (BAGIS ise anlamlı) |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `malzemeId`

---

### `GET /api/depo-gorevlisi/talepler/gonderilecek`

Deponun karşılaması gereken onaylanmış talepleri listeler.

**Response `200 OK`:**
```json
[
  {
    "talepId": 7,
    "malzemeler": [
      {
        "malzemeAdi": "Battaniye",
        "miktar": 10
      },
      {
        "malzemeAdi": "Su",
        "miktar": 20
      }
    ]
  }
]
```

---

### `PUT /api/depo-gorevlisi/talep/{talepId}/hazirla`

Talebin hazırlanmaya başlandığını işaretler. Kargo durumu `HAZIRLANIYIOR` olur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek talebin ID'si |

**Response `200 OK`:** _(boş body)_

---

### `PUT /api/depo-gorevlisi/talep/{talepId}/yola-cik`

Talebin yola çıktığını işaretler. Kargo durumu `YOLDA` olur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek talebin ID'si |

**Response `200 OK`:** _(boş body)_

---

### `PUT /api/depo-gorevlisi/talep/{talepId}/teslim-edildi`

Talebin teslim edildiğini işaretler. Kargo durumu `TESLİM_EDİLDİ` olur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek talebin ID'si |

**Response `200 OK`:** _(boş body)_

---

### Depolar Arası Transfer Yönetimi

#### `GET /api/depo-gorevlisi/depolar-arasi-transfer/aktif`

Admin tarafından onaylanmış, görevlinin deposundan gönderilmesi gereken depolar arası transfer taleplerini listeler.

**Response `200 OK`:**
```json
[
  {
    "talepId": 15,
    "oncelik": "ACIL",
    "transferDurumu": "BEKLEMEDE",
    "adminNotu": null,
    "latitude": 39.9334,
    "longitude": 32.8597,
    "malzemeler": [
      {
        "malzemeAdi": "Battaniye",
        "miktar": 200
      }
    ]
  }
]
```

---

#### `PUT /api/depo-gorevlisi/depolar-arasi-transfer/{talepId}/hazirla`

Depolar arası transfer talebinin hazırlanmaya başlandığını işaretler. Transfer durumu `HAZIRLANIYOR` olur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek transfer talebinin ID'si |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `talepId` veya talep bu depoya ait değil

---

#### `PUT /api/depo-gorevlisi/depolar-arasi-transfer/{talepId}/yola-cik`

Depolar arası transferin yola çıktığını işaretler. Transfer durumu `YOLDA` olur.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek transfer talebinin ID'si |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `talepId` veya talep bu depoya ait değil

---

#### `PUT /api/depo-gorevlisi/depolar-arasi-transfer/{talepId}/teslim-edildi`

Depolar arası transferin teslim edildiğini işaretler. Transfer durumu `TESLIM_EDILDI` olur, stok güncellenir ve talep pasife alınır.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | İşaretlenecek transfer talebinin ID'si |

**Response `200 OK`:** _(boş body)_

**Hata Durumları:**
- `404` — Geçersiz `talepId` veya talep bu depoya ait değil
- `404` — Depoda ilgili malzeme bulunamadı

---

## 5. Admin Endpoints

> **Rol:** `ADMIN`
> **Header:** `Authorization: Bearer <token>`

---

### Kullanıcı & Depo Oluşturma

#### `POST /api/admin/koordinator`

Yeni bir koordinatör hesabı oluşturur.

**Request Body:**
```json
{
  "tc": "12345678901",
  "telNo": "05301234567",
  "email": "koordinator@ornek.com",
  "username": "Ali Veli",
  "gorevYaptıgıIl": "Ankara"
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `tc` | string | Evet | Tam 11 karakter |
| `telNo` | string | Evet | `05XXXXXXXXX` formatı |
| `email` | string | Evet | Geçerli email formatı |
| `username` | string | Evet | 3–30 karakter |
| `gorevYaptıgıIl` | string | Evet | 3–20 karakter (il adı) |

**Response `200 OK`:** _(boş body)_

---

#### `POST /api/admin/depo-gorevlisi`

Yeni bir depo görevlisi hesabı oluşturur. Request body formatı koordinatör oluşturmayla aynıdır.

---

#### `POST /api/admin/depo`

Yeni bir depo oluşturur.

**Request Body:**
```json
{
  "depoAdi": "İzmir Lojistik Merkezi",
  "depoModeli": "FIZIKSEL",
  "il": "İzmir",
  "latitude": 38.4192,
  "longitude": 27.1287
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `depoAdi` | string | Evet | Maks. 30 karakter |
| `depoModeli` | string | Evet | Bkz. DepoModeli enum |
| `il` | string | Evet | 3–20 karakter (il adı) |
| `latitude` | number | Evet | Türkiye sınırları: 35.0–43.0 |
| `longitude` | number | Evet | Türkiye sınırları: 25.0–45.0 |

**Response `200 OK`:** _(boş body)_

---

### Depo Görüntüleme

#### `GET /api/admin/depolar`

Sistemdeki tüm depoları malzemeleriyle birlikte listeler.

**Response `200 OK`:**
```json
[
  {
    "depoId": 1,
    "depoAdi": "Ankara Merkez",
    "depoModeli": "MERKEZİ",
    "ilAdı": "Ankara",
    "malzemeler": [
      {
        "id": 1,
        "malzemeAdi": "Battaniye",
        "malzemeKategori": "ISINMA",
        "stok": 150
      }
    ]
  }
]
```

---

### Afet Yönetimi

#### `POST /api/admin/afet/aktifleştir`

Bir ili afet bölgesi olarak aktifleştirir.

**Request Body:**
```json
{
  "il": "Kahramanmaraş",
  "afetId": 1
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `il` | string | Evet | 3–20 karakter (il adı) |
| `afetId` | number | Evet | Sistemdeki afet tanım ID'si |

**Response `200 OK`:** _(boş body)_

---

#### `GET /api/admin/afet/aktif`

Sistemdeki tüm aktif afetleri listeler.

**Response `200 OK`:**
```json
[
  {
    "afetId": 1,
    "afetTuru": "DEPREM",
    "ilAdı": "Kahramanmaraş",
    "baslangicTarihi": "2026-05-01T08:30:00"
  }
]
```

---

#### `PUT /api/admin/afet/{afetId}/iptal`

Aktif bir afeti sona erdirir.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `afetId` | number | Sona erdirilecek afetin ID'si |

**Response `200 OK`:** _(boş body)_

---

### Transfer Talebi Yönetimi

#### `GET /api/admin/transfer-talepleri`

Tüm koordinatörlerden gelen depolar arası transfer taleplerini listeler.

**Response `200 OK`:**
```json
[
  {
    "kaynakTalepId": 15,
    "oncelik": "ACIL",
    "acıklama": "Acil battaniye ihtiyacımız var",
    "olusturulmaTarihi": "2026-05-20T10:00:00",
    "kalemler": [
      {
        "malzemeId": 1,
        "malzemeAdi": "Battaniye",
        "miktar": 200
      }
    ]
  }
]
```

---

#### `GET /api/admin/transfer-talepleri/{talepId}/depo-onerisi`

Belirli bir transfer talebi için en yakın uygun depo önerilerini mesafeye göre sıralı döner.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Öneri istenilen talebin ID'si |

**Response `200 OK`:** _(DepoOneriResponse listesi — koordinatör depo-onerisi ile aynı format)_

---

#### `PUT /api/admin/transfer-talepleri/{talepId}/onayla/{depoId}`

Bir transfer talebini onaylar ve hangi depodan karşılanacağını belirtir.

**Path Parameters:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Onaylanacak talebin ID'si |
| `depoId` | number | Karşılayacak deponun ID'si |

**Response `200 OK`:** _(boş body)_

---

#### `PUT /api/admin/transfer-talepleri/{talepId}/reddet`

Bir transfer talebini reddeder.

**Path Parameter:**

| Parametre | Tip | Açıklama |
|---|---|---|
| `talepId` | number | Reddedilecek talebin ID'si |

**Request Body:**
```json
{
  "aciklama": "İstenen stok mevcut depoda bulunmuyor."
}
```

| Alan | Tip | Zorunlu | Kural |
|---|---|---|---|
| `aciklama` | string | Hayır | Maks. 100 karakter |

**Response `200 OK`:** _(boş body)_

---

## Veri Modelleri

### LoginResponse
```json
{
  "token": "eyJhbGci...",
  "role": "USER",
  "ilkGiris": false
}
```

### KodGonderRequest
```json
{
  "tc": "12345678901",
  "telNo": "05301234567",
  "kod": "481523"
}
```

### MalzemeResponseDTO
```json
{
  "id": 1,
  "malzemeAdi": "Battaniye",
  "malzemeKategori": "ISINMA",
  "stok": 150
}
```

### PinResponseDTO
```json
{
  "id": 12,
  "latitude": 39.9334,
  "longitude": 32.8597,
  "pinTuru": "YARDIM"
}
```

### UserTalepResponse
```json
{
  "talep_id": 5,
  "durum": "BEKLEMEDE",
  "malzemeAdi": ["Battaniye", "Su"]
}
```

### KoordinatorTaleplerResponse
```json
{
  "talepId": 7,
  "kisiSayisi": 4,
  "malzemeler": ["Battaniye", "Su"],
  "pinResponseDTO": {
    "id": 12,
    "latitude": 39.9334,
    "longitude": 32.8597,
    "pinTuru": "YARDIM"
  }
}
```

### DepoResponse
```json
{
  "depoId": 2,
  "depoAdi": "Ankara Merkez Deposu",
  "depoModeli": "MERKEZİ",
  "malzemeler": [ ... ]
}
```

### DepoOneriResponse
```json
{
  "depoId": 2,
  "depoAdi": "Ankara Merkez Deposu",
  "mesafeKm": 3.7,
  "malzemeler": [ ... ]
}
```

### DepoTalepResponse
```json
{
  "talepId": 7,
  "malzemeler": [
    { "malzemeAdi": "Battaniye", "miktar": 10 }
  ]
}
```

### DepoToDepoTransferResponse
```json
{
  "talepId": 15,
  "oncelik": "ACIL",
  "transferDurumu": "BEKLEMEDE",
  "adminNotu": null,
  "malzemeler": [
    { "malzemeAdi": "Battaniye", "miktar": 200 }
  ]
}
```

### AllDepoMalzemeResponse _(Admin)_
```json
{
  "depoId": 1,
  "depoAdi": "Ankara Merkez",
  "depoModeli": "MERKEZİ",
  "ilAdı": "Ankara",
  "malzemeler": [ ... ]
}
```

### AfetAktifResponse _(Admin)_
```json
{
  "afetId": 1,
  "afetTuru": "DEPREM",
  "ilAdı": "Kahramanmaraş",
  "baslangicTarihi": "2026-05-01T08:30:00"
}
```

### AfetResponse _(Koordinatör)_
```json
{
  "afetTuru": "DEPREM",
  "baslangicTarihi": "2026-05-01T08:30:00",
  "bitisTarihi": null
}
```

### KaynakTalepResponse _(Admin — Transfer Talepleri)_
```json
{
  "kaynakTalepId": 15,
  "oncelik": "ACIL",
  "acıklama": "Acil ihtiyaç",
  "olusturulmaTarihi": "2026-05-20T10:00:00",
  "kalemler": [
    { "malzemeId": 1, "malzemeAdi": "Battaniye", "miktar": 200 }
  ]
}
```

### DepoGorevliResponse
```json
{
  "gorevliId": 3,
  "gorevliAdi": "Mehmet Demir",
  "email": "mehmet@ornek.com"
}
```

---

## Örnek Kullanım Akışları

### Vatandaş — Yardım Talebi
```
1. POST /api/auth/login                    → kimlik doğrula, SMS kodu gönderilir
2. POST /api/auth/verify                   → SMS kodunu onayla, token ve rol al
3. GET  /api/user/talepler/aktif           → mevcut taleplere bak
4. POST /api/user/talep                    → yeni talep oluştur (malzemeId listesi + pin konum)
5. GET  /api/user/pinler                   → pinleri haritada göster
6. PUT  /api/user/talep/{id}/iptal         → gerekirse talebi iptal et
```

### Koordinatör — Talep Yönetimi
```
1. POST /api/auth/login                                         → kimlik doğrula, SMS kodu gönderilir
2. POST /api/auth/verify                                        → token al
2. GET  /api/koordinator/talepler/aktif                         → bekleyen talepleri listele
3. GET  /api/koordinator/talep/{talepId}/depo-onerisi           → en yakın depo öner
4. PUT  /api/koordinator/talep/{talepId}/onayla/{depoId}        → talebi onayla
   veya
   PUT  /api/koordinator/talep/{talepId}/reddet                 → talebi reddet
```

### Koordinatör — Depolar Arası Transfer
```
1. POST /api/auth/login  →  POST /api/auth/verify              → token al
2. GET  /api/koordinator/depolar                                → depolarını gör
3. POST /api/koordinator/depo/{depoId}/yardim-talebi           → transfer talebi oluştur
4. GET  /api/koordinator/depo-yardim-talepleri/aktif           → talebin durumunu takip et
```

### Depo Görevlisi — Kullanıcı Talebi Teslimat Akışı
```
1. POST /api/auth/login  →  POST /api/auth/verify              → token al
2. GET  /api/depo-gorevlisi/talepler/gonderilecek              → gönderilecek talepleri gör
3. PUT  /api/depo-gorevlisi/talep/{id}/hazirla                 → hazırlamaya başla
4. PUT  /api/depo-gorevlisi/talep/{id}/yola-cik                → yola çıkış
5. PUT  /api/depo-gorevlisi/talep/{id}/teslim-edildi           → teslim tamamlandı
```

### Depo Görevlisi — Depolar Arası Transfer Akışı
```
1. POST /api/auth/login  →  POST /api/auth/verify                             → token al
2. GET  /api/depo-gorevlisi/depolar-arasi-transfer/aktif                      → onaylanmış transfer taleplerini gör
3. PUT  /api/depo-gorevlisi/depolar-arasi-transfer/{id}/hazirla               → hazırlamaya başla
4. PUT  /api/depo-gorevlisi/depolar-arasi-transfer/{id}/yola-cik              → malzemeleri gönder
5. PUT  /api/depo-gorevlisi/depolar-arasi-transfer/{id}/teslim-edildi         → teslim tamamlandı, stok düşüldü
```

### Admin — Afet & Transfer Yönetimi
```
1. POST /api/auth/login  →  POST /api/auth/verify              → token al
2. POST /api/admin/afet/aktifleştir                            → il afet bölgesi ilan et
3. GET  /api/admin/transfer-talepleri                          → koordinatör transfer taleplerini gör
4. GET  /api/admin/transfer-talepleri/{id}/depo-onerisi        → uygun depo bul
5. PUT  /api/admin/transfer-talepleri/{id}/onayla/{depoId}     → transferi onayla
6. PUT  /api/admin/afet/{afetId}/iptal                         → afet bitince iptal et
```
