package pl.pjatk.finansemanager

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import pl.pjatk.finansemanager.db.ExpenseDb
import java.text.SimpleDateFormat
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

        val monthButton = findViewById<Button>(R.id.chart_select_month_button)
        monthButton.setOnClickListener {
            handleDateInputClick()
        }

        prepareSelectedMonthTextView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleDateInputClick() {
        println("Handling date input click")
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.select_date);
        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            if (picker.selection == null) {
                return@addOnPositiveButtonClickListener
            }

            val graphView = findViewById<ExpensesGraph>(R.id.graph_view)
            graphView.setData(prepareExpensesData())

            selectedDate = Date(picker.selection!!)
            graphView.setData(prepareExpensesData())
            prepareSelectedMonthTextView()
            picker.dismiss()
        }
    }

    private fun prepareSelectedMonthTextView() {
        val month_date = SimpleDateFormat("MMMM")
        val month_name: String = month_date.format(selectedDate.time)

        val selectedMonthTextView = findViewById<TextView>(R.id.chart_selected_month)
        selectedMonthTextView.text = "Chart for $month_name"
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