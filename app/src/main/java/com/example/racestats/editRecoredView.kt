package com.example.racestats

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class editRecoredView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_recored_view_ui)

        // Get the data passed from the previous activity
        val serviceText = intent.getStringExtra("serviceText")
        val mileageText = intent.getStringExtra("mileageText")
        val dateText = intent.getStringExtra("dateText")
        val recordId = intent.getLongExtra("recordId", -1)

        // Find the UI values
        val serviceEditText = findViewById<EditText>(R.id.service_edittext)
        val mileageEditText = findViewById<EditText>(R.id.mileage_edittext)
        val dateEditText = findViewById<EditText>(R.id.date_edittext)

        // Use these values to display the details in the UI
        serviceEditText.text = Editable.Factory.getInstance().newEditable(serviceText ?: "")
        mileageEditText.text = Editable.Factory.getInstance().newEditable(mileageText ?: "")
        dateEditText.text = Editable.Factory.getInstance().newEditable(dateText ?: "")

        // Perform any other actions with the data as needed
    }

}