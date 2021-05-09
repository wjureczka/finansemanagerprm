package pl.pjatk.finansemanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import pl.pjatk.finansemanager.db.ExpenseDb
import java.time.YearMonth
import java.util.*

class ExpensesChartActivity : AppCompatActivity() {

    private var selectedDate: Date = Date()

    private val db by lazy {
        ExpenseDb.open(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_chart)

        val graphView = findViewById<ExpensesGraph>(R.id.graph_view)
        graphView.setData(prepareExpensesData())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareExpensesData(): List<DataPoint> {
        val expenses  = db.expenses().getAll().filter {
            it.date.month == selectedDate.month
        }

        val daysInMonth = YearMonth.of(selectedDate.year, selectedDate.month + 1).lengthOfMonth();
        val dataSet = List(daysInMonth) { index -> DataPoint(index, 0) }

        expenses.forEachIndexed { _, expense ->
            val dayOfExpense = expense.date.date - 1
            val dataPoint = dataSet[dayOfExpense]
            dataPoint.yVal = dataPoint.yVal + expense.balance.toInt()
        }

        return dataSet
    }
}