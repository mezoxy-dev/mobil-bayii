package com.mezoxy.mobilbayii

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import dataClasses.Request
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

        // Views
        const val VIEW_PRODUCTS_WITH_STOCK = "view_products_with_stock"

        // Sorgu hızını arttırmak için Index Tanımlamaları
        //-- (CREATE_INDEX_PRODUCT_BRAND) Marka bazlı aramalarda hız
        //-- (CREATE_INDEX_PRODUCT_MODEL) Model bazlı sorgularda hız
        //-- (CREATE_INDEX_PRODUCT_NAME) Ad bazlı arama (search bar için faydalı)
        //-- (CREATE_INDEX_STOCK_PRODUCT_ID) Stok tablosundaki product_id için index (JOIN işlemleri için)

        private const val CREATE_INDEX_PRODUCT_BRAND =
            "CREATE INDEX IF NOT EXISTS idx_product_brand ON $TABLE_PRODUCTS($COLUMN_PRODUCT_BRAND);"

        private const val CREATE_INDEX_PRODUCT_MODEL =
            "CREATE INDEX IF NOT EXISTS idx_product_model ON $TABLE_PRODUCTS($COLUMN_PRODUCT_MODEL);"

        private const val CREATE_INDEX_PRODUCT_NAME =
            "CREATE INDEX IF NOT EXISTS idx_product_name ON $TABLE_PRODUCTS($COLUMN_PRODUCT_NAME);"

        private const val CREATE_INDEX_STOCK_PRODUCT_ID =
            "CREATE INDEX IF NOT EXISTS idx_stock_product_id ON $TABLE_STOCKS($COLUMN_PRODUCT_ID);"



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
                "$COLUMN_CUSTOMER_CREATED_AT TEXT DEFAULT (datetime('now', 'localtime')));"

        private const val CREATE_TABLE_REQUESTS = "CREATE TABLE $TABLE_REQUESTS (" +
                "$COLUMN_REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_REQUEST_ORDER_CONF INTEGER DEFAULT 0," +
                "$COLUMN_REQUEST_DATE TEXT DEFAULT (datetime('now', 'localtime')));"

        private const val CREATE_TABLE_PRODUCTS = "CREATE TABLE $TABLE_PRODUCTS (" +
                "$COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PRODUCT_NAME TEXT," +
                "$COLUMN_PRODUCT_BRAND TEXT," +
                "$COLUMN_PRODUCT_MODEL TEXT," +
                "$COLUMN_PRODUCT_PRICE REAL," +
                "$COLUMN_PRODUCT_DESCRIPTION TEXT," +
                "$COLUMN_PRODUCT_CREATED_AT TEXT DEFAULT (datetime('now', 'localtime'))," +
                "$COLUMN_PRODUCT_UPDATED_AT TEXT DEFAULT (datetime('now', 'localtime')));"

        private const val CREATE_TABLE_ORDERS = "CREATE TABLE $TABLE_ORDERS (" +
                "$COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_CUSTOMER_ID INTEGER," +
                "$COLUMN_ORDER_DATE TEXT DEFAULT (datetime('now', 'localtime'))," +
                "$COLUMN_ORDER_TOTAL_AMOUNT REAL," +
                "$COLUMN_REQUEST_ID INTEGER," +
                "FOREIGN KEY($COLUMN_CUSTOMER_ID) REFERENCES $TABLE_CUSTOMERS($COLUMN_CUSTOMER_ID) ON DELETE SET NULL ON UPDATE CASCADE," +
                "FOREIGN KEY($COLUMN_REQUEST_ID) REFERENCES $TABLE_REQUESTS($COLUMN_REQUEST_ID) ON DELETE SET NULL ON UPDATE CASCADE);"
        // ON DELETE NO ACTION yerine SET NULL veya CASCADE daha uygun olabilir, uygulamanızın mantığına bağlı.

        private const val CREATE_TABLE_PRODUCT_IMAGES = "CREATE TABLE $TABLE_PRODUCT_IMAGES (" +
                "$COLUMN_IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PRODUCT_ID INTEGER," +
                "$COLUMN_IMAGE_URL TEXT," +
                "$COLUMN_IMAGE_UPLOADED_AT TEXT DEFAULT (datetime('now', 'localtime'))," +
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

        //Ürünleri, stok miktarlarını ve img leri birlikte görüntüleyen view
        private const val CREATE_VIEW_PRODUCTS_WITH_STOCK =
            "CREATE VIEW IF NOT EXISTS $VIEW_PRODUCTS_WITH_STOCK AS " +
                    "SELECT " +
                    "p.$COLUMN_PRODUCT_ID, " +
                    "p.$COLUMN_PRODUCT_NAME, " +
                    "s.$COLUMN_STOCK_QUANTITY, " +
                    "p.$COLUMN_PRODUCT_BRAND, " +
                    "p.$COLUMN_PRODUCT_PRICE, " +
                    "p.$COLUMN_PRODUCT_DESCRIPTION, " +
                    "pi.$COLUMN_IMAGE_URL " +
                    "FROM $TABLE_PRODUCTS p " +
                    "LEFT JOIN $TABLE_STOCKS s ON p.$COLUMN_PRODUCT_ID = s.$COLUMN_PRODUCT_ID " +
                    "LEFT JOIN $TABLE_PRODUCT_IMAGES pi ON p.$COLUMN_PRODUCT_ID = pi.$COLUMN_PRODUCT_ID"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Yabancı anahtar kısıtlamalarını etkinleştir
        db?.execSQL("PRAGMA foreign_keys=ON;")

        // Tabloları oluşturma
        db?.execSQL(CREATE_TABLE_ADMIN)
        db?.execSQL(CREATE_TABLE_CUSTOMERS)
        db?.execSQL(CREATE_TABLE_REQUESTS)
        db?.execSQL(CREATE_TABLE_PRODUCTS)
        db?.execSQL(CREATE_TABLE_ORDERS)
        db?.execSQL(CREATE_TABLE_PRODUCT_IMAGES)
        db?.execSQL(CREATE_TABLE_STOCKS)
        db?.execSQL(CREATE_TABLE_REQUEST_ITEMS)

        // View oluşturma
        db?.execSQL(CREATE_VIEW_PRODUCTS_WITH_STOCK)

        // İndeksleri oluşturma
        db?.execSQL(CREATE_INDEX_PRODUCT_BRAND)
        db?.execSQL(CREATE_INDEX_PRODUCT_MODEL)
        db?.execSQL(CREATE_INDEX_PRODUCT_NAME)
        db?.execSQL(CREATE_INDEX_STOCK_PRODUCT_ID)
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
    fun addAdmin(username: String, password: String) {
        try {
            val db = this.writableDatabase

            // Aynı kullanıcı adına sahip admin var mı kontrol et
            val cursor = db.rawQuery(
                "SELECT * FROM $TABLE_ADMIN WHERE $COLUMN_ADMIN_USERNAME = ?",
                arrayOf(username)
            )

            if (cursor.count == 0) {
                // Yoksa admin ekle
                db.execSQL(
                    "INSERT INTO $TABLE_ADMIN ($COLUMN_ADMIN_USERNAME, $COLUMN_ADMIN_PASSWORD) VALUES (?, ?)",
                    arrayOf(username, password)
                )
                Log.d("SQL_INFO", "Yeni admin eklendi: $username")
            } else {
                Log.d("SQL_INFO", "Admin zaten var: $username")
            }

            cursor.close()
            db.close()
        } catch (e: Exception) {
            Log.d("SQL_ERROR", e.toString())
        }
    }


    fun foundAdminByUsername(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ADMIN WHERE $COLUMN_ADMIN_USERNAME = ? AND $COLUMN_ADMIN_PASSWORD = ?",
            arrayOf(username, password)
        )
        if (cursor.moveToFirst()) {
            val uname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_USERNAME))
            val pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_PASSWORD))
            Log.d("SQL_RESULT", "Username: $uname, Password: $pass")
            cursor.close()
            return true
        }
        Log.d("SQL_RESULT", "Admin Bulunamadı")
        cursor.close()
        return false
    }

    fun insertRequest(): Long {
        val db = writableDatabase

        // 1. Kaydı ekle (order_confirmation = 0 yani false)
        db.execSQL("INSERT INTO $TABLE_REQUESTS ($COLUMN_REQUEST_ORDER_CONF) VALUES (0)")

        // 2. Eklenen kaydın ID'sini döndür
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        var id = -1L
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0)
        }
        cursor.close()
        return id
    }

    fun insertRequestItem(requestId: Long, productId: Int, price: Double) {
        val db = writableDatabase
        db.execSQL("INSERT INTO $TABLE_REQUEST_ITEMS ($COLUMN_REQUEST_ID, $COLUMN_PRODUCT_ID, $COLUMN_REQUEST_PRICE_AT_PURCHASE) VALUES ($requestId, $productId, $price)")
    }


    fun urunStoktaVarMi(urunId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_STOCKS WHERE $COLUMN_PRODUCT_ID = ?",
            arrayOf(urunId.toString())
        )
        if (cursor.moveToFirst()) {
            cursor.close()
            return true
        }
        cursor.close()
        return false
    }

    /* Ekranda Mesaj görüntülemek için */
    fun showStockAlert(mesaj: String, context: Context, title: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(mesaj)
            .setPositiveButton("Tamam", null)
            .show()
    }

    /* Gerekmedikçe kullanmamak lazım */
    fun execByQuery(query: String): Exception? {
        try {
            val db = this.readableDatabase
            db.execSQL(query)
        } catch (e: Exception) {
            Log.d("SQL_ERROR", e.toString())
            return e
        }
        return null
    }

    fun getAllProducts(): List<Urun> {
        val urunList = mutableListOf<Urun>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $VIEW_PRODUCTS_WITH_STOCK", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                val brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_BRAND))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION))
                var imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL))
                var inStock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY)) > 0

                urunList.add(Urun(id, imageUrl, name, brand, price, description, inStock))

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return urunList
    }

    fun getAllRequests(): MutableList<Request> {
        val requestList = mutableListOf<Request>()
        val db = this.readableDatabase

        val requestQuery = "SELECT * FROM requests"
        val cursor = db.rawQuery(requestQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val requestId = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_REQUEST_ID"))
                val orderConf = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_REQUEST_ORDER_CONF"))

                // Her bir request için ilgili ürünleri al
                val urunList = getUrunlerForRequest(requestId, db)
                val request = Request(requestId, orderConf == 1, urunList)
                requestList.add(request)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return requestList
    }

    private fun getUrunlerForRequest(requestId: Int, db: SQLiteDatabase): MutableList<Urun> {
        val urunList = mutableListOf<Urun>()
        val db = this.readableDatabase
        val urunQuery = "SELECT * FROM $VIEW_PRODUCTS_WITH_STOCK WHERE $COLUMN_PRODUCT_ID IN (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_REQUEST_ITEMS WHERE $COLUMN_REQUEST_ID = $requestId)"


        val urunCursor = db.rawQuery(urunQuery, null)
        if (urunCursor.moveToFirst()) {
            do {
                val urunId = urunCursor.getInt(urunCursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_ID"))
                val urunAdi = urunCursor.getString(urunCursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_NAME"))
                val urunFiyat = urunCursor.getDouble(urunCursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_PRICE"))
                val urunAciklama = urunCursor.getString(urunCursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_DESCRIPTION"))
                val urunResim = urunCursor.getString(urunCursor.getColumnIndexOrThrow("$COLUMN_IMAGE_URL"))
                val urunMarka = urunCursor.getString(urunCursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_BRAND"))

                val stokMiktar = urunCursor.getInt(urunCursor.getColumnIndexOrThrow("$COLUMN_STOCK_QUANTITY"))
                val urun = Urun(urunId, urunResim, urunAdi, urunMarka, urunFiyat, urunAciklama, stokMiktar > 0)

                urunList.add(urun)
            } while (urunCursor.moveToNext())
        }
        urunCursor.close()
        return urunList
    }



    fun getAllFromStocks(): List<StockPhone> {
        val stockList = mutableListOf<StockPhone>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $VIEW_PRODUCTS_WITH_STOCK" // tablo adın buysa doğru, değilse değiştir
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val product_id = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_ID"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_PRODUCT_NAME"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_STOCK_QUANTITY"))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_IMAGE_URL"))

                val phone = StockPhone(

                    product_id = product_id,
                    name = name,
                    quantity = quantity,
                    imageUrl = imageUrl
                )
                stockList.add(phone)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return stockList
    }

    fun initializeDatabaseIfNeeded(context: Context) {
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isDataInserted = sharedPref.getBoolean("isDataInserted", false)

        if (!isDataInserted) {
            addSampleProductsAndStocks()
            sharedPref.edit().putBoolean("isDataInserted", true).apply()
            Log.d("DB_INIT", "Sample data inserted.")
        } else {
            Log.d("DB_INIT", "Sample data already inserted. Skipping.")
        }
    }



    fun addSampleProductsAndStocks() {
        val db = this.writableDatabase

        try {
            // Ürünler
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Samsung Galaxy S21', 'Samsung', 'Galaxy S21', 14999.99, 'Amiral gemisi Samsung telefonu.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('iPhone 14 Pro', 'Apple', 'iPhone 14 Pro', 45999.99, 'Apple''ın son çıkan üst düzey telefonu.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Xiaomi Mi 12 Lite', 'Xiaomi', 'Mi 12 Lite', 10999.99, 'Fiyat/performans odaklı akıllı telefon.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('OnePlus 11', 'OnePlus', '11', 17999.99, 'Yüksek performanslı amiral gemisi telefon.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Google Pixel 7', 'Google', 'Pixel 7', 16999.99, 'Google kamerasıyla öne çıkan telefon.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Huawei P40 Pro', 'Huawei', 'P40 Pro', 13999.99, 'Google servisleri olmayan ama güçlü bir cihaz.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Realme GT Neo 3', 'Realme', 'GT Neo 3', 9999.99, 'Fiyat/performans canavarı.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Motorola Edge 30', 'Motorola', 'Edge 30', 8999.99, 'İnce tasarım ve saf Android deneyimi.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Nokia G50', 'Nokia', 'G50', 6499.99, 'Dayanıklı ve uygun fiyatlı telefon.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Asus ROG Phone 6', 'Asus', 'ROG Phone 6', 24999.99, 'Oyuncular için özel olarak tasarlandı.');")

            // Stoklar
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (15, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Galaxy S21' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (8, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'iPhone 14 Pro' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (20, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Mi 12 Lite' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (10, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = '11' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (12, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Pixel 7' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (5, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'P40 Pro' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (18, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'GT Neo 3' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (9, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Edge 30' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (14, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'G50' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (7, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'ROG Phone 6' LIMIT 1));")

            // Resimler
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Galaxy S21' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/samsung/samsung-galaxy-s21-5g-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'iPhone 14 Pro' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/apple/apple-iphone-14-pro-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Mi 12 Lite' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/xiaomi/xiaomi-12-lite-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = '11' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/oneplus/oneplus-11-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Pixel 7' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/google/google-pixel7-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'P40 Pro' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/huawei/huawei-p40-pro-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'GT Neo 3' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/realme/realme-gt-neo3-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'Edge 30' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/motorola/motorola-edge-30-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'G50' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/nokia/nokia-g50-1.jpg');")
            db.execSQL("INSERT INTO $TABLE_PRODUCT_IMAGES ($COLUMN_PRODUCT_ID, $COLUMN_IMAGE_URL) VALUES ((SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_MODEL = 'ROG Phone 6' LIMIT 1), 'https://fdn2.gsmarena.com/vv/pics/asus/asus-rog-phone6-1.jpg');")
        } catch (e: Exception) {
            Log.d("SQL_ERROR", e.toString())
        }
    }
    // Örnek: Basit SQLite Helper içinde güncelleme fonksiyonu
    fun updateStockQuantity(product_id: Int, newQuantity: Int): Boolean {
        return try {
            val db = writableDatabase
            val query = "UPDATE $TABLE_STOCKS SET quantity = ? WHERE product_id = ?"
            db.execSQL(query, arrayOf(newQuantity, product_id))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteProductById(id: Int): Boolean {
        return try {
            val db = this.writableDatabase
            val sql = "DELETE FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_ID = $id"
            db.execSQL(sql)
            db.close()
            true
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Silme hatası: ${e.message}")
            false
        }
    }



}
