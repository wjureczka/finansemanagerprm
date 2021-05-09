package pl.pjatk.finansemanager

import android.content.DialogInterface
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
import java.util.*
import kotlin.properties.Delegates

class AddExpenseActivity : AppCompatActivity() {

    private val db by lazy {
        ExpenseDb.open(this)
    }

    private lateinit var dateInput: EditText

    private var dateInputValue: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        dateInput = findViewById(R.id.input_expense_date)
        dateInput.setOnFocusChangeListener { view: View, b: Boolean ->
            dateInput.showSoftInputOnFocus = false
            if (b) handleDateInputClick()
        }

        findViewById<Button>(R.id.button_submit_expense_form).setOnClickListener {
            handleFormSubmit()
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

    private fun handleFormSubmit() {
        val placeName = findViewById<EditText>(R.id.input_expense_place_name).text.toString()
        val categoryName = findViewById<EditText>(R.id.input_expense_category_name).text.toString()
        val balance = findViewById<EditText>(R.id.input_expense_balance).text.toString().toDoubleOrNull()

        if (placeName == "" || categoryName == "" || balance == null || dateInputValue == null) {
            showValidationAlert()
            return
        }

        runOnUiThread {
            db.expenses().insert(Expense(0, placeName, categoryName, Date(dateInputValue!!), balance))
        }

        val newItent = Intent(this, MainActivity::class.java)

        startActivity(newItent)
    }

    private fun handleDateInputClick() {
        println("Handling date input click")

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.expense_choose_date);
        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            dateInputValue = picker.selection
            dateInput.setText(picker.headerText)
            dateInput.clearFocus()
            picker.dismiss()
        }
    }
}