package pl.pjatk.finansemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [Expense::class])
@TypeConverters(pl.pjatk.finansemanager.db.TypeConverters::class)
abstract class ExpenseDb: RoomDatabase() {
    abstract fun expenses(): ExpenseDao

    companion object {

        private var db: ExpenseDb? = null

        fun open(context: Context): ExpenseDb {
            if (db == null) {
                val newDb = Room.databaseBuilder(context, ExpenseDb::class.java, "expenses").allowMainThreadQueries().build()
                db = newDb
                return newDb
            }

            return db as ExpenseDb
        }
    }
}