package com.example.teachmate360;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class StudentAttendanceActivity extends AppCompatActivity {

    private TextView tvOverallAttendance;
    private TextView tvPresent;
    private TextView tvTotalLectures;
    private TextView tvAttendanceReport;

    private Button btnRefreshAttendance;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference attendanceReference;
    private DatabaseReference studentReference;

    private String studentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_attendance);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        attendanceReference =
                FirebaseDatabase.getInstance()
                        .getReference("attendance");

        studentReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        // Find views
        tvOverallAttendance =
                findViewById(R.id.tvOverallAttendance);

        tvPresent =
                findViewById(R.id.tvPresent);

        tvTotalLectures =
                findViewById(R.id.tvTotalLectures);

        tvAttendanceReport =
                findViewById(R.id.tvAttendanceReport);

        btnRefreshAttendance =
                findViewById(R.id.btnRefreshAttendance);


        // Load student name
        loadStudentName();


        // Refresh
        btnRefreshAttendance.setOnClickListener(v -> {

            if (!studentName.isEmpty()) {
                loadAttendance();
            } else {
                loadStudentName();
            }
        });
    }


    // ==========================================
    // LOAD STUDENT NAME
    // ==========================================

    private void loadStudentName() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        String uid =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        studentReference
                .child(uid)
                .child("name")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    studentName =
                                            snapshot.getValue(
                                                    String.class
                                            );

                                    loadAttendance();

                                } else {

                                    tvAttendanceReport.setText(
                                            "Student profile not found."
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvAttendanceReport.setText(
                                        "Unable to load student profile."
                                );
                            }
                        }
                );
    }


    // ==========================================
    // LOAD ATTENDANCE
    // ==========================================

    private void loadAttendance() {

        tvAttendanceReport.setText(
                "Loading attendance..."
        );

        attendanceReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {

                                int totalLectures = 0;
                                int presentLectures = 0;

                                StringBuilder report =
                                        new StringBuilder();

                                if (!snapshot.exists()) {

                                    tvOverallAttendance.setText(
                                            "Overall Attendance: 0%"
                                    );

                                    tvPresent.setText("0");

                                    tvTotalLectures.setText("0");

                                    tvAttendanceReport.setText(
                                            "No attendance records found."
                                    );

                                    return;
                                }


                                report.append(
                                        "Student: "
                                );

                                report.append(
                                        studentName
                                );

                                report.append(
                                        "\n\n"
                                );


                                for (DataSnapshot attendance :
                                        snapshot.getChildren()) {


                                    // Subject
                                    String subject =
                                            attendance
                                                    .child("subject")
                                                    .getValue(
                                                            String.class
                                                    );


                                    // Date
                                    String date =
                                            attendance
                                                    .child("date")
                                                    .getValue(
                                                            String.class
                                                    );


                                    if (subject == null) {
                                        subject = "Unknown Subject";
                                    }

                                    if (date == null) {
                                        date = "Unknown Date";
                                    }


                                    // Student attendance
                                    String status =
                                            attendance
                                                    .child(studentName)
                                                    .getValue(
                                                            String.class
                                                    );


                                    if (status != null) {

                                        totalLectures++;


                                        if (status.equalsIgnoreCase(
                                                "Present"
                                        )) {

                                            presentLectures++;
                                        }


                                        report.append(
                                                "Subject: "
                                        );

                                        report.append(
                                                subject
                                        );

                                        report.append(
                                                "\nDate: "
                                        );

                                        report.append(
                                                date
                                        );

                                        report.append(
                                                "\nStatus: "
                                        );

                                        report.append(
                                                status
                                        );

                                        report.append(
                                                "\n\n"
                                        );
                                    }
                                }


                                // Calculate percentage
                                double percentage = 0;

                                if (totalLectures > 0) {

                                    percentage =
                                            (presentLectures * 100.0)
                                                    / totalLectures;
                                }


                                // Display summary
                                tvOverallAttendance.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "Overall Attendance: %.2f%%",
                                                percentage
                                        )
                                );

                                tvPresent.setText(
                                        String.valueOf(
                                                presentLectures
                                        )
                                );

                                tvTotalLectures.setText(
                                        String.valueOf(
                                                totalLectures
                                        )
                                );


                                if (totalLectures == 0) {

                                    tvAttendanceReport.setText(
                                            "No attendance records found for "
                                                    + studentName
                                                    + "."
                                    );

                                } else {

                                    tvAttendanceReport.setText(
                                            report.toString()
                                    );
                                }
                            }


                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvAttendanceReport.setText(
                                        "Failed to load attendance."
                                );

                                Toast.makeText(
                                        StudentAttendanceActivity.this,
                                        error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}