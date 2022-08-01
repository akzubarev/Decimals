package com.education4all.activities.tabs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.education4all.BuildConfig
import com.education4all.R
import com.education4all.mathCoachAlg.DataReader

class SettingsTaskTab : Fragment() {
    private var view: View? = null
    private val id = BuildConfig.APPLICATION_ID
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_tasks, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        view = v
        if (BuildConfig.FLAVOR == "integers") {
            val newDivisionCHeckbox = v.findViewById<CheckBox>(R.id.checkBoxDiv2)
            newDivisionCHeckbox.setOnClickListener { v1: View? ->
                newDivisionCHeckbox.isChecked = !newDivisionCHeckbox.isChecked
                val wrapper: Context = ContextThemeWrapper(
                    context, R.style.CustomPopup
                )
                val popup = PopupMenu(wrapper, newDivisionCHeckbox)
                popup.menuInflater
                    .inflate(R.menu.dropdown_div, popup.menu)
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    item.isChecked = !item.isChecked
                    var one = false
                    var two = false
                    if (popup.menu.getItem(0).isChecked) {
                        one = true
                    }
                    if (popup.menu.getItem(1).isChecked) {
                        two = true
                    }
                    addComplexityFromPopup(one, two)
                    newDivisionCHeckbox.isChecked = one || two
                    true
                }
                popup.show()
                if (DataReader.checkComplexity(3, 1, context)) {
                    popup.menu.getItem(0).isChecked = true
                }
                if (DataReader.checkComplexity(3, 4, context)) {
                    popup.menu.getItem(1).isChecked = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val names = arrayOf("checkBoxAdd", "checkBoxSub", "checkBoxMul", "checkBoxDiv")
        for (operation in 0..3) for (i in 1..4) {
            val name = names[operation] + i
            var checked = false
            val resID = resources.getIdentifier(name, "id", id)
            val cb = view!!.findViewById<CheckBox>(resID)
            if (BuildConfig.FLAVOR == "integers" && operation == 3 && i == 2) {
                checked = (DataReader.checkComplexity(3, 1, activity!!.applicationContext)
                        || DataReader.checkComplexity(3, 4, activity!!.applicationContext))
            } else {
                checked =
                    DataReader.checkComplexity(operation, i - 1, activity!!.applicationContext)
                cb.setOnClickListener { v: View -> onCheckBoxClick(v) }
            }
            if (cb.isChecked != checked) cb.isChecked = checked
        }
    }

    fun onCheckBoxClick(v: View) {
        val currentComplexity = StringBuilder()
        val editor = activity!!.application
            .getSharedPreferences(COMPLEXITY_SETTINGS, AppCompatActivity.MODE_PRIVATE).edit()
        var action = ""
        val tag = v.tag.toString().toInt()
        when (tag) {
            1 -> action = "Add"
            2 -> action = "Sub"
            3 -> action = "Mul"
            4 -> action = "Div"
        }
        for (i in 1..4) {
            val name = "checkBox$action$i"
            val resID = resources.getIdentifier(name, "id", id)
            val chk = view!!.findViewById<CheckBox>(resID)
            if (chk.isChecked) {
                currentComplexity.append(i - 1).append(",")
            }
        }
        editor.putString(action, currentComplexity.toString())
        editor.apply()
    }

    fun addComplexityFromPopup(one: Boolean, two: Boolean) {
        val currentComplexity = StringBuilder()
        val editor =
            context!!.getSharedPreferences(COMPLEXITY_SETTINGS, Context.MODE_PRIVATE).edit()
        var chk = view!!.findViewById<CheckBox>(R.id.checkBoxDiv1)
        if (chk.isChecked) {
            currentComplexity.append("0").append(",")
        }
        if (one) {
            currentComplexity.append("1").append(",")
        }
        chk = view!!.findViewById(R.id.checkBoxDiv3)
        if (chk.isChecked) {
            currentComplexity.append("2").append(",")
        }
        chk = view!!.findViewById(R.id.checkBoxDiv4)
        if (chk.isChecked) {
            currentComplexity.append("3").append(",")
        }
        if (two) {
            currentComplexity.append("4").append(",")
        }
        editor.putString("Div", currentComplexity.toString())
        editor.apply()
    }

    companion object {
        const val COMPLEXITY_SETTINGS = "ComplexitySettings"
    }
}