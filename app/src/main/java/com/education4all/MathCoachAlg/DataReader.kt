package com.education4all.mathCoachAlg

import android.content.*
import java.util.*

object DataReader {
    const val COMPLEXITY_SETTINGS = "ComplexitySettings"
    const val ROUND_TIME_SETTINGS = "RoundTimeSettings"
    const val ROUND_TIME = "RoundTime"
    const val DISAP_ROUND_TIME = "DisapRoundTime"
    const val TIMER_STATE = "TimerState"
    const val BUTTONS_PLACE = "ButtonsPlace"
    const val LAYOUT_STATE = "LayoutState"
    const val GOAL = "Goal"
    const val THEME = "Theme"
    const val REMINDER = "Reminder"
    const val REMINDER_TIME = "ReminderTime"
    const val QUEUE = "QueueEnabled"
    const val SIGNEDINTOFIREBASE = "SignedInToFirebase"
    private val defaultInts = HashMap<String?, Int>()
    private val defaultStrings = HashMap<String?, String>()
    private val defaultBooleans = HashMap<String?, Boolean>()
    fun convertIntegers(integers: ArrayList<Int>): IntArray {
        val ret = IntArray(integers.size)
        for (i in ret.indices) {
            ret[i] = integers[i]
        }
        return ret
    }

    private fun StrToIntArr(savedString: String?): IntArray {
        val st = StringTokenizer(savedString, ",")
        val savedList = ArrayList<Int>()
        val i = 0
        while (st.hasMoreTokens()) {
            savedList.add(st.nextToken().toInt())
        }
        return convertIntegers(savedList)
    }

    fun readAllowedTasks(p_context: Context?): Array<IntArray> {
        val prefs = p_context!!.getSharedPreferences(COMPLEXITY_SETTINGS, Context.MODE_PRIVATE)
        var savedString: String? = ""
        val Add: IntArray
        if (prefs.contains("Add")) {
            savedString = prefs.getString("Add", null)
            Add = StrToIntArr(savedString)
        } else {
            Add = IntArray(0)
        }
        val Sub: IntArray
        if (prefs.contains("Sub")) {
            savedString = prefs.getString("Sub", null)
            Sub = StrToIntArr(savedString)
        } else {
            Sub = IntArray(0)
        }
        val Mul: IntArray
        if (prefs.contains("Mul")) {
            savedString = prefs.getString("Mul", null)
            Mul = StrToIntArr(savedString)
        } else {
            Mul = IntArray(0)
        }
        val Div: IntArray
        if (prefs.contains("Div")) {
            savedString = prefs.getString("Div", null)
            Div = StrToIntArr(savedString)
        } else {
            Div = IntArray(0)
        }
        return arrayOf(Add, Sub, Mul, Div)
    }

    fun checkComplexity(p_action: Int, p_complexity: Int, p_context: Context?): Boolean {
        val l_allowedTasks = readAllowedTasks(p_context)
        for (i in 0 until l_allowedTasks[p_action].length) if (l_allowedTasks[p_action][i] == p_complexity) return true
        return false
    }

    fun SaveInt(value: Int, name: String?, p_context: Context?) {
        val editor =
            p_context!!.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putInt(name, value)
        editor.apply()
    }

    fun GetInt(name: String?, p_context: Context?): Int {
        return if (!defaultInts.containsKey(name)) throw IllegalArgumentException() else {
            val prefs = p_context!!.getSharedPreferences(
                ROUND_TIME_SETTINGS,
                Context.MODE_PRIVATE
            )
            prefs.getInt(name, defaultInts[name]!!)
        }
    }

    fun SaveString(value: String?, name: String?, p_context: Context?) {
        val editor =
            p_context!!.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putString(name, value)
        editor.apply()
    }

    fun GetString(name: String?, p_context: Context?): String? {
        return if (!defaultStrings.containsKey(name)) throw IllegalArgumentException() else {
            val prefs = p_context!!.getSharedPreferences(
                ROUND_TIME_SETTINGS,
                Context.MODE_PRIVATE
            )
            prefs.getString(name, defaultStrings[name])
        }
    }

    fun SaveBoolean(value: Boolean?, name: String?, p_context: Context?) {
        val editor =
            p_context!!.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putBoolean(name, value!!)
        editor.apply()
    }

    fun GetBoolean(name: String?, p_context: Context?): Boolean {
        return if (!defaultBooleans.containsKey(name)) throw IllegalArgumentException() else {
            val prefs = p_context!!.getSharedPreferences(
                ROUND_TIME_SETTINGS,
                Context.MODE_PRIVATE
            )
            prefs.getBoolean(name, defaultBooleans[name]!!)
        }
    }

    fun SaveQueue(json: String?, p_context: Context) {
        val editor =
            p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putString("Queue", json)
        editor.apply()
    }

    fun GetQueue(p_context: Context): String? {
        val prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE)
        return prefs.getString("Queue", "")
    }

    fun SaveStat(json: String?, p_context: Context) {
        val editor =
            p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putString("Stat", json)
        editor.apply()
    }

    fun GetStat(p_context: Context): String? {
        val prefs = p_context.getSharedPreferences(ROUND_TIME_SETTINGS, Context.MODE_PRIVATE)
        return prefs.getString("Stat", "")
    }

    init {
        //ints
        defaultInts[ROUND_TIME] = 1
        defaultInts[DISAP_ROUND_TIME] = -1
        defaultInts[GOAL] = 5

        //enums
        defaultInts[TIMER_STATE] = 1 // continuous
        defaultInts[BUTTONS_PLACE] = 0 // right
        defaultInts[LAYOUT_STATE] = 0 // 789
        defaultInts[THEME] = -1 // dark

        //booleans
        defaultBooleans[REMINDER] = false
        defaultBooleans[QUEUE] = false
        defaultBooleans[SIGNEDINTOFIREBASE] = false

        //strings
        defaultStrings[REMINDER_TIME] = "18:00"
    }
}