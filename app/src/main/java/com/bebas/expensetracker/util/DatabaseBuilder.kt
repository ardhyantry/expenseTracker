package com.bebas.expensetracker.util

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bebas.expensetracker.model.AppDatabase

const val DB_NAME = "expense_tracker_db"

fun buildDb(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DB_NAME
    )
        .fallbackToDestructiveMigration()
        .build()
}

// Contoh migrasi dari versi 1 ke 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Tambahkan kolom notes ke expenses (jika kamu tetap butuh)
        database.execSQL("ALTER TABLE expenses ADD COLUMN notes TEXT")

        // ✅ Tambahkan kolom userId ke budgets
        database.execSQL("ALTER TABLE budgets ADD COLUMN userId INTEGER NOT NULL DEFAULT 0")
    }
}
