package com.education4all.activities

import android.os.Bundle
import android.view.ViewManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.education4all.BuildConfig
import com.education4all.R
import com.education4all.mathCoachAlg.DataReader

class ComplexityActivity : AppCompatActivity() {
    private var actionType = 0
    val id = BuildConfig.APPLICATION_ID
    var isIntegers = BuildConfig.FLAVOR == "integers"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complexity)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val extras = intent.extras
        actionType = extras!!.getInt("Type")
        var checked = false
        for (i in 1..4) {
            val resID = resources.getIdentifier("checkBox$i", "id", id)
            val chk = findViewById<CheckBox>(resID)
            checked = DataReader.checkComplexity(actionType - 1, i - 1, this)
            if (chk.isChecked != checked) chk.isChecked = checked
        }
        if (isIntegers && actionType == 4) {
            val chk = findViewById<CheckBox>(R.id.checkBox5)
            checked = DataReader.checkComplexity(actionType - 1, 4, this)
            if (chk.isChecked != checked) chk.isChecked = checked
            val layout5 = findViewById<LinearLayout>(R.id.layout5)
            val biglayout = layout5.parent as LinearLayout
            biglayout.removeView(layout5)
            biglayout.addView(layout5, 3)
        } else {
            val layout = findViewById<LinearLayout>(R.id.layout5)
            (layout.parent as ViewManager).removeView(layout)
            //            ((ViewManager) chk.getParent()).removeView(chk);
//            TextView tw1 = findViewById(R.id.textView51);
//            ((ViewManager) tw1.getParent()).removeView(tw1);
//            TextView tw2 = findViewById(R.id.textView52);
//            ((ViewManager) tw2.getParent()).removeView(tw2);
        }

        // StupidTextFill(actionType);
        CleverTextFill()
    }

    private fun CleverTextFill() {
        var tvname: TextView
        var tvexample: TextView
        var tvnameID: Int
        var tvexampleID: Int
        var nameID: Int
        var exampleID: Int
        var limit = 5
        val operation: String
        val symbol: String
        var name: String
        var example: String
        val complType = findViewById<TextView>(R.id.complTextView)
        when (actionType) {
            1 -> {
                operation = "addition"
                symbol = ADD
                complType.text = "Сложение"
            }
            2 -> {
                operation = "substraction"
                symbol = SUB
                complType.text = "Вычитание"
            }
            3 -> {
                operation = "multiplication"
                symbol = MUL
                complType.text = "Умножение"
            }
            4 -> {
                operation = "division"
                symbol = DIV
                complType.text = "Деление"
                if (isIntegers) limit = 6
            }
            else -> {
                operation = "addition"
                symbol = ADD
                complType.text = "Сложение"
            }
        }
        for (i in 1 until limit) {
            tvnameID = resources.getIdentifier("textView" + i + 1, "id", id)
            tvexampleID = resources.getIdentifier("textView" + i + 2, "id", id)
            tvname = findViewById(tvnameID)
            tvexample = findViewById(tvexampleID)
            nameID = resources.getIdentifier(operation + "Name" + i, "string", id)
            exampleID = resources.getIdentifier(operation + "Example" + i, "string", id)
            name = getString(nameID)
            example = getString(exampleID)
            tvname.text = name
            tvexample.text = String.format(example, symbol, symbol, symbol)
        }
    }

    override fun onBackPressed() {
        var action = ""
        val currentComplexity = StringBuilder()
        val editor = getSharedPreferences(COMPLEXITY_SETTINGS, MODE_PRIVATE).edit()
        when (actionType) {
            1 -> action = "Add"
            2 -> action = "Sub"
            3 -> action = "Mul"
            4 -> action = "Div"
        }
        var chk = findViewById<CheckBox>(R.id.checkBox1)
        if (chk.isChecked) {
            currentComplexity.append("0").append(",")
        }
        chk = findViewById(R.id.checkBox2)
        if (chk.isChecked) {
            currentComplexity.append("1").append(",")
        }
        chk = findViewById(R.id.checkBox3)
        if (chk.isChecked) {
            currentComplexity.append("2").append(",")
        }
        chk = findViewById(R.id.checkBox4)
        if (chk.isChecked) {
            currentComplexity.append("3").append(",")
        }
        if (isIntegers && actionType == 4) {
            chk = findViewById(R.id.checkBox5)
            if (chk.isChecked) {
                currentComplexity.append("4").append(",")
            }
        }
        editor.putString(action, currentComplexity.toString())
        editor.apply()
        finish()
    }

    override fun onDestroy() {
        onBackPressed()
        super.onDestroy()
    }

    override fun onPause() {
        onBackPressed()
        super.onPause()
    }

    companion object {
        const val COMPLEXITY_SETTINGS = "ComplexitySettings"
        const val ADD = "\u2006+\u2006"
        const val SUB = "\u2006−\u2006"
        const val MUL = "\u2006\u22C5\u2006"
        const val DIV = "\u2006:\u2006"
    }
}