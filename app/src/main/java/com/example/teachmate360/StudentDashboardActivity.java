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

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvWelcomeStudent;
    private TextView tvStudentEmail;

    // Quick Stats
    private TextView tvStudentAttendance;
    private TextView tvStudentMarks;
    private TextView tvStudentAssignments;
    private TextView tvStudentNotices;

    private Button btnStudentLogout;

    private LinearLayout cardStudentAttendance;
    private LinearLayout cardStudentMarks;
    private LinearLayout cardStudentAssignments;
    private LinearLayout cardStudentNotices;
    private LinearLayout cardStudentProfile;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String studentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");


        // ==========================================
        // FIND VIEWS
        // ==========================================

        tvWelcomeStudent =
                findViewById(R.id.tvWelcomeStudent);

        tvStudentEmail =
                findViewById(R.id.tvStudentEmail);

        // Quick Stats
        tvStudentAttendance =
                findViewById(R.id.tvStudentAttendance);

        tvStudentMarks =
                findViewById(R.id.tvStudentMarks);

        tvStudentAssignments =
                findViewById(R.id.tvStudentAssignments);

        tvStudentNotices =
                findViewById(R.id.tvStudentNotices);


        btnStudentLogout =
                findViewById(R.id.btnStudentLogout);


        cardStudentAttendance =
                findViewById(R.id.cardStudentAttendance);

        cardStudentMarks =
                findViewById(R.id.cardStudentMarks);

        cardStudentAssignments =
                findViewById(R.id.cardStudentAssignments);

        cardStudentNotices =
                findViewById(R.id.cardStudentNotices);

        cardStudentProfile =
                findViewById(R.id.cardStudentProfile);


        // ==========================================
        // LOAD STUDENT DATA
        // ==========================================

        loadStudentData();


        // ==========================================
        // LOAD QUICK STATS
        // ==========================================

        loadStudentStats();


        // ==========================================
        // ATTENDANCE
        // ==========================================

        cardStudentAttendance.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
                            StudentAttendanceActivity.class
                    );

            startActivity(intent);
        });


        // ==========================================
        // MARKS
        // ==========================================

        cardStudentMarks.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
                            StudentMarksActivity.class
                    );

            startActivity(intent);
        });


        // ==========================================
        // ASSIGNMENTS
        // ==========================================

        cardStudentAssignments.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
                            StudentAssignmentsActivity.class
                    );

            startActivity(intent);
        });


        // ==========================================
        // NOTICES
        // ==========================================

        cardStudentNotices.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
                            StudentNoticesActivity.class
                    );

            startActivity(intent);
        });


        // ==========================================
        // PROFILE
        // ==========================================

        cardStudentProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
                            StudentProfileActivity.class
                    );

            startActivity(intent);
        });


        // ==========================================
        // LOGOUT
        // ==========================================

        btnStudentLogout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent =
                    new Intent(
                            StudentDashboardActivity.this,
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


    // ==========================================
    // LOAD STUDENT DATA
    // ==========================================

    private void loadStudentData() {

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
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        String name =
                                snapshot
                                        .child("name")
                                        .getValue(String.class);

                        String email =
                                snapshot
                                        .child("email")
                                        .getValue(String.class);


                        if (name != null &&
                                !name.isEmpty()) {

                            studentName = name;

                            tvWelcomeStudent.setText(
                                    "Welcome, " + name + "!"
                            );
                        }


                        if (email != null &&
                                !email.isEmpty()) {

                            tvStudentEmail.setText(email);
                        }

                        // Name milne ke baad stats load karo
                        loadStudentStats();
                    }
                })
                .addOnFailureListener(error -> {

                    Toast.makeText(
                            StudentDashboardActivity.this,
                            "Unable to load student profile",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }


    // ==========================================
    // LOAD STUDENT QUICK STATS
    // ==========================================

    private void loadStudentStats() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        DatabaseReference attendanceReference =
                FirebaseDatabase.getInstance()
                        .getReference("attendance");

        DatabaseReference marksReference =
                FirebaseDatabase.getInstance()
                        .getReference("marks");

        DatabaseReference assignmentsReference =
                FirebaseDatabase.getInstance()
                        .getReference("assignments");

        DatabaseReference noticesReference =
                FirebaseDatabase.getInstance()
                        .getReference("notices");


        // ==========================================
        // ATTENDANCE
        // ==========================================

        attendanceReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    int present = 0;
                    int total = 0;

                    for (DataSnapshot data :
                            snapshot.getChildren()) {

                        if (data.child(studentName).exists()) {

                            String status =
                                    data.child(studentName)
                                            .getValue(String.class);

                            if (status != null) {

                                total++;

                                if (status.equalsIgnoreCase(
                                        "Present")) {

                                    present++;
                                }
                            }
                        }
                    }

                    double percentage = 0;

                    if (total > 0) {
                        percentage =
                                (present * 100.0) / total;
                    }

                    tvStudentAttendance.setText(
                            String.format(
                                    "%.0f%%",
                                    percentage
                            )
                    );
                });


        // ==========================================
        // MARKS
        // ==========================================

        marksReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    double obtained = 0;
                    double totalMarks = 0;

                    for (DataSnapshot data :
                            snapshot.getChildren()) {

                        if (data.child(studentName).exists()) {

                            Object markObject =
                                    data.child(studentName)
                                            .getValue();

                            if (markObject != null) {

                                try {

                                    double mark =
                                            Double.parseDouble(
                                                    markObject
                                                            .toString()
                                            );

                                    obtained += mark;

                                    // Each exam assumed out of 100
                                    totalMarks += 100;

                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }

                    double percentage = 0;

                    if (totalMarks > 0) {

                        percentage =
                                (obtained * 100.0)
                                        / totalMarks;
                    }

                    tvStudentMarks.setText(
                            String.format(
                                    "%.0f%%",
                                    percentage
                            )
                    );
                });


        // ==========================================
        // ASSIGNMENTS
        // ==========================================

        assignmentsReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    long totalAssignments =
                            snapshot.getChildrenCount();

                    tvStudentAssignments.setText(
                            String.valueOf(
                                    totalAssignments
                            )
                    );
                });


        // ==========================================
        // NOTICES
        // ==========================================

        noticesReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    long totalNotices =
                            snapshot.getChildrenCount();

                    tvStudentNotices.setText(
                            String.valueOf(
                                    totalNotices
                            )
                    );
                });
    }
}