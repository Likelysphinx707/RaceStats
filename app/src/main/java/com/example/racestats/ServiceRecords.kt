package com.example.racestats

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ServiceRecords : AppCompatActivity() {
    // Declare private properties for the buttons, layouts, and EditTexts
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var newRecordLayout: LinearLayout
    private lateinit var serviceEditText: EditText
    private lateinit var mileageEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var recordViews: MutableList<View>
    private lateinit var deleteButtons: MutableList<Button>
    private lateinit var idList: MutableList<Long>
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_records)

        // Initialize the recordViews and deleteButtons lists
        recordViews = mutableListOf()
        deleteButtons = mutableListOf()
        idList = mutableListOf()

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

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
                        Toast.makeText(this, "Canceled New Record", Toast.LENGTH_LONG).show()
                    } else {
                        // Enable editing of the existing records
                        for (recordView in recordViews) {
                            val serviceTextView =
                                recordView.findViewById<TextView>(R.id.service_textview)
                            val mileageTextView =
                                recordView.findViewById<TextView>(R.id.mileage_textview)
                            val dateTextView = recordView.findViewById<TextView>(R.id.date_textview)

                            // Enable editing of the record values
                            serviceTextView.isEnabled = true
                            mileageTextView.isEnabled = true
                            dateTextView.isEnabled = true
                        }

                        editButton.text = "Save"
                    }
                }
            } else {
                // Change the addButton's text from "Add New" to "Save"
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
                serviceTextView.id = R.id.service_textview
                serviceTextView.text = serviceEditText.text
                serviceTextView.setTextColor(resources.getColor(R.color.white))
                serviceTextView.textSize = 22f
                serviceTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                serviceTextView.gravity = Gravity.CENTER // set text alignment to center
                serviceTextView.isEnabled = false // disable editing initially

                val mileageTextView = TextView(this)
                mileageTextView.id = R.id.mileage_textview
                mileageTextView.text = mileageEditText.text
                mileageTextView.setTextColor(resources.getColor(R.color.white))
                mileageTextView.textSize = 22f
                mileageTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                mileageTextView.gravity = Gravity.CENTER // set text alignment to center
                mileageTextView.isEnabled = false // disable editing initially

                val dateTextView = TextView(this)
                dateTextView.id = R.id.date_textview
                dateTextView.text = dateEditText.text
                dateTextView.setTextColor(resources.getColor(R.color.white))
                dateTextView.textSize = 22f
                dateTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                dateTextView.gravity = Gravity.CENTER // set text alignment to center
                dateTextView.isEnabled = false // disable editing initially

                // Create the "x" button to delete the record
                val deleteButtonX = Button(this)
                deleteButtonX.text = "x"
                deleteButtonX.setTextColor(resources.getColor(R.color.white))
                deleteButtonX.textSize = 22f
                deleteButtonX.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                deleteButtonX.visibility = View.INVISIBLE // initially set as not visible
                deleteButtonX.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    deleteRecord(recordLayout, recordId)
                }
                deleteButtonX.gravity = Gravity.CENTER // set text alignment to center

                // Add the TextViews and the "x" button to the record LinearLayout
                recordLayout.addView(serviceTextView)
                recordLayout.addView(mileageTextView)
                recordLayout.addView(dateTextView)
                recordLayout.addView(deleteButtonX)

                // Add the record LinearLayout to the service_records_list LinearLayout
                serviceRecordsList.addView(recordLayout)

                // Add the record view to the recordViews list
                recordViews.add(recordLayout)

                // Add the delete button to the deleteButtons list
                deleteButtons.add(deleteButtonX)


                // Save the record to the database
                val service = serviceEditText.text.toString()
                val mileage = mileageEditText.text.toString()
                val date = dateEditText.text.toString()
                val db = databaseHelper.writableDatabase
                val insertQuery = "INSERT INTO service_records (service, mileage, date) VALUES ('$service', '$mileage', '$date')"
                db.execSQL(insertQuery)

                // Grab Id for new record
                val selectQuery = "SELECT id FROM service_records ORDER BY id DESC LIMIT 1"
                val cursor = db.rawQuery(selectQuery, null)
                var submmitedId: Long? = null

                if (cursor.moveToFirst()) {
                    submmitedId = cursor.getLong(0)
                    // add the id into our list
                    idList.add(submmitedId)
                    // Assign the id as a tag to the recordLayout
                    recordLayout.tag = submmitedId
                }

                cursor.close()
                db.close()

                recordLayout.tag = submmitedId

                // Clear the EditText fields
                serviceEditText.text.clear()
                mileageEditText.text.clear()
                dateEditText.text.clear()

                // Toggle the visibility of the new record layout
                newRecordLayout.visibility = View.GONE

                // Reset the button text
                addButton.text = "Add New"
                editButton.text = "Edit"
                deleteButton.visibility = View.VISIBLE

                Toast.makeText(this, "New Record Added", Toast.LENGTH_LONG).show()
            }
        }

        // set onClickListener on the delete button
        deleteButton.setOnClickListener {
            if (deleteButton.text == "Delete") {
                // Change the delete button text to "Done"
                deleteButton.text = "Done"

                // Show the "x" delete buttons
                for (button in deleteButtons) {
                    button.visibility = View.VISIBLE
                }
            } else {
                // Change the delete button text to "Delete"
                deleteButton.text = "Delete"

                // Hide the "x" delete buttons
                for (button in deleteButtons) {
                    button.visibility = View.INVISIBLE
                }
            }
        }

        // Load the saved records from the database
        loadSavedRecords()
    }

    private fun loadSavedRecords() {
        val db = databaseHelper.readableDatabase
        val selectQuery = "SELECT * FROM service_records"
        val cursor = db.rawQuery(selectQuery, null)

        val columnIndexId = cursor.getColumnIndex("id")
        val columnIndexService = cursor.getColumnIndex("service")
        val columnIndexMileage = cursor.getColumnIndex("mileage")
        val columnIndexDate = cursor.getColumnIndex("date")

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(columnIndexId)
                val service = cursor.getString(columnIndexService)
                val mileage = cursor.getString(columnIndexMileage)
                val date = cursor.getString(columnIndexDate)

                idList.add(id)

                // Create a new LinearLayout to represent the record
                val recordLayout = LinearLayout(this)
                recordLayout.orientation = LinearLayout.HORIZONTAL
                recordLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Assign the id as a tag to the recordLayout
                recordLayout.tag = id

                // Create three TextViews to display the service, mileage, and date
                val serviceTextView = TextView(this)
                serviceTextView.id = R.id.service_textview
                serviceTextView.text = service
                serviceTextView.setTextColor(resources.getColor(R.color.white))
                serviceTextView.textSize = 22f
                serviceTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                serviceTextView.gravity = Gravity.CENTER // set text alignment to center
                serviceTextView.isEnabled = false // disable editing initially

                val mileageTextView = TextView(this)
                mileageTextView.id = R.id.mileage_textview
                mileageTextView.text = mileage
                mileageTextView.setTextColor(resources.getColor(R.color.white))
                mileageTextView.textSize = 22f
                mileageTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                mileageTextView.gravity = Gravity.CENTER // set text alignment to center
                mileageTextView.isEnabled = false // disable editing initially

                val dateTextView = TextView(this)
                dateTextView.id = R.id.date_textview
                dateTextView.text = date
                dateTextView.setTextColor(resources.getColor(R.color.white))
                dateTextView.textSize = 22f
                dateTextView.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1F
                )
                dateTextView.gravity = Gravity.CENTER // set text alignment to center
                dateTextView.isEnabled = false // disable editing initially

                // Create the "x" button to delete the record
                val deleteButtonX = Button(this)
                deleteButtonX.text = "x"
                deleteButtonX.setTextColor(resources.getColor(R.color.white))
                deleteButtonX.textSize = 22f
                deleteButtonX.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                deleteButtonX.visibility = View.INVISIBLE // initially set as not visible
                deleteButtonX.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    deleteRecord(recordLayout, recordId)
                }
                deleteButtonX.gravity = Gravity.CENTER // set text alignment to center

                // Add the TextViews and the "x" button to the record LinearLayout
                recordLayout.addView(serviceTextView)
                recordLayout.addView(mileageTextView)
                recordLayout.addView(dateTextView)
                recordLayout.addView(deleteButtonX)

                // Add the record LinearLayout to the service_records_list LinearLayout
                val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)
                serviceRecordsList.addView(recordLayout)

                // Add the record view to the recordViews list
                recordViews.add(recordLayout)

                // Add the delete button to the deleteButtons list
                deleteButtons.add(deleteButtonX)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }


    private fun deleteRecord(recordLayout: LinearLayout, recordId: Long) {
        // Remove the record LinearLayout from the service_records_list LinearLayout
        val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)
        serviceRecordsList.removeView(recordLayout)

        // Remove the record view from the recordViews list
        recordViews.remove(recordLayout)

        // Remove the delete button from the deleteButtons list
        val deleteButton = deleteButtons.firstOrNull { it.tag == recordId }
        deleteButton?.let {
            deleteButtons.remove(it)
        }

        // Delete the record from the database
        val db = databaseHelper.writableDatabase
        val deleteQuery = "DELETE FROM service_records WHERE id = $recordId"
        db.execSQL(deleteQuery)
        db.close()

        Toast.makeText(this, "Record Deleted", Toast.LENGTH_LONG).show()
    }

}
