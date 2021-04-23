package com.education4all;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonOperations {

    public static void FixDialog(AlertDialog dialog, Context context) {
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



    public static boolean requiresKostyl(String date) {
        String NEWVERSIONDATE = "05.04.2021";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(NEWVERSIONDATE));
            c2.setTime(sdf.parse(date));
            if (c2.before(c1))
                return true;

        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
