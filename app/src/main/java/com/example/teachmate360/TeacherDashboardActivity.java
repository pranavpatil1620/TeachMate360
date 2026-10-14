package com.example.teachmate360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeTeacher;
    private TextView tvTeacherEmail;

    // Quick Stats
    private TextView tvTotalStudents;
    private TextView tvTotalAttendance;
    private TextView tvTotalAssignments;
    private TextView tvTotalNotices;

    private Button btnTeacherLogout;

    private LinearLayout cardAttendance;
    private LinearLayout cardMarks;
    private LinearLayout cardAssignment;
    private LinearLayout cardNotices;
    private LinearLayout cardProfile;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_teacher_dashboard
        );

        // Firebase
        firebaseAuth =
                FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("teachers");


        // =========================================
        // FIND VIEWS
        // =========================================

        tvWelcomeTeacher =
                findViewById(R.id.tvWelcomeTeacher);

        tvTeacherEmail =
                findViewById(R.id.tvTeacherEmail);

        // Quick Stats
        tvTotalStudents =
                findViewById(R.id.tvTotalStudents);

        tvTotalAttendance =
                findViewById(R.id.tvTotalAttendance);

        tvTotalAssignments =
                findViewById(R.id.tvTotalAssignments);

        tvTotalNotices =
                findViewById(R.id.tvTotalNotices);


        // Buttons
        btnTeacherLogout =
                findViewById(R.id.btnTeacherLogout);


        // Cards
        cardAttendance =
                findViewById(R.id.cardAttendance);

        cardMarks =
                findViewById(R.id.cardMarks);

        cardAssignment =
                findViewById(R.id.cardAssignment);

        cardNotices =
                findViewById(R.id.cardNotices);

        cardProfile =
                findViewById(R.id.cardProfile);


        // =========================================
        // LOAD DATA
        // =========================================

        loadTeacherData();

        loadDashboardStats();


        // =========================================
        // ATTENDANCE
        // =========================================

        cardAttendance.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            AttendanceActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // MARKS
        // =========================================

        cardMarks.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            MarksActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // ASSIGNMENTS
        // =========================================

        cardAssignment.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            AssignmentActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // NOTICES
        // =========================================

        cardNotices.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            NoticesActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // PROFILE
        // =========================================

        cardProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            TeacherProfileActivity.class
                    );

            startActivity(intent);
        });


        // =========================================
        // LOGOUT
        // =========================================

        btnTeacherLogout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent =
                    new Intent(
                            TeacherDashboardActivity.this,
                            RoleActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }


    // =================================================
    // LOAD TEACHER DATA
    // =================================================

    private void loadTeacherData() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String uid =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();


        databaseReference
                .child(uid)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (snapshot.exists()) {

                                String name =
                                        snapshot
                                                .child("name")
                                                .getValue(
                                                        String.class
                                                );

                                String email =
                                        snapshot
                                                .child("email")
                                                .getValue(
                                                        String.class
                                                );


                                if (name != null &&
                                        !name.isEmpty()) {

                                    tvWelcomeTeacher.setText(
                                            "Welcome, " +
                                                    name +
                                                    "!"
                                    );
                                }


                                if (email != null &&
                                        !email.isEmpty()) {

                                    tvTeacherEmail.setText(
                                            email
                                    );
                                }
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    TeacherDashboardActivity.this,
                                    "Unable to load teacher profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }


    // =================================================
    // LOAD DASHBOARD STATISTICS
    // =================================================

    private void loadDashboardStats() {

        // =========================================
        // FIREBASE REFERENCES
        // =========================================

        DatabaseReference studentsReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        DatabaseReference attendanceReference =
                FirebaseDatabase.getInstance()
                        .getReference("attendance");

        DatabaseReference assignmentsReference =
                FirebaseDatabase.getInstance()
                        .getReference("assignments");

        DatabaseReference noticesReference =
                FirebaseDatabase.getInstance()
                        .getReference("notices");


        // =========================================
        // TOTAL STUDENTS
        // =========================================

        studentsReference
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            long totalStudents =
                                    snapshot.getChildrenCount();

                            tvTotalStudents.setText(
                                    String.valueOf(
                                            totalStudents
                                    )
                            );
                        }
                );


        // =========================================
        // CURRENT TEACHER ID
        // =========================================

        String teacherId = "";

        if (firebaseAuth.getCurrentUser() != null) {

            teacherId =
                    firebaseAuth
                            .getCurrentUser()
                            .getUid();
        }

        final String currentTeacherId =
                teacherId;


        // =========================================
        // TOTAL ATTENDANCE RECORDS
        // =========================================

        attendanceReference
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            int count = 0;

                            for (DataSnapshot data :
                                    snapshot.getChildren()) {

                                String id =
                                        data.child("teacherId")
                                                .getValue(
                                                        String.class
                                                );

                                if (currentTeacherId.equals(id)) {
                                    count++;
                                }
                            }

                            tvTotalAttendance.setText(
                                    String.valueOf(count)
                            );
                        }
                );


        // =========================================
        // TOTAL ASSIGNMENTS
        // =========================================

        assignmentsReference
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            int count = 0;

                            for (DataSnapshot data :
                                    snapshot.getChildren()) {

                                String id =
                                        data.child("teacherId")
                                                .getValue(
                                                        String.class
                                                );

                                if (currentTeacherId.equals(id)) {
                                    count++;
                                }
                            }

                            tvTotalAssignments.setText(
                                    String.valueOf(count)
                            );
                        }
                );


        // =========================================
        // TOTAL NOTICES
        // =========================================

        noticesReference
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            int count = 0;

                            for (DataSnapshot data :
                                    snapshot.getChildren()) {

                                String id =
                                        data.child("teacherId")
                                                .getValue(
                                                        String.class
                                                );

                                if (currentTeacherId.equals(id)) {
                                    count++;
                                }
                            }

                            tvTotalNotices.setText(
                                    String.valueOf(count)
                            );
                        }
                );
    }
}