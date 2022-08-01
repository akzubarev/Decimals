package com.education4all.utils

import android.content.*
import com.education4all.mathCoachAlg.DataReader
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tasks.*
import com.education4all.mathCoachAlg.tours.Tour
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object OldFormatHandler {
    var oldtours: ArrayList<Tour?>? = ArrayList()
    fun save(context: Context) {
        oldtours = StatisticMaker.loadToursOLD(context)
        var json = ""
        try {
            val objectMapper = ObjectMapper()
            json = objectMapper.writeValueAsString(oldtours)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        DataReader.SaveStat(json, context)
    }

    fun load(context: Context) {
        if (oldtours!!.size > 0) for (tour in oldtours!!) StatisticMaker.saveTourOLD(
            tour,
            context
        ) else {
            try {
                val json = DataReader.GetStat(context)
                val objectMapper = ObjectMapper()
                oldtours =
                    objectMapper.readValue(json, object : TypeReference<ArrayList<Tour?>?>() {})
                for (tour in oldtours) StatisticMaker.saveTourOLD(tour, context)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun requiresKostyl(date: String?): Boolean {
        val NEWVERSIONDATE = "05.04.2021"
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val newversiondate = Calendar.getInstance()
        val currentdate = Calendar.getInstance()
        try {
            newversiondate.time = sdf.parse(NEWVERSIONDATE)
            currentdate.time = sdf.parse(date)
            if (currentdate.before(newversiondate)) return true
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun updateStatistics(context: Context) {
        if (StatisticMaker.getTourCount(context) > 0) {
            val update = ArrayList<Tour?>()
            for (tourNumber in 0 until StatisticMaker.getTourCount(context)) {
                val tourInfoStr = StatisticMaker.getTourInfoOLD(context, tourNumber)
                if (!tourInfoStr!!.isEmpty()) {
                    val txt: String = Tour.Companion.DepictTour(tourInfoStr)
                    val divider = txt.indexOf("Решено")
                    val datetime = txt.substring(1, divider - 1)
                    val date = datetime.substring(0, 10)
                    val oldtour = StatisticMaker.loadTourOld(context, tourNumber)
                    val newTour = Tour()
                    newTour.copy(oldtour)
                    if (requiresKostyl(date)) {
                        newTour.tourTasks.clear()
                        val deTour = oldtour!!.serializeOLD()
                        var jump = 0
                        var i = 1
                        while (i < deTour!!.size - 1) {
                            val answers = ArrayList<String>()
                            val currentTask: Task = Task.Companion.makeTask(
                                deTour[i]
                            )
                            val TaskDepiction: ArrayList<String> =
                                Task.Companion.DepictTaskExtended(
                                    deTour[i], answers
                                )
                            val newTask: Task = Task.Companion.makeTask()
                            newTask.copy(currentTask)
                            newTask.update()
                            newTour.addTask(newTask)
                            if (requiresKostyl(date)) for (j in TaskDepiction.indices) {
                                val answerIsCorrect = answers[j] == currentTask.answer
                                val taskWasSkipped = answers[j] == "\u2026"
                                if (answerIsCorrect || taskWasSkipped) {
                                    i += jump
                                    jump = 0
                                } else {
                                    ++jump
                                }
                                if (i + jump == deTour.size - 2) {
                                    i += jump
                                }
                            }
                            ++i
                        }
                    } else for (task in newTour.tourTasks) task!!.update()
                    update.add(newTour)
                } else update.add(StatisticMaker.loadTour(context, tourNumber))
            }
            StatisticMaker.removeStatistics(context)
            for (tour in update) StatisticMaker.saveTour(tour, context)
        }
    }
}