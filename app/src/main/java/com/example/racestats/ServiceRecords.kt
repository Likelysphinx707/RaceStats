package com.example.racestats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class ServiceRecords : AppCompatActivity() {
    // Declare private properties for the buttons, layouts, and EditTexts
    private lateinit var titleTextView: TextView
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var cancelButton: Button
    private lateinit var newRecordLayout: LinearLayout
    private lateinit var serviceEditText: EditText
    private lateinit var mileageEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var recordViews: MutableList<View>
    private lateinit var deleteButtons: MutableList<Button>
    private lateinit var idList: MutableList<Long>
    private lateinit var editButtonIcons: MutableList<Button>
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_records)

        // Initialize the lists
        recordViews = mutableListOf()
        deleteButtons = mutableListOf()
        editButtonIcons = mutableListOf()
        idList = mutableListOf()

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

        // find the service_records_list LinearLayout
        val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)

        // find the UI buttons and assign them to their vars
        addButton = findViewById(R.id.add_new_button)
        editButton = findViewById(R.id.edit_button)
        deleteButton = findViewById(R.id.delete_button)
        cancelButton = findViewById(R.id.cancel_button)

        // find the EditTexts
        titleTextView = findViewById(R.id.title_textview)
        serviceEditText = findViewById(R.id.service_edittext)
        mileageEditText = findViewById(R.id.mileage_edittext)
        dateEditText = findViewById(R.id.date_edittext)

        // find the LinearLayout for new record
        newRecordLayout = findViewById(R.id.new_record_layout)

        // set onClickListener on the button
        addButton.setOnClickListener {

            // Toggle the visibility of the new record layout
            if (newRecordLayout.visibility == View.GONE) {
                toggleRecordLayoutVisibility(newRecordLayout, addButton, recordViews, titleTextView, editButton, deleteButton, cancelButton)
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
                deleteButtonX.setBackgroundColor(Color.BLACK)
                deleteButtonX.setTextColor(Color.RED)
                deleteButtonX.textSize = 30f
                deleteButtonX.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                deleteButtonX.visibility = View.GONE // initially set as not visible
                deleteButtonX.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    deleteRecord(recordLayout, recordId)
                }
                deleteButtonX.gravity = Gravity.CENTER // set text alignment to center

                val editButtonIcon = Button(this)
                editButtonIcon.id = R.id.delete_button
                editButtonIcon.setBackgroundResource(R.drawable.save) // Set the button background to the save.xml icon
                editButtonIcon.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                editButtonIcon.visibility = View.GONE // initially set as not visible
                editButtonIcon.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    val serviceText = serviceTextView.text.toString()
                    val mileageText = mileageTextView.text.toString()
                    val dateText = dateTextView.text.toString()

                    // Check if the variables are not null and if recordId is a Long
                    if (serviceText.isNotEmpty() && mileageText.isNotEmpty() && dateText.isNotEmpty()) {
                        // Create an Intent to start the EditRecordView activity
                        val intent = Intent(this, EditRecordView::class.java).apply {
                            putExtra("serviceText", serviceText)
                            putExtra("mileageText", mileageText)
                            putExtra("dateText", dateText)
                            putExtra("recordId", recordId)
                        }

                        // Start the new activity
                        startActivity(intent)
                    } else {
                        // Handle the case where data is missing or invalid
                        Log.e("View1", "Data is missing or invalid.")
                    }
                }
                editButtonIcon.gravity = Gravity.CENTER // set text alignment to center


                // Add the TextViews and the "x" button to the record LinearLayout
                recordLayout.addView(serviceTextView)
                recordLayout.addView(mileageTextView)
                recordLayout.addView(dateTextView)
                recordLayout.addView(deleteButtonX)
                recordLayout.addView(editButtonIcon)

                // Add the record LinearLayout to the service_records_list LinearLayout
                serviceRecordsList.addView(recordLayout)

                // Add the record view to the recordViews list
                recordViews.add(recordLayout)

                // Add the delete button to the deleteButtons list
                deleteButtons.add(deleteButtonX)

                // Add the edit button to the editButtonList
                editButtonIcons.add(editButtonIcon)


                // Check if any of the fields are null if they are new record will not be submitted and the user will be shown an error alert
                if (serviceEditText.text.isNullOrBlank() || mileageEditText.text.isNullOrBlank() || dateEditText.text.isNullOrBlank()) {
                    // One or more fields are null, handle the error or display a message
                    Toast.makeText(this, "You most fill in and least one field to add a new record", Toast.LENGTH_LONG).show()
                } else {
                    // Save the record to the database
                    val service = serviceEditText.text.toString()
                    val mileage = mileageEditText.text.toString()
                    val date = dateEditText.text.toString()
                    val db = databaseHelper.writableDatabase
                    val insertQuery =
                        "INSERT INTO service_records (service, mileage, date) VALUES ('$service', '$mileage', '$date')"
                    db.execSQL(insertQuery)

                    // Grab Id for new record
                    val selectQuery = "SELECT id FROM service_records ORDER BY id DESC LIMIT 1"
                    val cursor = db.rawQuery(selectQuery, null)
                    var submittedId: Long? = null

                    if (cursor.moveToFirst()) {
                        submittedId = cursor.getLong(0)
                        // Add the id into our list
                        idList.add(submittedId)
                        // Assign the id as a tag to the recordLayout
                        recordLayout.tag = submittedId
                    }

                    cursor.close()
                    db.close()

                    recordLayout.tag = submittedId

                    Toast.makeText(this, "New Record Added", Toast.LENGTH_LONG).show()
                }

                // Clear the EditText fields
                serviceEditText.text.clear()
                mileageEditText.text.clear()
                dateEditText.text.clear()

                // Toggle the visibility of the new record layout
                newRecordLayout.visibility = View.GONE

                // Reset the button text
                addButton.text = "Add New"
                editButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
                for (record in recordViews) {
                    record.visibility = View.VISIBLE
                }
                titleTextView.text = "Maintenance Records"
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
                    button.visibility = View.GONE
                }
            }
        }

        editButton.setOnClickListener {
            if(editButton.text == "Edit") {
                // Change the delete button text to "Save"
                editButton.text = "Save"


                // Show the edit Icons
                for (button in editButtonIcons) {
                    button.visibility = View.VISIBLE
                }
            } else {
                // Change the edit button text to "Edit"
                editButton.text = "Edit"

                // Hid the edit icons
                for (button in editButtonIcons) {
                    button.visibility = View.GONE
                }
            }
        }

        // Load the saved records from the database
        loadSavedRecords()
    }


    /**
     *  This function will populate the UI with all of the records that are currently in the DataBase
     */
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
                deleteButtonX.setBackgroundColor(Color.BLACK)
                deleteButtonX.setTextColor(Color.RED)
                deleteButtonX.textSize = 30f
                deleteButtonX.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                deleteButtonX.visibility = View.GONE // initially set as not visible
                deleteButtonX.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    deleteRecord(recordLayout, recordId)
                }
                deleteButtonX.gravity = Gravity.CENTER // set text alignment to center

                val editButtonIcon = Button(this)
                editButtonIcon.id = R.id.delete_button
                editButtonIcon.setBackgroundResource(R.drawable.save) // Set the button background to the save.xml icon
                editButtonIcon.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                editButtonIcon.visibility = View.GONE // initially set as not visible
                editButtonIcon.setOnClickListener {
                    val recordId = recordLayout.tag as Long
                    val serviceText = serviceTextView.text.toString()
                    val mileageText = mileageTextView.text.toString()
                    val dateText = dateTextView.text.toString()

                    // Check if the variables are not null and if recordId is a Long
                    if (serviceText.isNotEmpty() && mileageText.isNotEmpty() && dateText.isNotEmpty()) {
                        // Create an Intent to start the EditRecordView activity
                        val intent = Intent(this, EditRecordView::class.java).apply {
                            putExtra("serviceText", serviceText)
                            putExtra("mileageText", mileageText)
                            putExtra("dateText", dateText)
                            putExtra("recordId", recordId)
                        }

                        // Start the new activity
                        startActivity(intent)
                    } else {
                        // Handle the case where data is missing or invalid
                        Log.e("View1", "Data is missing or invalid.")
                    }
                }
                editButtonIcon.gravity = Gravity.CENTER // set text alignment to center

                // Add the TextViews and the "x" button to the record LinearLayout
                recordLayout.addView(serviceTextView)
                recordLayout.addView(mileageTextView)
                recordLayout.addView(dateTextView)
                recordLayout.addView(deleteButtonX)
                recordLayout.addView(editButtonIcon)

                // Add the record LinearLayout to the service_records_list LinearLayout
                val serviceRecordsList = findViewById<LinearLayout>(R.id.service_records_list)
                serviceRecordsList.addView(recordLayout)

                // Add the record view to the recordViews list
                recordViews.add(recordLayout)

                // Add the delete button to the deleteButtons list
                deleteButtons.add(deleteButtonX)

                // Add the edit button to the editButtonList
                editButtonIcons.add(editButtonIcon)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

    private fun toggleRecordLayoutVisibility(newRecordLayout: View, addButton: Button, recordViews: List<View>, titleTextView: TextView, editButton: Button, deleteButton: Button, cancelButton: Button) {
        newRecordLayout.visibility = View.VISIBLE
        addButton.text = "Confirm"
        for (record in recordViews) {
            record.visibility = View.GONE
        }
        titleTextView.text = "Add New Record"
        editButton.visibility = View.GONE
        deleteButton.visibility = View.GONE
        cancelButton.visibility = View.VISIBLE

        cancelButton.setOnClickListener {
            if (newRecordLayout.visibility == View.VISIBLE) {
                newRecordLayout.visibility = View.GONE
                addButton.text = "Add New"
                for (record in recordViews) {
                    record.visibility = View.VISIBLE
                }
                titleTextView.text = "Maintenance Records"
                editButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
                cancelButton.visibility = View.GONE
            }
        }
    }


    /**
     * This function can be called to delete the records for both the UI and the DataBase
     */
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
