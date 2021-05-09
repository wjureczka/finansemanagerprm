package pl.pjatk.finansemanager

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.pjatk.finansemanager.contentprovider.FinanseManagerContentProvider
import pl.pjatk.finansemanager.db.Expense
import pl.pjatk.finansemanager.db.ExpenseDb
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        ExpenseDb.open(this)
    }

    private fun createMonthsSummary() {
        val monthlyCashBalanceElement = findViewById<TextView>(R.id.monthly_cash_balance)
        val summaryMonth = findViewById<TextView>(R.id.summary_month)
        val currentMonth = Date().month

        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMM")
        val month_name: String = month_date.format(cal.time)

        summaryMonth.text = "Summary for $month_name:"

        runOnUiThread {
            val expenses = db.expenses().getAll()

            if (expenses.isEmpty()) {
                monthlyCashBalanceElement.text = "0"
                return@runOnUiThread;
            }

            val currentMonthExpenses = expenses.filter { it.date.month == currentMonth }

            if (currentMonthExpenses.isEmpty()) {
                monthlyCashBalanceElement.text = "0"
                return@runOnUiThread;
            }

            val balanceForCurrentMonth =
                currentMonthExpenses.filter { it.date.month == currentMonth }.map { it.balance }
                    .reduce { acc, d -> 0 + acc + d }

            monthlyCashBalanceElement.text = balanceForCurrentMonth.toString()
        }
    }

    private fun loadExpensesList() {
        val recyclerView = findViewById<RecyclerView>(R.id.expense_list)

        recyclerView.layoutManager = LinearLayoutManager(this)

        runOnUiThread {
            val expenses = db.expenses().getAll()

            val expenseClickListener: (Expense) -> Unit = {
                val newIntent = Intent(this, EditExpenseActivity::class.java)
                newIntent.putExtra("EXPENSE_ID", it.id)
                newIntent.putExtra("EXPENSE_PLACE_NAME", it.placeName)
                newIntent.putExtra("EXPENSE_CATEGORY_NAME", it.categoryName)
                newIntent.putExtra("EXPENSE_DATE", it.date.toString())
                newIntent.putExtra("EXPENSE_BALANCE", it.balance)

                startActivity(newIntent)
            }

            val expenseLongClickListener: (Expense) -> Unit = {
                handleExpenseDelete(it)
            }

            recyclerView.adapter =
                ExpenseListAdapter(expenses, expenseClickListener, expenseLongClickListener)
        }
    }

    private fun printFromCursor(cursor: Cursor) {
        println(
            "${cursor.getString(cursor.getColumnIndex("id"))} - ${
                cursor.getString(
                    cursor.getColumnIndex(
                        "placeName"
                    )
                )
            }"
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testContentProvider() {
        val cursor =
            contentResolver.query(Uri.parse(FinanseManagerContentProvider.URL), null, null, null)

        if (cursor == null) {
            println("No cursor")
            return
        }

        if (!cursor.moveToFirst()) {
            return;
        }
        println("Cursor below")
        printFromCursor(cursor)
        cursor.moveToLast()
        printFromCursor(cursor)
        println("Cursor above")

        val cursorForSingle = contentResolver.query(
            Uri.parse(FinanseManagerContentProvider.URL + "/595"),
            null,
            null,
            null
        )

        if (cursorForSingle == null) {
            println("No cursor for single")
            return
        }

        if (!cursorForSingle.moveToFirst()) {
            println("move to first false")
            return
        }
        println("Single cursor below")
        printFromCursor(cursorForSingle)
        println("Single cursor above")
    }

    private fun handleExpenseDelete(expense: Expense) {
        AlertDialog.Builder(this)
            .setTitle("Delete expense")
            .setMessage("Are you sure you want to delete expense?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                runOnUiThread {
                    db.expenses().delete(expense)
                    loadExpensesList()
                    createMonthsSummary()
                }
            })
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.btn_minus)
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateData() {
        val selectedDate = Date()
        val daysInMonth = YearMonth.of(selectedDate.year, selectedDate.month).lengthOfMonth();

        val tempList = List(daysInMonth) {
            it
        }

        tempList.forEach {
            runOnUiThread {
                val date = Date(selectedDate.year, selectedDate.month, it)
                val tempBalance = Random().nextInt(5) * 100.00 + 400.00
                val tempExpense = Expense(0, "placename $it", "categoryname $it", date, tempBalance)
                db.expenses().insert(tempExpense)
            }
        }
    }

    private fun nukeDb() {
        runOnUiThread { db.expenses().nukeTable() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()

        loadExpensesList()
        createMonthsSummary()
        testContentProvider()

        findViewById<ImageButton>(R.id.add_expense_button).setOnClickListener {
            val newIntent = Intent(this, AddExpenseActivity::class.java)

            startActivity(newIntent)
        }

        findViewById<ImageButton>(R.id.show_chart_button).setOnClickListener {
            val newIntent = Intent(this, ExpensesChartActivity::class.java)

            startActivity(newIntent)
        }

//        nukeDb()
//        generateData()
    }
}