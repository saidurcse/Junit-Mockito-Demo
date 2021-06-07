package demo.mockito.junit.mindoks

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var mSharedPreferencesHelper : SharedPreferencesHelper? = null
    private var mNameText : EditText? = null
    private var mDobPicker : DatePicker? = null
    private var mEmailText : EditText? = null
    private var mEmailValidator: EmailValidator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main) // Shortcuts to input fields.
            mNameText = findViewById<View>(R.id.userNameInput) as EditText
            mDobPicker = findViewById<View>(R.id.dateOfBirthInput) as DatePicker
            mEmailText = findViewById<View>(R.id.emailInput) as EditText // Setup field validators.
            mEmailValidator = EmailValidator()
            mEmailText!!.addTextChangedListener(mEmailValidator) // Instantiate a SharedPreferencesHelper.
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            mSharedPreferencesHelper = SharedPreferencesHelper(sharedPreferences) // Fill input fields from data retrieved from the SharedPreferences.
            populateUi()
        }

    /**
     * Initialize all fields from the personal info saved in the SharedPreferences.
     */
    private fun populateUi() {
        val sharedPreferenceEntry: SharedPreferenceEntry
        sharedPreferenceEntry = mSharedPreferencesHelper!!.getPersonalInfo()
        mNameText!!.setText(sharedPreferenceEntry.getName())
        val dateOfBirth: Calendar = sharedPreferenceEntry.getDateOfBirth()
        mDobPicker!!.init(
            dateOfBirth[Calendar.YEAR], dateOfBirth[Calendar.MONTH],
            dateOfBirth[Calendar.DAY_OF_MONTH], null
        )
        mEmailText!!.setText(sharedPreferenceEntry.getEmail())
    }

    /**
     * Called when the "Save" button is clicked.
     */
    fun onSaveClick(view: View?) {
        // Don't save if the fields do not validate.
        if (!mEmailValidator!!.isValid()) {
            mEmailText!!.error = "Invalid email"
            Log.w(TAG, "Not saving personal information: Invalid email")
            return
        } // Get the text from the input fields.
        val name = mNameText!!.text.toString()
        val dateOfBirth = Calendar.getInstance()
        dateOfBirth[mDobPicker!!.year, mDobPicker!!.month] = mDobPicker!!.dayOfMonth
        val email = mEmailText!!.text.toString() // Create a Setting model class to persist.
        val sharedPreferenceEntry =
            SharedPreferenceEntry(name, dateOfBirth, email) // Persist the personal information.
        val isSuccess: Boolean = mSharedPreferencesHelper!!.savePersonalInfo(sharedPreferenceEntry)
        if (isSuccess) {
            Toast.makeText(this, "Personal information saved", Toast.LENGTH_LONG).show()
            Log.i(TAG, "Personal information saved")
        } else {
            Log.e(TAG, "Failed to write personal information to SharedPreferences")
        }
    }

    /**
     * Called when the "Revert" button is clicked.
     */
    fun onRevertClick(view: View?) {
        populateUi()
        Toast.makeText(this, "Personal information reverted", Toast.LENGTH_LONG).show()
        Log.i(TAG, "Personal information reverted")
    }
}