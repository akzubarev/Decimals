package com.education4all.utils

import android.content.Context
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tasks.Task
import com.education4all.mathCoachAlg.tours.Tour
import java.util.*

object Mocker {
    fun genereteFakeStatistics(context: Context, old: Boolean) {
        val stats = arrayOf(
            "1 0 0 1 1 1 1 1 1 1 1 0",
            "2 1 1 1 1 1 1 1 1 1 0 1",
            "3 1 1 1 1 1 1 1 0 1 1 1",
            "4 1 0 1 1 1 1 1 1 0 1 1",
            "5 1 0 1 1 1 1 0 1 1 1 1",
            "6 1 1 1 1 1 1 1 0 1 1 1",
            "7 0 1 1 1 1 1 1 1 1 0 1",
            "8 1 0 1 1 0 1 1 1 1 1 0",
            "9 1 1 1 1 1 1 1 1 1 1 0",
            "10 1 1 1 1 1 0 1 1 1 0 0"
        )
        for (stat in stats) generateFakeTour(stat, context, old)
    }

    fun generateFakeTour(stat: String, context: Context, old: Boolean) {
        if (old) StatisticMaker.saveTourOLD(
            genTour(stat, true),
            context
        ) else StatisticMaker.saveTour(
            genTour(stat, false), context
        )
    }

    private fun genTour(stat: String, old: Boolean): Tour {
        val rnd = Random()
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DATE, -31)
        val lines = stat.split(" ").toTypedArray()
        c.add(Calendar.DATE, lines[0].toInt())
        val tour = Tour()
        tour.tourDateTime = c.timeInMillis
        tour.tourTime = 0
        for (j in 1 until lines.size) {
            val task: Task = Task.Companion.makeTask()
            task.timeTaken = rnd.nextInt(100).toLong()
            tour.tourTime = tour.tourTime + task.timeTaken
            val correct = lines[j].toInt() == 1
            if (old) task.makeUserAnswer(
                if (correct) task.answer else "…" + ":" + rnd.nextInt(20) + '|',
                ""
            ) else task.makeUserAnswer(
                if (correct) task.answer else "…",
                rnd.nextInt(20).toString()
            )
            tour.addTaskOld(task, correct)
        }
        return tour
    }
}