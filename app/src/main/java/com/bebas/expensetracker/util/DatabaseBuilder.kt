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
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigrationOnDowngrade() // opsional
        .build()
}

// Contoh migrasi dari versi 1 ke 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Contoh: Menambah kolom "notes" pada tabel expenses
        database.execSQL(
            "ALTER TABLE expenses ADD COLUMN notes TEXT"
        )
    }
}