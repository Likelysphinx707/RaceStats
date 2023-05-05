package com.example.racestats

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ServiceRecords : AppCompatActivity() {
    private lateinit var addButton: Button
    private lateinit var newRecordLayout: LinearLayout
    private lateinit var serviceEditText: EditText
    private lateinit var mileageEditText: EditText
    private lateinit var dateEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_records)

        // find the service_records_list LinearLayout
        val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)

        // find the "Add New" button
        addButton = findViewById(R.id.add_new_button)

        // find the EditTexts
        serviceEditText = findViewById(R.id.service_edittext)
        mileageEditText = findViewById(R.id.mileage_edittext)
        dateEditText = findViewById(R.id.date_edittext)

        // find the LinearLayout for new record
        newRecordLayout = findViewById(R.id.new_record_layout)

        // set onClickListener on the button
//        addButton.setOnClickListener {
//            // toggle the visibility of the new record layout
//            if (newRecordLayout.visibility == View.GONE) {
//                newRecordLayout.visibility = View.VISIBLE
//            } else {
//                // create a new LinearLayout to represent the record
//                val recordLayout = LinearLayout(this)
//                recordLayout.orientation = LinearLayout.HORIZONTAL
//                recordLayout.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//
//                // create three TextViews to display the service, mileage, and date
//                val serviceTextView = TextView(this)
//                serviceTextView.text = serviceEditText.text
//                serviceTextView.setTextColor(resources.getColor(R.color.white))
//                serviceTextView.textSize = 18f
//                serviceTextView.layoutParams = LinearLayout.LayoutParams(
//                    0,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    1F
//                )
//                serviceTextView.gravity = Gravity.CENTER // set text alignment to center
//
//                val mileageTextView = TextView(this)
//                mileageTextView.text = mileageEditText.text
//                mileageTextView.setTextColor(resources.getColor(R.color.white))
//                mileageTextView.textSize = 18f
//                mileageTextView.layoutParams = LinearLayout.LayoutParams(
//                    0,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    1F
//                )
//                mileageTextView.gravity = Gravity.CENTER // set text alignment to center
//
//                val dateTextView = TextView(this)
//                dateTextView.text = dateEditText.text
//                dateTextView.setTextColor(resources.getColor(R.color.white))
//                dateTextView.textSize = 18f
//                dateTextView.layoutParams = LinearLayout.LayoutParams(
//                    0,
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    1F
//                )
//                dateTextView.gravity = Gravity.CENTER // set text alignment to center
//
//                // add the TextViews to the record LinearLayout
//                recordLayout.addView(serviceTextView)
//                recordLayout.addView(mileageTextView)
//                recordLayout.addView(dateTextView)
//
//                // add the record LinearLayout to the service_records_list LinearLayout
//                serviceRecordsList.addView(recordLayout)
//            }
//        }

        addButton.setOnClickListener {
            // Toggle the visibility of the new record layout
            if (newRecordLayout.visibility == View.GONE) {
                newRecordLayout.visibility = View.VISIBLE
            } else {
                // Create a new LinearLayout to represent the record
                val recordLayout = LinearLayout(this)
                recordLayout.orientation = LinearLayout.HORIZONTAL
                recordLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Create three TextViews to display the service, mileage, and date
                val serviceTextView = TextView(this)
                serviceTextView.text = serviceEditText.text
                serviceTextView.setTextColor(resources.getColor(R.color.white))
                serviceTextView.textSize = 18f
                serviceTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                serviceTextView.gravity = Gravity.CENTER // set text alignment to center

                val mileageTextView = TextView(this)
                mileageTextView.text = mileageEditText.text
                mileageTextView.setTextColor(resources.getColor(R.color.white))
                mileageTextView.textSize = 18f
                mileageTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                mileageTextView.gravity = Gravity.CENTER // set text alignment to center

                val dateTextView = TextView(this)
                dateTextView.text = dateEditText.text
                dateTextView.setTextColor(resources.getColor(R.color.white))
                dateTextView.textSize = 18f
                dateTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                dateTextView.gravity = Gravity.CENTER // set text alignment to center

                // Check if all three EditText fields have values
                if (serviceEditText.text.isNotEmpty() || mileageEditText.text.isNotEmpty() || dateEditText.text.isNotEmpty()) {
                    // Add the TextViews to the record LinearLayout
                    recordLayout.addView(serviceTextView)
                    recordLayout.addView(mileageTextView)
                    recordLayout.addView(dateTextView)
                } else {
                    // Show a message to the user indicating that all three fields are required
                    Toast.makeText(this, "Please fill in at least one field to create a new record", Toast.LENGTH_LONG).show();
                }

                // Add the record LinearLayout to the service_records_list LinearLayout
                serviceRecordsList.addView(recordLayout)

                // Reset the EditText fields and hide the new record layout
                serviceEditText.text.clear()
                mileageEditText.text.clear()
                dateEditText.text.clear()
                newRecordLayout.visibility = View.GONE
            }
        }
    }
}
