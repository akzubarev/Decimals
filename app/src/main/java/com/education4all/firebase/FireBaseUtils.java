package com.education4all.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.education4all.activities.StatiscticsActivity;
import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tours.Tour;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FireBaseUtils {

    private static FirebaseUser user;
    private static DatabaseReference usersDBR;
    private static DatabaseReference statisticsDBR;
    private static boolean online = true;

//    private static void checkConnection() {
//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                online = snapshot.getValue(Boolean.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                reportException(error.toException());
//            }
//        });
//
//    }

    private static void initFirebase(Context context) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            usersDBR = database.getReference().child("users");
            statisticsDBR = usersDBR.child(user.getUid()).child("statistics");

            getUserStats(loaded_tours -> {
                if (loaded_tours.size() != StatisticMaker.getTourCount(context)) {
                    ArrayList<Tour> tours = new ArrayList<>();
                    for (int tourNumber = StatisticMaker.getTourCount(context) - 1; tourNumber >= 0; --tourNumber)
                        tours.add(StatisticMaker.loadTour(context, tourNumber));
                    uploadTours(tours);
                }
            });
        } catch (Exception e) {
            reportException(e);
        }
    }

    public static void login(Context context) {
//        checkConnection();
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            if (user == null)
                auth.signInAnonymously().addOnCompleteListener((Task<AuthResult> task) ->
                {
                    if (task.isSuccessful()) {
                        user = task.getResult().getUser();
                        initFirebase(context);
                    } else
                        online = false;
                }).addOnFailureListener((Exception e) -> {
                    online = false;
                    reportException(e);
                });
            else
                initFirebase(context);
        } catch (Exception e) {
            reportException(e);
        }
    }

    public static FirebaseUser getUser() {
        return user;
    }


    public interface StatisticsCallback {
        void onCallback(ArrayList<Tour> loaded_tours);
    }

    public static void getUserStats(String id, StatisticsCallback callback) {
        ArrayList<Tour> tours = new ArrayList<>();
        if (online)
            usersDBR.child(id).child("statistics").get().addOnCompleteListener(
                    (Task<DataSnapshot> task) -> {
                        if (task.isSuccessful()) {
                            for (DataSnapshot postSnapshot : task.getResult().getChildren()) {
                                Tour tour = postSnapshot.getValue(Tour.class);
                                tours.add(tour);
                            }
                            callback.onCallback(tours);
                        } else
                            online = false;
                    }
            ).addOnFailureListener((Exception e) -> {
                online = false;
                reportException(e);
            });
    }

    public static void getUserStats(StatisticsCallback callback) {
        getUserStats(user.getUid(), callback);
    }

    private static void reportException(Exception e) {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.recordException(e);
        crashlytics.setCustomKey("Cause", "FirebaseUtils");
        crashlytics.sendUnsentReports();
    }

    public static void uploadTours(ArrayList<Tour> tours) {
        if (online)
            try {
                statisticsDBR.setValue(tours);
            } catch (Exception e) {
                reportException(e);
            }
    }

    public static void uploadTour(Tour tour) {
        if (online)
            try {
                statisticsDBR.push().setValue(tour);
            } catch (Exception e) {
                reportException(e);
            }
    }

    public static void deleteTours() {
        if (online)
            try {
                statisticsDBR.child("statistics").removeValue();
            } catch (Exception e) {
                reportException(e);
            }
    }
}
