package pl.pjatk.finansemanager.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Expense(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var placeName: String,
    var categoryName: String,
    var date: Date,
    var balance: Double
) {

}