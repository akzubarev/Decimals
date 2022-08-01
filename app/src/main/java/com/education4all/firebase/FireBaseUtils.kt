package com.education4all.firebase;

import android.content.Context;

import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tours.Tour;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FireBaseUtils {

    private final DatabaseReference mDatabase;
    private final FirebaseAuth auth;

    public FireBaseUtils() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public void login(Context context, LoginCallback callback) {
        try {
            FirebaseUser user = auth.getCurrentUser();

            if (user == null)
                auth.signInAnonymously().addOnCompleteListener(
                        (Task<AuthResult> task) -> callback.onCallback()
                ).addOnFailureListener(FireBaseUtils::reportException);
        } catch (Exception e) {
            reportException(e);
        }
    }

    public DatabaseReference getUserDBR() {
        return mDatabase.child("users");
    }

    public DatabaseReference getStatsDBR() {
        String userID = auth.getCurrentUser().getUid();
        return getUserDBR().child(userID).child("statistics");
    }

    public void updateTours(Context context) {
        getUserStats(loaded_tours -> {
            if (loaded_tours.size() != StatisticMaker.getTourCount(context)) {
                ArrayList<Tour> tours = new ArrayList<>();
                for (int tourNumber = StatisticMaker.getTourCount(context) - 1; tourNumber >= 0; --tourNumber)
                    tours.add(StatisticMaker.loadTour(context, tourNumber));
                uploadTours(tours);
            }
        });
    }

    public interface LoginCallback {
        void onCallback();
    }

    public interface SettingsCallback {
        void onCallback(String id_text);
    }

    public interface StatisticsCallback {
        void onCallback(ArrayList<Tour> loaded_tours);
    }

    public void getUserStats(String id, StatisticsCallback callback) {
        ArrayList<Tour> tours = new ArrayList<>();
        mDatabase.child("users").child(id).child("statistics").get().addOnCompleteListener(
                (Task<DataSnapshot> task) -> {
                    if (task.getResult() != null && task.getResult().hasChildren())
                        for (DataSnapshot postSnapshot : task.getResult().getChildren()) {
                            Tour tour = postSnapshot.getValue(Tour.class);
                            tours.add(tour);
                        }
                    callback.onCallback(tours);
                }
        ).addOnFailureListener(FireBaseUtils::reportException);
    }

    public void getUserStats(StatisticsCallback callback) {
        getUserStats(auth.getCurrentUser().getUid(), callback);
    }

    private static void reportException(Exception e) {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.recordException(e);
        crashlytics.setCustomKey("Cause", "FirebaseUtils");
        crashlytics.sendUnsentReports();
    }

    public void uploadTours(ArrayList<Tour> tours) {
        try {
            getStatsDBR().setValue(tours);
        } catch (Exception e) {
            reportException(e);
        }
    }

    public void uploadTour(Tour tour) {
        try {
            getStatsDBR().push().setValue(tour);
        } catch (Exception e) {
            reportException(e);
        }
    }

    public void deleteTours() {
        try {
            getStatsDBR().removeValue();
        } catch (Exception e) {
            reportException(e);
        }
    }
}
