package com.example.racestats

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ServiceRecords : AppCompatActivity() {
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
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
        editButton = findViewById(R.id.edit_button)
        deleteButton = findViewById(R.id.delete_button)

        // find the EditTexts
        serviceEditText = findViewById(R.id.service_edittext)
        mileageEditText = findViewById(R.id.mileage_edittext)
        dateEditText = findViewById(R.id.date_edittext)

        // find the LinearLayout for new record
        newRecordLayout = findViewById(R.id.new_record_layout)

        // set onClickListener on the button
        addButton.setOnClickListener {
            // Toggle the visibility of the new record layout
            if (newRecordLayout.visibility == View.GONE) {
                newRecordLayout.visibility = View.VISIBLE
                addButton.text = "Confirm"
                editButton.text = "Cancel"
                deleteButton.visibility = View.GONE
                editButton.setOnClickListener {
                    if (newRecordLayout.visibility == View.VISIBLE) {
                        // If the new record layout is visible, hide it and reset the button text
                        newRecordLayout.visibility = View.GONE
                        addButton.text = "Add New"
                        editButton.text = "Edit"
                        deleteButton.visibility = View.VISIBLE
                        Toast.makeText(this, "Canceled New Record", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                // Change the addButtons text from add New to save
                addButton.text = "Save"

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
                serviceTextView.textSize = 22f
                serviceTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                serviceTextView.gravity = Gravity.CENTER // set text alignment to center

                val mileageTextView = TextView(this)
                mileageTextView.text = mileageEditText.text
                mileageTextView.setTextColor(resources.getColor(R.color.white))
                mileageTextView.textSize = 22f
                mileageTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                mileageTextView.gravity = Gravity.CENTER // set text alignment to center

                val dateTextView = TextView(this)
                dateTextView.text = dateEditText.text
                dateTextView.setTextColor(resources.getColor(R.color.white))
                dateTextView.textSize = 22f
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

                    // Let the user know that the record was added successfully
                    Toast.makeText(this, "Record Added Successfully!", Toast.LENGTH_LONG).show();
                } else {
                    // Show a message to the user indicating that all three fields are required
                    Toast.makeText(this, "Please fill in at least one field to create a new record", Toast.LENGTH_LONG).show();
                }

                // Add the record LinearLayout to the service_records_list LinearLayout
                serviceRecordsList.addView(recordLayout)

                // Reset the EditText fields, hide the new record layout and set "Save" back to "Add New"
                addButton.text = "Add New"

                serviceEditText.text.clear()
                mileageEditText.text.clear()
                dateEditText.text.clear()
                newRecordLayout.visibility = View.GONE

                // Reset the layout and button text
                newRecordLayout.visibility = View.GONE
                addButton.text = "Add New"
                editButton.text = "Edit"
                deleteButton.visibility = View.VISIBLE
            }
        }
    }
}
