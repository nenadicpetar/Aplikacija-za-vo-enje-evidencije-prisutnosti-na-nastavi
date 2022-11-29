package com.example.feritep.adapters

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import com.example.feritep.R
import com.example.feritep.models.Attendance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SubjectDetailListAdapter(context: Context, arrayList: ArrayList<Attendance>) : ListAdapter {

    var context: Context? = context
    var arrayList: ArrayList<Attendance>? = arrayList

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return arrayList!!.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var attendance: Attendance = arrayList!!.get(position)

        var inflater = LayoutInflater.from(context)
        var rowView = inflater.inflate(R.layout.subject_detail_listview_row, null, true)


        var date = rowView.findViewById<TextView>(R.id.attendance_date)
        var day = rowView.findViewById<TextView>(R.id.attendance_day)
        var time = rowView.findViewById<TextView>(R.id.attendance_time)

        //getthe current date of day of week
        val DATEFORMATT = "dd-MM-yyyy"
        var formatofDate = SimpleDateFormat(DATEFORMATT)
        var dateForWeekDay = formatofDate.parse(attendance.date)
        var calendarObj = Calendar.getInstance()
        calendarObj.time = dateForWeekDay

        var weekofDay = ""
        when (calendarObj.get(Calendar.DAY_OF_WEEK)) {

            Calendar.MONDAY -> {
                weekofDay = "Ponedjeljak"
            }
            Calendar.TUESDAY -> {
                weekofDay = "Utorak"
            }
            Calendar.WEDNESDAY -> {
                weekofDay = "Srijeda"
            }
            Calendar.THURSDAY -> {
                weekofDay = "Četvrtak"
            }
            Calendar.FRIDAY -> {
                weekofDay = "Petak"
            }
            Calendar.SATURDAY -> {
                weekofDay = "Subota"
            }
            Calendar.SUNDAY -> {
                weekofDay = "Nedjelja"
            }
        }

        date.text = attendance.date
        day.text = weekofDay
        time.text = attendance.time

        rowView.setOnClickListener {

            // Toast.makeText(rowView.context, "Pozdrav, $text ", Toast.LENGTH_LONG).show()

            /*  val intent = Intent(rowView.context, NotiDetailActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            intent.putExtra("date", date)*/

            //ContextCompat.startActivity(rowView.context, intent, null)
        }

        return rowView
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return arrayList!!.size
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(p0: Int): Boolean {
        return true
    }

}