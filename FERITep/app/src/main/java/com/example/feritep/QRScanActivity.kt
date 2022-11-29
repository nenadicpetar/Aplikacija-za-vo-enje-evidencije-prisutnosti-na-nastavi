package com.example.feritep

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.feritep.models.Attendance
import com.example.feritep.models.Student
import com.google.firebase.database.*
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class QRScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null

    lateinit var tvName: TextView
    lateinit var tvMkpt: TextView
    lateinit var tvMajor: TextView

    lateinit var imgProfile: ImageView

    lateinit var studentTable: DatabaseReference
    lateinit var attendanceTable: DatabaseReference

    lateinit var sharedPreferences: SharedPreferences
    var hour = ""
    var monthForAttendanceKey = ""

    var todayForAttendanceKey: String = ""

    public override fun onCreate(state: Bundle?) {
        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Molimo, pričekajte...")
        progressDialog.show()
        super.onCreate(state)
        // mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(R.layout.activity_qrscan)
        //setContentView(mScannerView)                // Set the scanner view as the content view

        mScannerView = findViewById<ZXingScannerView>(R.id.camera_scanner_view)
        //mScannerView = ZXingScannerView(this)

        tvName = findViewById(R.id.tv_stud_name_scan)
        tvMkpt = findViewById(R.id.tv_mkpt_scan)
        tvMajor = findViewById(R.id.tv_major_scan)

        imgProfile = findViewById(R.id.profile_pic_img_scan)

        studentTable = FirebaseDatabase.getInstance().reference.child("ams").child("student")

        attendanceTable = FirebaseDatabase.getInstance().reference.child("ams").child("pendingattendance")


        sharedPreferences = getSharedPreferences(getString(R.string.myPref), Context.MODE_PRIVATE)
        studentTable.child(sharedPreferences.getString("mkpt", null)!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    var student = datasnapshot.getValue(Student::class.java)
                    tvName.text = student?.name
                    tvMkpt.text = "mkpt-${student?.mkpt}"
                    tvMajor.text = student?.major

                    Glide.with(this@QRScanActivity).load(student?.profileurl).into(imgProfile)
                    progressDialog.dismiss()
                }

            })


        supportActionBar?.title = "Skeniranje"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH) + 1
        var day = c.get(Calendar.DAY_OF_MONTH)

        var realDay: String? = ""
        var realMonth: String? = ""

        //get current time
        hour = c.get(Calendar.HOUR_OF_DAY).toString()
        hour = hour + ":" + c.get(Calendar.MINUTE).toString()

        if (day < 10) {
            realDay = "0$day"
        } else {

            realDay = day.toString()
        }
        if (month < 10) {
            realMonth = "0$month"
        } else {
            realMonth = month.toString()
        }


        monthForAttendanceKey = realMonth
        todayForAttendanceKey = "$realDay$realMonth$year"

    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun handleResult(rawResult: Result?) {

        mScannerView?.stopCamera()


        var decryptedData = ""
        try {
            decryptedData = rawResult!!.text.decrypt("amsucsmapp123456")
            //Log.d("Petar","današnji enkriptirani tekst je: $encrypted")
            Log.d("Petar", "današnji enkriptirani tekst je: ${decryptedData}")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        //get my current location
        var myLat = sharedPreferences.getString(getString(R.string.prefLat), null)
        var myLon = sharedPreferences.getString(getString(R.string.prefLon), null)

        //get teacher lat and lon and everythinng from qr
        var dataFromQR = decryptedData.split(",")
        Log.d("Petar", "veličina podataka za QR za danas je: ${dataFromQR.size}")
        var subjectCode = dataFromQR[0]
        var subjectName = dataFromQR[1]
        var day = dataFromQR[2]
        var time = dataFromQR[3]
        var tddate = dataFromQR[4]
        var trLat = dataFromQR[5]
        var trLon = dataFromQR[6]


        var myLocation = Location("me")
        try {
            myLocation.latitude = myLat!!.toDouble()
            myLocation.longitude = myLon!!.toDouble()
        } catch (e: java.lang.Exception) {

        }

        var teacherLocation = Location("teacher")
        try {
            teacherLocation.latitude = trLat.toDouble()
            teacherLocation.longitude = trLon.toDouble()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(this@QRScanActivity, "Dogodila se greška" + ex.message, Toast.LENGTH_LONG).show()
        }

        var distanceBetween = myLocation.distanceTo(teacherLocation).toDouble() / 1000 //in meter

        Log.d("Petar", "današnja udaljenost od moje lokacije do profesorove je $distanceBetween")

        if (distanceBetween > 10) {

            AlertDialog.Builder(this)
                .setTitle("Greška")
                .setMessage("Žao mi je! Niste u dosegu nastavnikova uređaja. Osvježite svoju lokaciju i pokušajte ponovno.")
                .setPositiveButton("U redu") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else if (tddate != todayForAttendanceKey) {
            AlertDialog.Builder(this)
                .setTitle("Greška")
                .setMessage("Žao mi je! Vaš datum i datum nastavnika se ne podudaraju. Provjerite datum i pokušajte ponovno.")
                .setPositiveButton("U redu") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else if (!sharedPreferences.getString(getString(R.string.prefSubject), null)!!.contains(subjectCode)) {

            // Log.d("Petar",": subjekt ${sharedPreferences.getString(getString(R.string.prefSubject), null)} i trenutni QR sub je: $subjectCode")
            //check whether this student is enrolling for this subject or not
            AlertDialog.Builder(this)
                .setTitle("Greška")
                .setMessage("Žao mi je! Niste upisani na ovaj kolegij $subjectCode \n$subjectName.")
                .setPositiveButton("U redu") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {

            var mkpt = sharedPreferences.getString(getString(R.string.prefMkpt), null)

            //var attendanceKey = "subcode$subjectCode"
            var attendance = Attendance(todayForAttendanceKey, hour, mkpt!!, subjectCode)
            attendanceTable.child(monthForAttendanceKey).child(todayForAttendanceKey).child(subjectCode).child(mkpt)
                .setValue(attendance)

            AlertDialog.Builder(this)
                .setTitle("Uspješno")
                .setMessage("Vaša prisutnost je zabilježena!")
                .setPositiveButton("U redu") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    fun String.encrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size) {
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

        val encryptedValue = cipher.doFinal(this.toByteArray())
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    }

    fun String.decrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size) {
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val decryptedByteValue = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
        return String(decryptedByteValue)
    }
}