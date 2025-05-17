package com.mezoxy.mobilbayii

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dataClasses.StockPhone
import dataClasses.Urun
import java.util.concurrent.ExecutionException

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

        //Views
        const val VIEW_PRODUCTS_WITH_STOCK = "view_products_with_stock"


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

        //Ürünleri ve onların stok miktarlarını birlikte görüntüleyen view
        private const val CREATE_VIEW_PRODUCTS_WITH_STOCK =
            "CREATE VIEW IF NOT EXISTS $VIEW_PRODUCTS_WITH_STOCK AS " +
                    "SELECT " +
                    "p.$COLUMN_PRODUCT_ID, " +
                    "p.$COLUMN_PRODUCT_NAME, " +
                    "p.$COLUMN_PRODUCT_BRAND, " +
                    "p.$COLUMN_PRODUCT_MODEL, " +
                    "p.$COLUMN_PRODUCT_PRICE, " +
                    "p.$COLUMN_PRODUCT_DESCRIPTION, " +
                    "p.$COLUMN_PRODUCT_CREATED_AT, " +
                    "p.$COLUMN_PRODUCT_UPDATED_AT, " +
                    "s.$COLUMN_STOCK_QUANTITY AS stock_quantity " +
                    "FROM $TABLE_PRODUCTS p " +
                    "LEFT JOIN $TABLE_STOCKS s ON p.$COLUMN_PRODUCT_ID = s.$COLUMN_PRODUCT_ID"


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
        db?.execSQL(CREATE_VIEW_PRODUCTS_WITH_STOCK)

        // İsteğe bağlı: Başlangıç verileri ekleyebilirsiniz (örneğin, varsayılan bir admin)
        //addInitialAdmin(db)
        //addSampleProductsAndStocks(db) // Örnek ürün ve stok eklemek için
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

    fun deleteByQuery(query: String): Exception? {
        try {
            val db = this.readableDatabase
            db.execSQL(query)
        } catch (e: Exception) {
            Log.d("SQL_ERROR", e.toString())
            return e
        }
        return null
    }

    fun addSampleProductsAndStocks() {
        val db = this.writableDatabase

        try {
            // Ürünler
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Akıllı Telefon', 'Samsung', 'Galaxy S21', 14999.99, 'Amiral gemisi Samsung telefonu.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Akıllı Telefon', 'Apple', 'iPhone 14 Pro', 45999.99, 'Apple''ın son çıkan üst düzey telefonu.');")
            db.execSQL("INSERT INTO $TABLE_PRODUCTS ($COLUMN_PRODUCT_NAME, $COLUMN_PRODUCT_BRAND, $COLUMN_PRODUCT_MODEL, $COLUMN_PRODUCT_PRICE, $COLUMN_PRODUCT_DESCRIPTION) VALUES ('Akıllı Telefon', 'Xiaomi', 'Mi 12 Lite', 10999.99, 'Fiyat/performans odaklı akıllı telefon.');")

            // Stoklar
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (15, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_NAME = 'Akıllı Telefon' AND $COLUMN_PRODUCT_BRAND = 'Samsung' AND $COLUMN_PRODUCT_MODEL = 'Galaxy S21' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (8, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_NAME = 'Akıllı Telefon' AND $COLUMN_PRODUCT_BRAND = 'Apple' AND $COLUMN_PRODUCT_MODEL = 'iPhone 14 Pro' LIMIT 1));")
            db.execSQL("INSERT INTO $TABLE_STOCKS ($COLUMN_STOCK_QUANTITY, $COLUMN_PRODUCT_ID) VALUES (20, (SELECT $COLUMN_PRODUCT_ID FROM $TABLE_PRODUCTS WHERE $COLUMN_PRODUCT_NAME = 'Akıllı Telefon' AND $COLUMN_PRODUCT_BRAND = 'Xiaomi' AND $COLUMN_PRODUCT_MODEL = 'Mi 12 Lite' LIMIT 1));")
        } catch (e: Exception) { Log.d("SQL_ERROR", e.toString()) }
    }

}
