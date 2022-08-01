package com.education4all.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.education4all.R
import com.education4all.activities.ComplexityActivity
import com.education4all.activities.tabs.SectionsPagerAdapter
import com.education4all.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class SettingsMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_main)
        val spa = SectionsPagerAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        viewPager.adapter = spa
        val tabs = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(
            tabs, viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            val name = if (position == 0) "Задания" else "Приложение"
            val color =
                if (position == 0) resources.getColor(R.color.main) else resources.getColor(R.color.shadowed)
            tab.setCustomView(R.layout.tab) //TextView
            if (tab.customView is TextView) {
                val tv = tab.customView as TextView?
                tv!!.text = name
                tv.setTextColor(color)
            }
        }.attach()
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.customView is TextView) (tab.customView as TextView?)!!.setTextColor(
                    resources.getColor(R.color.main)
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.customView is TextView) (tab.customView as TextView?)!!.setTextColor(
                    resources.getColor(R.color.shadowed)
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val intent = intent
        val fromtask = intent.getBooleanExtra("FromTask", false)
        if (fromtask) {
            val dialog = AlertDialog.Builder(this) //, R.style.AlertDialogTheme)
                //                    .setTitle("Ошибка")
                .setMessage("Пожалуйста, сначала выберите виды заданий.")
                .setCancelable(false)
                .setPositiveButton("Ок") { dialog1: DialogInterface?, which: Int -> }.create()
            dialog.show()
            Utils.FixDialog(dialog, applicationContext) // почему-то нужно для планшетов
        }
    }

    fun startCompl(view: View) {
        val intent = Intent(this, ComplexityActivity::class.java)
        val p: Int = view.tag as kotlin.String?. toInt ()
        intent.putExtra("Type", p)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
    } //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        MenuInflater inflater = getMenuInflater();
    //        inflater.inflate(R.menu.menu_settings, menu);
    //        return true;
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        if (item.getItemId() == R.id.action_logout) {
    //            FirebaseAuth.getInstance().signOut();
    //            startActivity(new Intent(this, MainActivity.class));
    //            finish();
    //            return true;
    //        } else
    //            return super.onOptionsItemSelected(item);
    //    }
}