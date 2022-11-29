package com.ucsm.tylersai.amsteacher.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ucsm.tylersai.amsteacher.model.Subject
import com.ucsm.tylersai.amsteacher.ui.activity.QRGenerateActivity
import kotlinx.android.synthetic.main.teaching_subjects_recycler_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class TeachingSubjectAdapter(val items: ArrayList<Subject>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val DATE_FORMAT = "dd-MM-yyyy"
    var todaysDay: String = ""
    var hour: Int? = null

    private var todayForAttendanceKey = ""

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = ViewHolder(
            LayoutInflater.from(context).inflate(
                com.ucsm.tylersai.amsteacher.R.layout.teaching_subjects_recycler_row,
                parent,
                false
            )
        )

        val dateFormat = SimpleDateFormat(DATE_FORMAT)
        val c = Calendar.getInstance().time
        val today = dateFormat.format(c)

        val formatofDate = SimpleDateFormat(DATE_FORMAT)
        val dateForWeekDay = formatofDate.parse(today)
        val calendarObj = Calendar.getInstance()

        //get current time
        hour = calendarObj.get(Calendar.HOUR_OF_DAY)
        Log.d("Petar", "today current hour is $hour and current date is: $today")
        calendarObj.time = dateForWeekDay

        when (calendarObj.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> {
                todaysDay = "Nedjelja"
            }
            Calendar.MONDAY -> {
                todaysDay = "Ponedjeljak"
            }
            Calendar.TUESDAY -> {
                todaysDay = "Utorak"
            }
            Calendar.WEDNESDAY -> {
                todaysDay = "Wednesday"
            }
            Calendar.THURSDAY -> {
                todaysDay = "Četvrtak"
            }
            Calendar.FRIDAY -> {
                todaysDay = "Petak"
            }
            Calendar.SATURDAY -> {
                todaysDay = "Subota"
            }
        }

        val cc = Calendar.getInstance()
        val year = cc.get(Calendar.YEAR)
        val month = cc.get(Calendar.MONTH) + 1
        val day = cc.get(Calendar.DAY_OF_MONTH)

        var realDay: String? = ""
        var realMonth: String? = ""

        realDay = if (day < 10) {
            "0${day}"
        } else {
            day.toString()
        }
        realMonth = if (month < 10) {
            "0${month}"
        } else {
            month.toString()
        }

        todayForAttendanceKey = "$realDay$realMonth$year"

        view.itemView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Potvrditi")
                .setMessage("Jeste li sigurni da želite pokupiti prisutnost za kolegij: ${view.tvSubjectCode.text}")
                .setPositiveButton("Pokupite", DialogInterface.OnClickListener { dialogInterface, i ->

                    val correspondingDate = view.tvD
                    val correspondingTime = view.tvT
                    Log.d("Petar", "today, tvd up ${view.tvD}")

                    if (dateAndTimeCorrect(correspondingDate, correspondingTime)) {

                        dialogInterface.dismiss()
                        val intent = Intent(context, QRGenerateActivity::class.java)
                        intent.putExtra(
                            "textToEncrypt",
                            "${view.tvSubjectCode.text},${view.tvSubjectName.text},$todaysDay,$hour,$todayForAttendanceKey"
                        )
                        intent.putExtra("subjectName", view.tvSubjectName.text)
                        startActivity(context, intent, null)

                    } else {

                        dialogInterface.dismiss()
                        AlertDialog.Builder(context)
                            .setTitle("Greška")
                            .setMessage("Trenutni datum i vrijeme se ne poklapaju s rasporedom.")
                            .setPositiveButton(
                                "U redu",
                                DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                            .show()
                    }//end of else

                })
                .setNegativeButton("Prekini", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .show()
        }

        return view
    }

    private fun dateAndTimeCorrect(corDate: String, corTime: String): Boolean {
        var eligible = true

        val dayAry = corDate.split(",")
        val timeAry = corTime.split(",")

        for (i in 0 until dayAry.size) {
            val d = dayAry[i].trim()
            //Log.d("Petar", "today is: $todaysDay and schedule is ${d}")
            if (d.equals(todaysDay)) {
                //Log.d("Petar", "today entering checking time")
                val timeInterval = timeAry[i].split("-")
                val timeStart = timeInterval[0].trim().toInt()
                val timeEnd = timeInterval[1].trim().toInt()

                //Log.d("Petar", "today date n time is: ${dayAry[i]}, ${timeAry[i]} current hour is : $hour")
                //Log.d("Petar", "today start and end time is: $timeStart, $timeEnd")
                //check if time is correct
                eligible = compareValues(hour, timeStart) >= 0 && compareValues(
                    hour,
                    timeEnd
                ) < 0//end of else time check

                break

            } else {
                eligible = false
            }
        }
        return eligible
    }

    // Binds each animal in the ArrayList to a view,
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvSubjectCode?.text = items[position].subjectCode
        holder.tvSubjectName?.text = items[position].name

        //day and time
        val day = items[position].day
        val time = items[position].time

        val dayAry = day.split(",")
        val timeAry = time.split(",")
        holder.tvsubjectDay?.text = ""
        for (i in dayAry.indices) {
            holder.tvsubjectDay?.text =
                "${holder.tvsubjectDay?.text}${dayAry[i]} : ${timeAry[i]}(hr)\n"
        }

        holder.tvD = items[position].day
        holder.tvT = items[position].time

        Log.d("Petar", "today, tvd ${holder.tvD}")

        holder.tvsubjectRoom?.text = items[position].room
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvSubjectCode = view.subject_code_tv
    val tvSubjectName = view.subject_title_detail_tv
    val tvsubjectDay = view.subject_day_tv
    val tvsubjectRoom = view.subject_room_tv
    var tvD: String = ""
    var tvT: String = ""
}