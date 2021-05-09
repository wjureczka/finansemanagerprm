package pl.pjatk.finansemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.pjatk.finansemanager.db.Expense
import java.text.SimpleDateFormat

class ExpenseListAdapter(private val dataSet: List<Expense>, private val onItemClicked: (Expense) -> Unit, private val onLongItemClick: (Expense) -> Unit): RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val expenseLocation: TextView
        val expenseCategory: TextView
        val expenseDate: TextView
        val expenseBalance: TextView

        init {
            expenseLocation = view.findViewById(R.id.expense_place_name)
            expenseCategory = view.findViewById(R.id.expense_category_name)
            expenseDate = view.findViewById(R.id.expense_date)
            expenseBalance = view.findViewById(R.id.expense_balance)
        }

        fun bind(expense: Expense, onItemClicked: (Expense) -> Unit) {
            this.itemView.setOnClickListener { onItemClicked(expense) }
        }

        fun bindLongClick(expense: Expense, onLongItemClick: (Expense) -> Unit) {
            this.itemView.setOnLongClickListener {
                onLongItemClick(expense)
                true
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.expense_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.expenseLocation.text = dataSet[position].placeName
        viewHolder.expenseCategory.text = dataSet[position].categoryName

        viewHolder.expenseDate.text = SimpleDateFormat("dd/MM/yyyy").format(dataSet[position].date)
        viewHolder.expenseBalance.text = dataSet[position].balance.toString()
        viewHolder.bind(dataSet[position], onItemClicked)
        viewHolder.bindLongClick(dataSet[position], onLongItemClick)
    }

    override fun getItemCount() = dataSet.size
}