package com.education4all.utils

import android.content.Context
import android.util.Pair
import androidx.appcompat.app.AlertDialog
import com.education4all.BuildConfig
import com.education4all.mathCoachAlg.DataReader
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.utils.Enums.twodates
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun FixDialog(dialog: AlertDialog?, context: Context?) {
//        Resources res = context.getResources();
//        TextView textView = dialog.findViewById(android.R.id.message);
//        textView.setTextSize(res.getDimension(R.dimen.dimen4) / res.getDisplayMetrics().density);
//        textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
//
//        int colormain = res.getColor(R.color.main);
//        Button btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        if (btn != null)
//            btn.setTextColor(colormain);
//        btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//        if (btn != null)
//            btn.setTextColor(colormain);
    }

    fun randomly_select(list: List<Pair<Int, Int>>): Int {
        var sum = 0
        for (item in list) sum += item.second
        val index = Random().nextInt(sum) + 1
        sum = 0
        for (item2 in list) {
            sum += item2.second
            if (sum >= index) return item2.first
        }
        return -1
    }

    fun reachedGoal(context: Context): Boolean {
        var secondstoday = 0
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val c = Calendar.getInstance()
        c.time = Date()
        val today = sdf.format(c.time)
        return if (StatisticMaker.getTourCount(context) > 0) {
            for (tourNumber in 0 until StatisticMaker.getTourCount(context)) {
                val tour = StatisticMaker.loadTour(context, tourNumber)
                if (tour.totalTasks == 0) return false
                val seconds = tour.tourTime.toInt()
                val date = tour!!.date()
                if (Enums.isSubsequent(date, today) == twodates.equal) secondstoday += seconds
            }
            val goal = DataReader.GetInt(DataReader.GOAL, context) * 60
            secondstoday > goal
        } else false
    }

    var VERSIONING_REMOVEFIREBASE = "removeFirebaseDetails"
    fun versioningTool(): String {
        var result = "normalBuild"
        var version = "other"
        val flavor = BuildConfig.FLAVOR
        val versionCode = BuildConfig.VERSION_CODE
        if (flavor == "decimals" && versionCode <= 5) version = "decimalsBeta"
        if (flavor == "integers" && versionCode <= 1) version = "integersBeta"
        if (version == "decimalsBeta" || version == "integersBeta") result =
            VERSIONING_REMOVEFIREBASE
        return result
    } //        TextView statistics = findViewById(R.id.statistics);
    //        SpannableString ss = new SpannableString("Разные шрифты");
    //
    //        Typeface font = Typeface.DEFAULT_BOLD,
    //                font2 = Typeface.MONOSPACE;
    //
    //        ss.setSpan(new CustomTypefaceSpan("", font2), 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    //        ss.setSpan(new CustomTypefaceSpan("", font), 6, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    //        statistics.setText(ss);
}