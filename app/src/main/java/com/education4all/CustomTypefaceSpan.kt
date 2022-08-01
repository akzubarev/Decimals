package com.education4all

import com.education4all.utils.Enums.twodates
import com.education4all.utils.Enums.TimerState
import com.education4all.utils.Enums.ButtonsPlace
import com.education4all.utils.Enums
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tours.Tour
import com.education4all.mathCoachAlg.DataReader
import com.education4all.utils.Mocker
import com.education4all.utils.OldFormatHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth
import com.education4all.firebase.FireBaseUtils.LoginCallback
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnFailureListener
import com.education4all.firebase.FireBaseUtils
import com.education4all.firebase.FireBaseUtils.StatisticsCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.education4all.R
import com.education4all.activities.tabs.SettingsAppTab.SpinnerCallback
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.view.Gravity
import android.widget.AdapterView
import android.util.TypedValue
import com.education4all.NotificationHelper
import android.widget.LinearLayout
import android.widget.TimePicker
import android.content.DialogInterface
import android.widget.CheckBox
import android.content.SharedPreferences
import com.education4all.activities.tabs.SettingsTaskTab
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.education4all.activities.tabs.SectionsPagerAdapter
import com.education4all.activities.tabs.SettingsAppTab
import android.webkit.WebView
import com.education4all.activities.WebActivity.MyWebViewClient
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.webkit.WebViewClient
import android.annotation.TargetApi
import android.os.Build
import android.webkit.WebResourceRequest
import android.view.MenuInflater
import android.content.Intent
import com.education4all.activities.AuthorsActivity
import com.education4all.activities.TaskActivity
import com.education4all.activities.SettingsMainActivity
import com.education4all.activities.StatiscticsActivity
import kotlin.Throws
import android.text.format.DateUtils
import android.widget.ProgressBar
import android.text.SpannableString
import android.text.style.SuperscriptSpan
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.annotation.SuppressLint
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.text.Html
import android.text.style.URLSpan
import com.education4all.activities.StatTourActivity
import com.education4all.activities.WebActivity
import com.education4all.mathCoachAlg.tasks.Fraction
import android.widget.EditText
import com.education4all.activities.MainActivity
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.view.ViewManager
import com.education4all.activities.ComplexityActivity
import android.widget.ToggleButton
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlin.jvm.JvmOverloads
import com.education4all.mathCoachAlg.tasks.IntegerTask
import com.education4all.mathCoachAlg.tasks.DecimalTask
import com.education4all.mathCoachAlg.tasks.FractionTask
import com.education4all.mathCoachAlg.tasks.SerializableTask
import androidx.recyclerview.widget.RecyclerView
import com.education4all.TourAdapter.TourViewHolder
import com.education4all.TourAdapter.OnUserClickListener
import androidx.appcompat.app.AppCompatDelegate
import android.graphics.Typeface
import android.text.style.TypefaceSpan
import android.text.TextPaint
import com.education4all.CustomTypefaceSpan
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.AlarmManager
import android.app.PendingIntent
import com.education4all.NotificationReceiver
import android.content.BroadcastReceiver
import android.graphics.Paint

class CustomTypefaceSpan(family: String?, private val newType: Typeface) : TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.typeface = tf
        }
    }
}