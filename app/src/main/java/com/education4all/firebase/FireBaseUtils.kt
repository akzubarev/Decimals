package com.education4all.firebase

import android.content.*
import com.education4all.mathCoachAlg.StatisticMaker
import com.education4all.mathCoachAlg.tours.Tour
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FireBaseUtils {
    private val mDatabase: DatabaseReference
    private val auth: FirebaseAuth
    fun login(context: Context?, callback: LoginCallback) {
        try {
            val user = auth.currentUser
            if (user == null) auth.signInAnonymously()
                .addOnCompleteListener { task: Task<AuthResult?>? -> callback.onCallback() }
                .addOnFailureListener { e: Exception -> reportException(e) }
        } catch (e: Exception) {
            reportException(e)
        }
    }

    val userDBR: DatabaseReference
        get() = mDatabase.child("users")
    val statsDBR: DatabaseReference
        get() {
            val userID = auth.currentUser!!.uid
            return userDBR.child(userID).child("statistics")
        }

    fun updateTours(context: Context) {
        getUserStats(StatisticsCallback { loaded_tours: ArrayList<Tour?> ->
            if (loaded_tours.size != StatisticMaker.getTourCount(context)) {
                val tours = ArrayList<Tour?>()
                for (tourNumber in StatisticMaker.getTourCount(context) - 1 downTo 0) tours.add(
                    StatisticMaker.loadTour(context, tourNumber)
                )
                uploadTours(tours)
            }
        })
    }

    interface LoginCallback {
        fun onCallback()
    }

    interface SettingsCallback {
        fun onCallback(id_text: String?)
    }

    interface StatisticsCallback {
        fun onCallback(loaded_tours: ArrayList<Tour?>?)
    }

    fun getUserStats(id: String?, callback: StatisticsCallback) {
        val tours = ArrayList<Tour?>()
        mDatabase.child("users").child(id!!).child("statistics").get()
            .addOnCompleteListener { task: Task<DataSnapshot?> ->
                if (task.result != null && task.result!!
                        .hasChildren()
                ) for (postSnapshot in task.result!!.children) {
                    val tour = postSnapshot.getValue(Tour::class.java)
                    tours.add(tour)
                }
                callback.onCallback(tours)
            }.addOnFailureListener { e: Exception -> reportException(e) }
    }

    fun getUserStats(callback: StatisticsCallback) {
        getUserStats(auth.currentUser!!.uid, callback)
    }

    fun uploadTours(tours: ArrayList<Tour?>?) {
        try {
            statsDBR.setValue(tours)
        } catch (e: Exception) {
            reportException(e)
        }
    }

    fun uploadTour(tour: Tour?) {
        try {
            statsDBR.push().setValue(tour)
        } catch (e: Exception) {
            reportException(e)
        }
    }

    fun deleteTours() {
        try {
            statsDBR.removeValue()
        } catch (e: Exception) {
            reportException(e)
        }
    }

    companion object {
        private fun reportException(e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.recordException(e)
            crashlytics.setCustomKey("Cause", "FirebaseUtils")
            crashlytics.sendUnsentReports()
        }
    }

    init {
        mDatabase = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
    }
}