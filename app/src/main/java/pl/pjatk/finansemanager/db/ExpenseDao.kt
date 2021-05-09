package pl.pjatk.finansemanager.db

import android.database.Cursor
import androidx.room.*

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expense ORDER BY Expense.date DESC")
    fun getAll(): List<Expense>

    @Query("SELECT * FROM Expense ORDER BY Expense.date DESC")
    fun getAllWithCursor(): Cursor

    @Query("SELECT * FROM EXPENSE WHERE Expense.id = :expenseId")
    fun getExpense(expenseId: Int): Expense

    @Query("SELECT * FROM EXPENSE WHERE Expense.id = :expenseId")
    fun getExpenseWithCursor(expenseId: Int): Cursor

    @Query("DELETE FROM EXPENSE")
    fun nukeTable();

    @Insert
    fun insert(expense: Expense): Long

    @Delete
    fun delete(expense: Expense)

    @Update
    fun update(expense: Expense)
}