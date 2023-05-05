package com.example.racestats

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServiceRecords : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_records)

        // find the service_records_list LinearLayout
        val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)

        // find the "Add New" button
//        val addButton = findViewById<Button>(R.id.)

        // set onClickListener on the button
//        addButton.setOnClickListener {
//            // create a new LinearLayout to represent the record
//            val recordLayout = LinearLayout(this)
//            recordLayout.orientation = LinearLayout.HORIZONTAL
//            recordLayout.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//
//            // create three TextViews to display the service, mileage, and date
//            val serviceTextView = TextView(this)
//            serviceTextView.text = "Service Done"
//            serviceTextView.setTextColor(resources.getColor(R.color.white))
//            serviceTextView.textSize = 18f
//            serviceTextView.layoutParams = LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1F
//            )
//            serviceTextView.gravity = Gravity.CENTER // set text alignment to center
//
//            val mileageTextView = TextView(this)
//            mileageTextView.text = "Mileage"
//            mileageTextView.setTextColor(resources.getColor(R.color.white))
//            mileageTextView.textSize = 18f
//            mileageTextView.layoutParams = LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1F
//            )
//            mileageTextView.gravity = Gravity.CENTER // set text alignment to center
//
//            val dateTextView = TextView(this)
//            dateTextView.text = "Date Completed"
//            dateTextView.setTextColor(resources.getColor(R.color.white))
//            dateTextView.textSize = 18f
//            dateTextView.layoutParams = LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1F
//            )
//            dateTextView.gravity = Gravity.CENTER // set text alignment to center
//
//            // add the TextViews to the record LinearLayout
//            recordLayout.addView(serviceTextView)
//            recordLayout.addView(mileageTextView)
//            recordLayout.addView(dateTextView)
//
//            // add the record LinearLayout to the service_records_list LinearLayout
//            serviceRecordsList.addView(recordLayout)
//        }
    }
}
