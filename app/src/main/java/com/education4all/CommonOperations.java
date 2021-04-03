package com.education4all;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.education4all.decimals.R;

public class CommonOperations {

    public static void FixDialog(AlertDialog dialog, Context context) {
        Resources res = context.getResources();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTextSize(res.getDimension(R.dimen.dimen4) / res.getDisplayMetrics().density);
        textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        int colormain = res.getColor(R.color.main);
        Button btn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btn != null)
            btn.setTextColor(colormain);
        btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btn != null)
            btn.setTextColor(colormain);
        btn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (btn != null)
            btn.setTextColor(colormain);

    }

}
