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

public class StudentMarksActivity extends AppCompatActivity {

    private TextView tvOverallPercentage;
    private TextView tvMarksObtained;
    private TextView tvTotalMarks;
    private TextView tvMarksReport;

    private Button btnRefreshMarks;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference marksReference;
    private DatabaseReference studentReference;

    private String studentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_marks);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        marksReference =
                FirebaseDatabase.getInstance()
                        .getReference("marks");

        studentReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        // Find views
        tvOverallPercentage =
                findViewById(R.id.tvOverallPercentage);

        tvMarksObtained =
                findViewById(R.id.tvMarksObtained);

        tvTotalMarks =
                findViewById(R.id.tvTotalMarks);

        tvMarksReport =
                findViewById(R.id.tvMarksReport);

        btnRefreshMarks =
                findViewById(R.id.btnRefreshMarks);


        // Load student name
        loadStudentName();


        // Refresh
        btnRefreshMarks.setOnClickListener(v -> {

            if (!studentName.isEmpty()) {
                loadMarks();
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

                                    loadMarks();

                                } else {

                                    tvMarksReport.setText(
                                            "Student profile not found."
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvMarksReport.setText(
                                        "Unable to load student profile."
                                );
                            }
                        }
                );
    }


    // ==========================================
    // LOAD MARKS
    // ==========================================

    private void loadMarks() {

        tvMarksReport.setText(
                "Loading marks..."
        );

        marksReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {

                                double obtainedMarks = 0;
                                double totalMarks = 0;

                                StringBuilder report =
                                        new StringBuilder();

                                if (!snapshot.exists()) {

                                    tvOverallPercentage.setText(
                                            "Overall Percentage: 0%"
                                    );

                                    tvMarksObtained.setText("0");

                                    tvTotalMarks.setText("0");

                                    tvMarksReport.setText(
                                            "No marks records found."
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


                                for (DataSnapshot mark :
                                        snapshot.getChildren()) {

                                    String subject =
                                            mark.child("subject")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String examName =
                                            mark.child("examName")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String date =
                                            mark.child("date")
                                                    .getValue(
                                                            String.class
                                                    );


                                    if (subject == null) {
                                        subject =
                                                "Unknown Subject";
                                    }

                                    if (examName == null) {
                                        examName =
                                                "Unknown Exam";
                                    }

                                    if (date == null) {
                                        date =
                                                "Unknown Date";
                                    }


                                    /*
                                     * Teacher Marks module saves
                                     * each student's marks using
                                     * the student's name.
                                     */
                                    Object value =
                                            mark.child(studentName)
                                                    .getValue();


                                    if (value != null) {

                                        double marks = 0;

                                        if (value instanceof Number) {

                                            marks =
                                                    ((Number) value)
                                                            .doubleValue();

                                        } else {

                                            try {

                                                marks =
                                                        Double.parseDouble(
                                                                value.toString()
                                                        );

                                            } catch (Exception e) {

                                                continue;
                                            }
                                        }


                                        /*
                                         * Assuming each exam is
                                         * out of 100.
                                         */
                                        double maxMarks = 100;


                                        obtainedMarks += marks;

                                        totalMarks += maxMarks;


                                        report.append(
                                                "Subject: "
                                        );

                                        report.append(
                                                subject
                                        );

                                        report.append(
                                                "\nExam: "
                                        );

                                        report.append(
                                                examName
                                        );

                                        report.append(
                                                "\nMarks: "
                                        );

                                        report.append(
                                                String.format(
                                                        Locale.getDefault(),
                                                        "%.0f / %.0f",
                                                        marks,
                                                        maxMarks
                                                )
                                        );

                                        report.append(
                                                "\nDate: "
                                        );

                                        report.append(
                                                date
                                        );

                                        report.append(
                                                "\n\n"
                                        );
                                    }
                                }


                                // Calculate percentage
                                double percentage = 0;

                                if (totalMarks > 0) {

                                    percentage =
                                            (obtainedMarks * 100.0)
                                                    / totalMarks;
                                }


                                // Display summary
                                tvOverallPercentage.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "Overall Percentage: %.2f%%",
                                                percentage
                                        )
                                );

                                tvMarksObtained.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "%.0f",
                                                obtainedMarks
                                        )
                                );

                                tvTotalMarks.setText(
                                        String.format(
                                                Locale.getDefault(),
                                                "%.0f",
                                                totalMarks
                                        )
                                );


                                if (totalMarks == 0) {

                                    tvMarksReport.setText(
                                            "No marks records found for "
                                                    + studentName
                                                    + "."
                                    );

                                } else {

                                    tvMarksReport.setText(
                                            report.toString()
                                    );
                                }
                            }


                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvMarksReport.setText(
                                        "Failed to load marks."
                                );

                                Toast.makeText(
                                        StudentMarksActivity.this,
                                        error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}