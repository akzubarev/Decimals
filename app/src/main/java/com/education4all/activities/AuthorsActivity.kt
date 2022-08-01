package com.education4all.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.education4all.BuildConfig
import com.education4all.R
import com.education4all.utils.Utils

class AuthorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authors)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val donate = findViewById<TextView>(R.id.donate)
        val ss = SpannableString(getString(R.string.donatetext))
        ss.setSpan(
            URLSpan(getString(R.string.donatelink)),
            0,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        donate.text = ss
        //  donate.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            val versionName = packageManager.getPackageInfo(packageName, 0).versionName
            val versionTV = findViewById<TextView>(R.id.version)
            var versionTag = ""
            if (BuildConfig.BUILD_TYPE == "debug" || Utils.versioningTool() == Utils.VERSIONING_REMOVEFIREBASE) versionTag =
                " (beta)"
            val versionText =
                String.format("Версия: %s%s", versionName, versionTag).trim { it <= ' ' }
            versionTV.text = versionText
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun goToWeb(view: View?) {
        val intent = Intent(this, WebActivity::class.java)
        startActivity(intent)
        finish()
    }
}