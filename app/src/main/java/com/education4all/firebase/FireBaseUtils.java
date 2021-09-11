package com.education4all.firebase;

import android.content.Context;

import com.education4all.mathCoachAlg.StatisticMaker;
import com.education4all.mathCoachAlg.tours.Tour;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FireBaseUtils {

    private static FirebaseUser user;
    private static DatabaseReference usersDBR;
    private static DatabaseReference statisticsDBR;

    private static void initFirebase(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersDBR = database.getReference().child("users");
        statisticsDBR = usersDBR.child(user.getUid()).child("statistics");

        if (getUserStats().size() != StatisticMaker.getTourCount(context)) {
            ArrayList<Tour> tours = new ArrayList<>();
            for (int tourNumber = StatisticMaker.getTourCount(context) - 1; tourNumber >= 0; --tourNumber)
                tours.add(StatisticMaker.loadTour(context, tourNumber));
            uploadTours(tours);
        }
    }

    public static void login(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null)
            auth.signInAnonymously().addOnCompleteListener((Task<AuthResult> task) ->
            {
                user = task.getResult().getUser();
                initFirebase(context);
            });
        else
            initFirebase(context);
    }


    public static ArrayList<Tour> getUserStats(String id) {
        ArrayList<Tour> tours = new ArrayList<>();
        usersDBR.child(id).child("statistics").get().addOnCompleteListener(
                (Task<DataSnapshot> task) -> {
                    for (DataSnapshot postSnapshot : task.getResult().getChildren()) {
                        Tour tour = postSnapshot.getValue(Tour.class);
                        tours.add(tour);
                    }
                }
        );
        return tours;
    }

    public static ArrayList<Tour> getUserStats() {
        return getUserStats(user.getUid());
    }

    public static void uploadTours(ArrayList<Tour> tours) {
        statisticsDBR.setValue(tours);
    }

    public static void uploadTour(Tour tour) {
        statisticsDBR.push().setValue(tour);
    }

    public static void deleteTours() {
        statisticsDBR.child("statistics").removeValue();
    }
}
