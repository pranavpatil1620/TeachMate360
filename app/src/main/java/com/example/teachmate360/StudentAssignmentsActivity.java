package com.example.teachmate360;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentAssignmentsActivity extends AppCompatActivity {

    private TextView tvAssignmentReport;
    private Button btnRefreshAssignments;

    private DatabaseReference assignmentsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_student_assignments
        );

        // Firebase
        assignmentsReference =
                FirebaseDatabase.getInstance()
                        .getReference("assignments");

        // Find views
        tvAssignmentReport =
                findViewById(
                        R.id.tvAssignmentReport
                );

        btnRefreshAssignments =
                findViewById(
                        R.id.btnRefreshAssignments
                );


        // Load assignments
        loadAssignments();


        // Refresh
        btnRefreshAssignments.setOnClickListener(
                v -> loadAssignments()
        );
    }


    // ==========================================
    // LOAD ASSIGNMENTS
    // ==========================================

    private void loadAssignments() {

        tvAssignmentReport.setText(
                "Loading assignments..."
        );

        assignmentsReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {

                                if (!snapshot.exists()) {

                                    tvAssignmentReport.setText(
                                            "No assignments available."
                                    );

                                    return;
                                }


                                StringBuilder report =
                                        new StringBuilder();


                                int count = 0;


                                for (DataSnapshot assignment :
                                        snapshot.getChildren()) {

                                    String subject =
                                            assignment
                                                    .child("subject")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String title =
                                            assignment
                                                    .child("title")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String description =
                                            assignment
                                                    .child("description")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String dueDate =
                                            assignment
                                                    .child("dueDate")
                                                    .getValue(
                                                            String.class
                                                    );


                                    if (subject == null) {
                                        subject =
                                                "Not specified";
                                    }

                                    if (title == null) {
                                        title =
                                                "Untitled Assignment";
                                    }

                                    if (description == null) {
                                        description =
                                                "No description";
                                    }

                                    if (dueDate == null) {
                                        dueDate =
                                                "Not specified";
                                    }


                                    count++;


                                    report.append(
                                            "Assignment "
                                    );

                                    report.append(
                                            count
                                    );

                                    report.append(
                                            "\n\n"
                                    );


                                    report.append(
                                            "📚 Subject: "
                                    );

                                    report.append(
                                            subject
                                    );

                                    report.append(
                                            "\n"
                                    );


                                    report.append(
                                            "📝 Title: "
                                    );

                                    report.append(
                                            title
                                    );

                                    report.append(
                                            "\n"
                                    );


                                    report.append(
                                            "📄 Description: "
                                    );

                                    report.append(
                                            description
                                    );

                                    report.append(
                                            "\n"
                                    );


                                    report.append(
                                            "📅 Due Date: "
                                    );

                                    report.append(
                                            dueDate
                                    );

                                    report.append(
                                            "\n"
                                    );


                                    report.append(
                                            "━━━━━━━━━━━━━━━━━━━━\n\n"
                                    );
                                }


                                tvAssignmentReport.setText(
                                        report.toString()
                                );
                            }


                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvAssignmentReport.setText(
                                        "Failed to load assignments."
                                );

                                Toast.makeText(
                                        StudentAssignmentsActivity.this,
                                        error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}