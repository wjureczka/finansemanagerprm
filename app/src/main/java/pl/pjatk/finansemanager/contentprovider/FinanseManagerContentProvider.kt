package pl.pjatk.finansemanager.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import pl.pjatk.finansemanager.db.Expense
import pl.pjatk.finansemanager.db.ExpenseDao
import pl.pjatk.finansemanager.db.ExpenseDb
import java.lang.NullPointerException
import java.util.*

class FinanseManagerContentProvider : ContentProvider() {

    companion object config {
        const val TABLE_NAME = "expenses"
        const val PROVIDER_NAME = "pl.pjatk.finansemanager.contentprovider"
        const val URL = "content://" + PROVIDER_NAME + "/$TABLE_NAME"
        val URI = Uri.parse(URL)
        val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(PROVIDER_NAME, TABLE_NAME, EXPENSES)
            addURI(PROVIDER_NAME, "$TABLE_NAME/#", EXPENSES_ID)
        }
        const val EXPENSES = 1
        const val EXPENSES_ID = 2
    }

    private lateinit var db: ExpenseDb

    private var expenseDao: ExpenseDao? = null

    override fun onCreate(): Boolean {
        db = ExpenseDb.open(context!!)

        expenseDao = db.expenses()

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            EXPENSES -> {
                db.expenses().getAllWithCursor()
            }
            EXPENSES_ID -> {
                db.expenses().getExpenseWithCursor(ContentUris.parseId(uri).toInt())
            }
            else -> {
                throw NullPointerException("Path not found")
            }
        }

        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri)
        val id: Long;

        if (values == null) {
            throw IllegalArgumentException("No values")
        }

        when (uriType) {
            EXPENSES -> id = db.expenses().insert(
                Expense(
                    0,
                    values.getAsString("placeName"),
                    values.getAsString("categoryName"),
                    Date(values.getAsString("date")),
                    values.getAsDouble("balance")
                )
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)
        return Uri.parse("$TABLE_NAME/$id")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}