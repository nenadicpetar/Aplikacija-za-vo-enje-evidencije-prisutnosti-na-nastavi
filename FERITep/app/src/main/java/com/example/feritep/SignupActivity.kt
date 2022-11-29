package com.example.feritep

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.feritep.models.PreStudentList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {

    private var sharedPreferences: SharedPreferences? = null
    private var preferenceEditor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setSupportActionBar(findViewById(R.id.signup_customized_toolbar))

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        sharedPreferences = getSharedPreferences(getString(R.string.mySignUpPref), Context.MODE_PRIVATE)
        preferenceEditor = sharedPreferences!!.edit()
        preferenceEditor?.clear()

        val edtmkpt = findViewById<EditText>(R.id.signup_edt_mkpt)
        val edtname = findViewById<EditText>(R.id.signup_edt_name)
        val edtphone = findViewById<EditText>(R.id.signup_edt_phone)
        val edtemail = findViewById<EditText>(R.id.signup_edt_email)
        val edtaddr = findViewById<EditText>(R.id.signup_edt_addr)

        val btCreate = findViewById<Button>(R.id.signup_bt_signup)

        btCreate.setOnClickListener {

            var mkpt = edtmkpt.text.toString()
            var name = edtname.text.toString()
            var phone = edtphone.text.toString()
            var email = edtemail.text.toString()
            var address = edtaddr.text.toString()

            if (mkpt.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Greška")
                    .setMessage("Ispunite sva polja")
                    .setPositiveButton(
                        "U redu",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }
                    )
                    .show()
            } else if (!mkpt.contains("mkpt-") || mkpt.length < 5) {
                AlertDialog.Builder(this)
                    .setTitle("Greška")
                    .setMessage("Krivi format!\nMora biti u mkpt-0000 formatu.")
                    .setPositiveButton(
                        "U redu",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }
                    )
                    .show()
            } else {
                mkpt = mkpt.substring(5)

                //get mkpt and name check for
                var prestudentlistTable = FirebaseDatabase.getInstance().reference.child("ams").child("prestudentlist")
                prestudentlistTable.child(mkpt).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@SignupActivity, "Dogodila se greška" + p0.toException().message, Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //data exists
                            //check name is the same or not
                            var preStudentList = dataSnapshot.getValue(PreStudentList::class.java)
                            Log.d("Petar", "unešeni mkpt je ")
                            if (preStudentList!!.name == name) {

                                //do the job

                                preferenceEditor?.putString(getString(R.string.prefName), name)
                                preferenceEditor?.putString(getString(R.string.prefMkpt), mkpt)
                                preferenceEditor?.putString(getString(R.string.prefPhone), phone)
                                preferenceEditor?.putString(getString(R.string.prefEmail), email)
                                preferenceEditor?.putString(getString(R.string.prefAddress), address)
                                preferenceEditor?.putString(getString(R.string.prefMajor), preStudentList.major)
                                preferenceEditor?.commit()

                                val intent = Intent(this@SignupActivity, ChooseSubjectActivity::class.java)
                                startActivity(intent)
                            } else {
                                //name does not match
                                AlertDialog.Builder(this@SignupActivity)
                                    .setTitle("Greška")
                                    .setMessage("Ime nije ispravno. Mora biti ${preStudentList.name}. Pokušajte ponovno")
                                    .setPositiveButton(
                                        "U redu",
                                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }
                                    )
                                    .show()
                            }
                        } else {
                            //data not exists mkpt
                            AlertDialog.Builder(this@SignupActivity)
                                .setTitle("Greška")
                                .setMessage("Niste na popisu za registriranje. Javite se administratoru.")
                                .setPositiveButton(
                                    "U redu",
                                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }
                                )
                                .show()
                        }
                    }
                })
            }
        }//end of button action
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}// end of class SignupActivity
