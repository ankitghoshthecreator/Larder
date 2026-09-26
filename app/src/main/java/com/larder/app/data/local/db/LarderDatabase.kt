package com.larder.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.larder.app.data.local.dao.HouseholdDao
import com.larder.app.data.local.dao.ItemDao
import com.larder.app.data.local.dao.ReceiptScanDao
import com.larder.app.domain.model.Household
import com.larder.app.domain.model.HouseholdMember
import com.larder.app.domain.model.Item
import com.larder.app.domain.model.ReceiptScan

@Database(
    entities = [
        Household::class,
        HouseholdMember::class,
        Item::class,
        ReceiptScan::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LarderDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun receiptScanDao(): ReceiptScanDao
    abstract fun householdDao(): HouseholdDao

    companion object {
        @Volatile
        private var INSTANCE: LarderDatabase? = null

        fun getDatabase(context: Context): LarderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LarderDatabase::class.java,
                    "larder_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
