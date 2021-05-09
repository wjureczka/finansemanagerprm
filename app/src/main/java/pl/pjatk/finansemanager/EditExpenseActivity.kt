package pl.pjatk.finansemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.datepicker.MaterialDatePicker
import pl.pjatk.finansemanager.db.Expense
import pl.pjatk.finansemanager.db.ExpenseDb
import java.text.SimpleDateFormat
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private val db by lazy {
        ExpenseDb.open(this)
    }

    private lateinit var expense: Expense;

    private lateinit var dateInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        prepareForm()

        dateInput = findViewById(R.id.input_expense_date)
        dateInput.setOnFocusChangeListener { view: View, b: Boolean ->
            dateInput.showSoftInputOnFocus = false
            if (b) handleDateInputClick()
        }

        findViewById<Button>(R.id.button_expense_apply_changes).setOnClickListener {
            handleFormSubmit()
        }

        findViewById<Button>(R.id.button_share_expense).setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out my last expense! Place: ${expense.placeName}, category: ${expense.categoryName}, date: ${
                    SimpleDateFormat("dd MMM yyyy").format(
                        expense.date
                    )
                }, balance: ${expense.balance}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }

    private fun showValidationAlert() {
        AlertDialog.Builder(this)
            .setTitle("All fields are required")
            .setMessage("Please fill all fields to add expense")
            .setPositiveButton("Ok", null)
            .setIcon(android.R.drawable.btn_plus)
            .show()
    }

    private fun prepareForm() {
        val expenseId = intent.getIntExtra("EXPENSE_ID", 0).toInt()
        val expensePlaceName = intent.getStringExtra("EXPENSE_PLACE_NAME").toString()
        val expenseCategoryName = intent.getStringExtra("EXPENSE_CATEGORY_NAME").toString()
        val expenseDate = Date(intent.getStringExtra("EXPENSE_DATE").toString())
        val expenseBalance = intent.getDoubleExtra("EXPENSE_BALANCE", 0.0).toDouble()

        val convertedExpense =
            Expense(expenseId, expensePlaceName, expenseCategoryName, expenseDate, expenseBalance)
        expense = convertedExpense

        findViewById<EditText>(R.id.input_expense_place_name).setText(expense.placeName)
        findViewById<EditText>(R.id.input_expense_category_name).setText(expense.categoryName)
        findViewById<EditText>(R.id.input_expense_date).setText(
            SimpleDateFormat("dd MMM yyyy").format(
                expense.date
            ).toString()
        )
        findViewById<EditText>(R.id.input_expense_balance).setText(expense.balance.toString())
    }

    private fun handleFormSubmit() {
        val placeName = findViewById<EditText>(R.id.input_expense_place_name).text.toString()
        val categoryName = findViewById<EditText>(R.id.input_expense_category_name).text.toString()
        val balance = findViewById<EditText>(R.id.input_expense_balance).text.toString().toDoubleOrNull()

        if (placeName == "" || categoryName == "" || balance == null) {
            showValidationAlert()
            return
        }

        runOnUiThread {
            expense.placeName = placeName
            expense.categoryName = categoryName
            expense.balance = balance

            db.expenses().update(expense)

            AlertDialog.Builder(this)
                .setTitle("Expense updated")
                .setPositiveButton("Ok", null)
                .setIcon(android.R.drawable.btn_plus)
                .show()
        }
    }

    private fun handleDateInputClick() {
        println("Handling date input click")

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.expense_choose_date);
        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            expense.date = Date(picker.selection!!.toLong())
            dateInput.setText(picker.headerText)
            dateInput.clearFocus()
            picker.dismiss()
        }
    }
}