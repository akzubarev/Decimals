package com.education4all.mathCoachAlg

import android.content.*
import com.education4all.firebase.FireBaseUtils
import com.education4all.mathCoachAlg.tours.Tour

object StatisticMaker {
    const val STATISTICS = "Statistics"
    const val TOURS = "tours"
    fun saveTour(tour: Tour?, context: Context) {
        val editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val tourCountStr = prefs.getString(TOURS, "0")
        val tourCount = tourCountStr!!.toInt()
        saveTour_by_number(tour, context, tourCount)
        editor.putString(TOURS, Integer.toString(tourCount + 1))
        editor.apply()
    }

    fun saveTourOLD(tour: Tour?, context: Context) {
        val l_editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val tourCountStr = prefs.getString(TOURS, "0")
        val tourCount = tourCountStr!!.toInt()
        l_editor.putString(TOURS + "_" + tourCountStr, Integer.toString(tour.getTotalTasks()))
        val serializedTour = tour!!.serializeOLD()
        for (taskNumber in 0 until tour.totalTasks + 2) {
            l_editor.putString(
                TOURS + "_" + tourCountStr + "_" + taskNumber,
                serializedTour!![taskNumber]
            )
        }
        l_editor.putString(TOURS, Integer.toString(tourCount + 1))
        l_editor.apply()
    }

    fun loadToursOLD(context: Context): ArrayList<Tour?> {
        val resultTours = ArrayList<Tour?>()
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val toursCount = prefs.getString(TOURS, "0")!!.toInt()
        for (tourNumber in 0 until toursCount) resultTours.add(loadTourOld(context, tourNumber))
        return resultTours
    }

    fun loadTour(context: Context, tourNumber: Int): Tour? {
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val currentTour = prefs.getString(TOURS + "_" + tourNumber, "")
        return Tour.Companion.deSerialize(currentTour)
    }

    fun loadTourOld(context: Context, tourNumber: Int): Tour? {
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val taskCount = prefs.getString(TOURS + "_" + tourNumber, "0")!!.toInt()
        val currentTour = ArrayList<String?>()
        for (taskNumber in 0 until taskCount + 2) {
            currentTour.add(prefs.getString(TOURS + "_" + tourNumber + "_" + taskNumber, "0"))
        }
        return if (currentTour.size > 0) {
            Tour(currentTour)
        } else {
            null
        }
    }

    fun getTourCount(context: Context): Int {
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        return prefs.getString(TOURS, "0")!!.toInt()
    }

    fun getTourInfoOLD(context: Context, tourNumber: Int): String? {
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        return prefs.getString(TOURS + "_" + tourNumber + "_" + "0", "")
    }

    fun removeStatistics(context: Context) {
        val editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
        FireBaseUtils().deleteTours()
    }

    fun removeTour(context: Context, tourNumber: Int) {
        val editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        editor.remove(TOURS + "_" + tourNumber)
        val toursCount = prefs.getString(TOURS, "0")!!.toInt()
        editor.putString(TOURS, Integer.toString(toursCount - 1))
        editor.apply()
        for (i in tourNumber + 1 until toursCount) {
            val tour = loadTour(context, i)
            saveTour_by_number(tour, context, i - 1)
        }
    }

    fun removeTourOld(context: Context, tourNumber: Int) {
        val editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        val prefs = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE)
        val taskCount = prefs.getString(TOURS + "_" + tourNumber, "0")!!.toInt()
        for (taskNumber in 0 until taskCount + 2) {
            editor.remove(TOURS + "_" + tourNumber + "_" + taskNumber)
        }
        editor.remove(TOURS + "_" + tourNumber)
        val toursCount = prefs.getString(TOURS, "0")!!.toInt()
        editor.putString(TOURS, Integer.toString(toursCount - 1))
        editor.apply()
        for (i in tourNumber + 1 until toursCount) {
            val tour = loadTour(context, i)
            saveTour_by_number(tour, context, i - 1)
        }
    }

    fun saveTour_by_number(tour: Tour?, context: Context, tourNumber: Int) {
        val editor = context.getSharedPreferences(STATISTICS, Context.MODE_PRIVATE).edit()
        val tourNumberString = Integer.toString(tourNumber)
        val serializedTour = tour!!.serialize()
        editor.putString(TOURS + "_" + tourNumberString, serializedTour)
        editor.apply()
        FireBaseUtils().uploadTour(tour)
    }
}