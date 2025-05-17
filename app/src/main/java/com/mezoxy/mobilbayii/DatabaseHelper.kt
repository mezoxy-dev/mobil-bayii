package com.mezoxy.mobilbayii

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dataClasses.Admin
import dataClasses.StockPhone
import dataClasses.Urun

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MobilBayiDB.db"
        private const val DATABASE_VERSION = 1 // Şema her değiştiğinde bu sürümü artırın

        // Tablo Adları
        const val TABLE_ADMIN = "admin"
        const val TABLE_CUSTOMERS = "customers"
        const val TABLE_REQUESTS = "requests"
        const val TABLE_PRODUCTS = "products"
        const val TABLE_ORDERS = "orders"
        const val TABLE_PRODUCT_IMAGES = "product_images"
        const val TABLE_STOCKS = "stocks"
        const val TABLE_REQUEST_ITEMS = "request_items"

        // Admin Tablosu Sütunları
        const val COLUMN_ADMIN_ID = "admin_id"
        const val COLUMN_ADMIN_USERNAME = "username"
        const val COLUMN_ADMIN_PASSWORD = "password"

        // Customers Tablosu Sütunları
        const val COLUMN_CUSTOMER_ID = "customer_id"
        const val COLUMN_CUSTOMER_NAME = "name" // products.name ile karışmaması için dikkat
        const val COLUMN_CUSTOMER_PHONE = "phone"
        const val COLUMN_CUSTOMER_EMAIL = "email"
        const val COLUMN_CUSTOMER_ADDRESS = "address"
        const val COLUMN_CUSTOMER_CREATED_AT = "created_at"

        // Requests Tablosu Sütunları
        const val COLUMN_REQUEST_ID = "requests_id"
        const val COLUMN_REQUEST_PRICE_AT_PURCHASE = "price_at_purchase" // request_items'da da var
        const val COLUMN_REQUEST_ORDER_CONF = "order_conf"
        const val COLUMN_REQUEST_DATE = "date"

        // Products Tablosu Sütunları
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_BRAND = "brand"
        const val COLUMN_PRODUCT_MODEL = "model"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_CREATED_AT = "created_at"
        const val COLUMN_PRODUCT_UPDATED_AT = "updated_at"

        // Orders Tablosu Sütunları
        const val COLUMN_ORDER_ID = "order_id"

        // customer_id -> COLUMN_CUSTOMER_ID (FK)
        const val COLUMN_ORDER_DATE = "order_date"
        const val COLUMN_ORDER_TOTAL_AMOUNT = "totalamount"
        // requests_id -> COLUMN_REQUEST_ID (FK)

        // Product Images Tablosu Sütunları
        const val COLUMN_IMAGE_ID = "image_id"

        // product_id -> COLUMN_PRODUCT_ID (FK)
        const val COLUMN_IMAGE_URL = "image_url"
        const val COLUMN_IMAGE_UPLOADED_AT = "uploaded_at"

        // Stocks Tablosu Sütunları
        const val COLUMN_STOCK_ID = "stock_id"
        const val COLUMN_STOCK_QUANTITY = "quantity"
        // product_id -> COLUMN_PRODUCT_ID (FK)

        // Request Items Tablosu Sütunları
        // requests_id -> COLUMN_REQUEST_ID (FK)
        // product_id -> COLUMN_PRODUCT_ID (FK)
        // price_at_purchase -> COLUMN_REQUEST_PRICE_AT_PURCHASE (veya ayrı bir ad)
        // Bu tablo için ayrı bir ID sütunu eklemek daha iyi olabilir:
        const val COLUMN_REQUEST_ITEM_ID = "request_item_id"


        // CREATE TABLE İfadeleri
        private const val CREATE_TABLE_ADMIN = "CREATE TABLE $TABLE_ADMIN (" +
                "$COLUMN_ADMIN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ADMIN_USERNAME TEXT," +
                "$COLUMN_ADMIN_PASSWORD TEXT);"

        private const val CREATE_TABLE_CUSTOMERS = "CREATE TABLE $TABLE_CUSTOMERS (" +
                "$COLUMN_CUSTOMER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CUSTOMER_NAME TEXT," +
                "$COLUMN_CUSTOMER_PHONE TEXT," +
                "$COLUMN_CUSTOMER_EMAIL TEXT," +
                "$COLUMN_CUSTOMER_ADDRESS TEXT," +
                "$COLUMN_CUSTOMER_CREATED_AT TEXT);"

        private const val CREATE_TABLE_REQUESTS = "CREATE TABLE $TABLE_REQUESTS (" +
                "$COLUMN_REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_REQUEST_PRICE_AT_PURCHASE REAL," +
                "$COLUMN_REQUEST_ORDER_CONF INTEGER DEFAULT 0," +
                "$COLUMN_REQUEST_DATE TEXT);"

        private const val CREATE_TABLE_PRODUCTS = "CREATE TABLE $TABLE_PRODUCTS (" +
                "$COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PRODUCT_NAME TEXT," +
                "$COLUMN_PRODUCT_BRAND TEXT," +
                "$COLUMN_PRODUCT_MODEL TEXT," +
                "$COLUMN_PRODUCT_PRICE REAL," +
                "$COLUMN_PRODUCT_DESCRIPTION TEXT," +
                "$COLUMN_PRODUCT_CREATED_AT TEXT," +
                "$COLUMN_PRODUCT_UPDATED_AT TEXT);"

        private const val CREATE_TABLE_ORDERS = "CREATE TABLE $TABLE_ORDERS (" +
                "$COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CUSTOMER_ID INTEGER," +
                "$COLUMN_ORDER_DATE TEXT," +
                "$COLUMN_ORDER_TOTAL_AMOUNT REAL," +
                "$COLUMN_REQUEST_ID INTEGER," +
                "FOREIGN KEY($COLUMN_CUSTOMER_ID) REFERENCES $TABLE_CUSTOMERS($COLUMN_CUSTOMER_ID) ON DELETE SET NULL ON UPDATE CASCADE," +
                "FOREIGN KEY($COLUMN_REQUEST_ID) REFERENCES $TABLE_REQUESTS($COLUMN_REQUEST_ID) ON DELETE SET NULL ON UPDATE CASCADE);"
        // ON DELETE NO ACTION yerine SET NULL veya CASCADE daha uygun olabilir, uygulamanızın mantığına bağlı.

        private const val CREATE_TABLE_PRODUCT_IMAGES = "CREATE TABLE $TABLE_PRODUCT_IMAGES (" +
                "$COLUMN_IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PRODUCT_ID INTEGER," +
                "$COLUMN_IMAGE_URL TEXT," +
                "$COLUMN_IMAGE_UPLOADED_AT TEXT," +
                "FOREIGN KEY($COLUMN_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID) ON DELETE CASCADE ON UPDATE CASCADE);"
        // Bir ürün silinirse resimleri de silinsin (CASCADE)

        private const val CREATE_TABLE_STOCKS = "CREATE TABLE $TABLE_STOCKS (" +
                "$COLUMN_STOCK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_STOCK_QUANTITY INTEGER," +
                "$COLUMN_PRODUCT_ID INTEGER UNIQUE," + // Bir ürün için tek bir stok kaydı olmalı (UNIQUE)
                "FOREIGN KEY($COLUMN_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID) ON DELETE CASCADE ON UPDATE CASCADE);"
        // Bir ürün silinirse stok kaydı da silinsin (CASCADE)

        private const val CREATE_TABLE_REQUEST_ITEMS = "CREATE TABLE $TABLE_REQUEST_ITEMS (" +
                "$COLUMN_REQUEST_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT," + // Ayrı bir ID ekledik
                "$COLUMN_REQUEST_ID INTEGER," +
                "$COLUMN_PRODUCT_ID INTEGER," +
                "${COLUMN_REQUEST_PRICE_AT_PURCHASE} REAL," + // Sütun adını requests tablosundakiyle aynı tuttuk
                "FOREIGN KEY($COLUMN_REQUEST_ID) REFERENCES $TABLE_REQUESTS($COLUMN_REQUEST_ID) ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY($COLUMN_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID) ON DELETE SET NULL ON UPDATE CASCADE);"
        // Bir istek silinirse ilgili kalemler silinsin. Ürün silinirse bu kalemlerdeki ürün ID'si null olsun.
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Yabancı anahtar kısıtlamalarını etkinleştir
        db?.execSQL("PRAGMA foreign_keys=ON;")

        // Tabloları oluştur
        db?.execSQL(CREATE_TABLE_ADMIN)
        db?.execSQL(CREATE_TABLE_CUSTOMERS)
        db?.execSQL(CREATE_TABLE_REQUESTS)
        db?.execSQL(CREATE_TABLE_PRODUCTS)
        db?.execSQL(CREATE_TABLE_ORDERS)
        db?.execSQL(CREATE_TABLE_PRODUCT_IMAGES)
        db?.execSQL(CREATE_TABLE_STOCKS)
        db?.execSQL(CREATE_TABLE_REQUEST_ITEMS)

        // İsteğe bağlı: Başlangıç verileri ekleyebilirsiniz (örneğin, varsayılan bir admin)
        // addInitialAdmin(db)
        // addSampleProductsAndStocks(db) // Örnek ürün ve stok eklemek için
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Bu basit bir yükseltme stratejisidir: tüm tabloları sil ve yeniden oluştur.
        // Gerçek bir uygulamada, kullanıcı verilerini kaybetmemek için daha karmaşık bir
        // migrasyon stratejisi (ALTER TABLE vb.) gerekebilir.
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REQUEST_ITEMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STOCKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCT_IMAGES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REQUESTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ADMIN")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        // Her veritabanı bağlantısı açıldığında yabancı anahtarları etkinleştir
        db?.execSQL("PRAGMA foreign_keys=ON;")
    }

    // --- Admin Fonksiyonları ---
    fun addAdmin(admin: Admin): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(
                COLUMN_ADMIN_USERNAME,
                admin.kullaniciAdi
            ) // Admin sınıfınızdaki alan adlarına göre güncelleyin
            put(
                COLUMN_ADMIN_PASSWORD,
                admin.sifre
            )     // Şifreleri hash'leyerek saklamak en iyisidir!
        }
        val id = db.insert(TABLE_ADMIN, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getAdminByUsername(username: String): Admin? {
        val db = this.readableDatabase
        var admin: Admin? = null
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf(COLUMN_ADMIN_ID, COLUMN_ADMIN_USERNAME, COLUMN_ADMIN_PASSWORD),
            "$COLUMN_ADMIN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_ID))
            val uname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_USERNAME))
            val pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_PASSWORD))
            admin = Admin(id, uname, pass) // Admin sınıfınızın constructor'ına göre güncelleyin
        }
        cursor.close()
        db.close()
        return admin
    }

    // --- Stok Fonksiyonları ---

    /**
     * Belirli bir ürünün stok miktarını günceller.
     * @param productId Güncellenecek ürünün ID'si.
     * @param newQuantity Yeni stok miktarı.
     * @return Güncelleme başarılıysa etkilenen satır sayısı (genellikle 1), değilse 0.
     */
    fun updateStockQuantity(productId: Int, newQuantity: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STOCK_QUANTITY, newQuantity)
        }
        // Stok tablosunda product_id'ye göre güncelleme yap
        val affectedRows = db.update(
            TABLE_STOCKS,
            values,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(productId.toString())
        )
        db.close()
        return affectedRows
    }

    /**
     * Belirli bir ürün ID'sine sahip ürünün güncel stok bilgilerini getirir.
     * @param productIdToFind Ürün ID'si
     * @return StockPhone nesnesi veya ürün bulunamazsa null.
     */
    @SuppressLint("Range")
    fun getStockPhoneById(productIdToFind: Int): StockPhone? {
        var stockPhone: StockPhone? = null
        val selectQuery = """
            SELECT
                p.$COLUMN_PRODUCT_ID,
                p.$COLUMN_PRODUCT_NAME,
                p.$COLUMN_PRODUCT_BRAND,
                p.$COLUMN_PRODUCT_MODEL,
                s.$COLUMN_STOCK_QUANTITY,
                pi.$COLUMN_IMAGE_URL 
            FROM
                $TABLE_PRODUCTS p
            INNER JOIN
                $TABLE_STOCKS s ON p.$COLUMN_PRODUCT_ID = s.$COLUMN_PRODUCT_ID
            LEFT JOIN
                $TABLE_PRODUCT_IMAGES pi ON p.$COLUMN_PRODUCT_ID = pi.$COLUMN_PRODUCT_ID
            WHERE
                p.$COLUMN_PRODUCT_ID = ?
            GROUP BY p.$COLUMN_PRODUCT_ID
        """.trimIndent()
        // GROUP BY p.product_id: Birden fazla resim varsa bile ürün başına tek satır döndürür.
        // LEFT JOIN: Resmi olmayan ürünlerin de sorgulanabilmesini sağlar.

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, arrayOf(productIdToFind.toString()))

            if (cursor.moveToFirst()) {
                val productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                val brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_BRAND))
                val model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_MODEL))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                    ?: "" // Null ise boş string

                // StockPhone constructor'ınıza göre ayarlayın
                //stockPhone = StockPhone(productId, name, brand, model, quantity, imageUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Hata durumunda null dönecek
        } finally {
            cursor?.close()
            db.close()
        }
        return stockPhone
    }

    // --- Diğer Fonksiyonlar (Müşteri, Ürün, Sipariş vb. için CRUD operasyonları) ---
    // Bu fonksiyonları uygulamanızın ihtiyaçlarına göre ekleyebilirsiniz.
    // Örnek olarak birkaç tane daha ekleyelim:

    // --- Ürün Fonksiyonları ---
    /**
     * Yeni bir ürün ekler ve başlangıç stok miktarını ayarlar.
     * @param product Eklenecek ürün (Product veri sınıfınız)
     * @param initialStock Başlangıç stok miktarı
     * @return Eklenen ürünün ID'si veya hata durumunda -1.
     */
    fun addProductWithStock(
        productName: String,
        productBrand: String,
        productModel: String,
        productPrice: Double,
        productDescription: String,
        initialStock: Int,
        imageUrl: String? = null
    ): Long {
        val db = this.writableDatabase
        var productId: Long = -1

        db.beginTransaction()
        try {
            // Ürünü ekle
            val productValues = ContentValues().apply {
                put(COLUMN_PRODUCT_NAME, productName)
                put(COLUMN_PRODUCT_BRAND, productBrand)
                put(COLUMN_PRODUCT_MODEL, productModel)
                put(COLUMN_PRODUCT_PRICE, productPrice)
                put(COLUMN_PRODUCT_DESCRIPTION, productDescription)
                // COLUMN_PRODUCT_CREATED_AT ve COLUMN_PRODUCT_UPDATED_AT için
                // System.currentTimeMillis().toString() veya uygun bir tarih formatı kullanabilirsiniz.
                put(COLUMN_PRODUCT_CREATED_AT, System.currentTimeMillis().toString())
                put(COLUMN_PRODUCT_UPDATED_AT, System.currentTimeMillis().toString())
            }
            productId = db.insert(TABLE_PRODUCTS, null, productValues)

            if (productId != -1L) {
                // Stok kaydını ekle
                val stockValues = ContentValues().apply {
                    put(COLUMN_PRODUCT_ID, productId)
                    put(COLUMN_STOCK_QUANTITY, initialStock)
                }
                db.insert(TABLE_STOCKS, null, stockValues)

                // Eğer resim URL'si varsa, product_images tablosuna ekle
                if (imageUrl != null && imageUrl.isNotBlank()) {
                    val imageValues = ContentValues().apply {
                        put(COLUMN_PRODUCT_ID, productId)
                        put(COLUMN_IMAGE_URL, imageUrl)
                        put(COLUMN_IMAGE_UPLOADED_AT, System.currentTimeMillis().toString())
                    }
                    db.insert(TABLE_PRODUCT_IMAGES, null, imageValues)
                }
                db.setTransactionSuccessful()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            productId = -1 // Hata durumunda ID'yi -1 yap
        } finally {
            db.endTransaction()
            db.close()
        }
        return productId
    }


    /**
     * Bir ürünün bilgilerini günceller.
     * @param productId Güncellenecek ürünün ID'si.
     * @param name Yeni ürün adı.
     * @param brand Yeni ürün markası.
     * @param model Yeni ürün modeli.
     * @param price Yeni ürün fiyatı.
     * @param description Yeni ürün açıklaması.
     * @return Güncelleme başarılıysa etkilenen satır sayısı (genellikle 1), değilse 0.
     */
    fun updateProduct(
        productId: Int,
        name: String,
        brand: String,
        model: String,
        price: Double,
        description: String
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, name)
            put(COLUMN_PRODUCT_BRAND, brand)
            put(COLUMN_PRODUCT_MODEL, model)
            put(COLUMN_PRODUCT_PRICE, price)
            put(COLUMN_PRODUCT_DESCRIPTION, description)
            put(
                COLUMN_PRODUCT_UPDATED_AT,
                System.currentTimeMillis().toString()
            ) // Güncelleme zamanını kaydet
        }
        val affectedRows = db.update(
            TABLE_PRODUCTS,
            values,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(productId.toString())
        )
        db.close()
        return affectedRows
    }

    /**
     * Bir ürünü ID'sine göre siler.
     * İlgili stok ve resim kayıtları da CASCADE kuralları sayesinde silinecektir.
     * @param productId Silinecek ürünün ID'si.
     * @return Silme başarılıysa etkilenen satır sayısı (genellikle 1), değilse 0.
     */
    fun deleteProduct(productId: Int): Int {
        val db = this.writableDatabase
        // products tablosundan silme işlemi, stocks ve product_images'daki
        // ilgili kayıtları FOREIGN KEY ON DELETE CASCADE sayesinde otomatik olarak silecektir.
        val affectedRows = db.delete(
            TABLE_PRODUCTS,
            "$COLUMN_PRODUCT_ID = ?",
            arrayOf(productId.toString())
        )
        db.close()
        return affectedRows
    }

}
// Örnek başlangıç verileri eklemek için fonksiyonlar (onCreate içinde çağrılabilir)
/*
private fun addInitialAdmin(db: SQLiteDatabase?) {
    val admin = Admin(kullaniciAdi = "admin", sifre = "password123") // Şifreyi hash'leyin!
    val values = ContentValues().apply {
        put(COLUMN_ADMIN_USERNAME, admin.kullaniciAdi)
        put(COLUMN_ADMIN_PASSWORD, admin.sifre) // Gerçek uygulamada hash'lenmiş şifre
    }
    db?.insert(TABLE_ADMIN, null, values)
}

private fun addSampleProductsAndStocks(db: SQLiteDatabase?) {
    // Örnek Ürün 1
    var productName = "Galaxy S23"
    var productBrand = "Samsung"
    var productModel = "SM-S911B"
    var productPrice = 25000.0
    var productDescription = "Yeni nesil Samsung Galaxy S23"
    var initialStock = 50
    var imageUrl = "https://example.com/s23.jpg" // Gerçek bir URL veya yerel dosya yolu

    var productValues = ContentValues().apply {
        put(COLUMN_PRODUCT_NAME, productName)
        put(COLUMN_PRODUCT_BRAND, productBrand)
        put(
} */