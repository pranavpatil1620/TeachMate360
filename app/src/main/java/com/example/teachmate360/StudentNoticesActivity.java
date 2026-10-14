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

public class StudentNoticesActivity extends AppCompatActivity {

    private TextView tvStudentNoticeReport;
    private Button btnRefreshStudentNotices;

    private DatabaseReference noticesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_notices);

        noticesReference =
                FirebaseDatabase.getInstance()
                        .getReference("notices");

        tvStudentNoticeReport =
                findViewById(R.id.tvStudentNoticeReport);

        btnRefreshStudentNotices =
                findViewById(R.id.btnRefreshStudentNotices);

        loadNotices();

        btnRefreshStudentNotices.setOnClickListener(
                v -> loadNotices()
        );
    }

    private void loadNotices() {

        tvStudentNoticeReport.setText(
                "Loading notices..."
        );

        noticesReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot) {

                                if (!snapshot.exists()) {

                                    tvStudentNoticeReport.setText(
                                            "No notices available."
                                    );

                                    return;
                                }

                                StringBuilder report =
                                        new StringBuilder();

                                int count = 0;

                                for (DataSnapshot notice :
                                        snapshot.getChildren()) {

                                    String title =
                                            notice.child("title")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String message =
                                            notice.child("message")
                                                    .getValue(
                                                            String.class
                                                    );

                                    String date =
                                            notice.child("date")
                                                    .getValue(
                                                            String.class
                                                    );

                                    if (title == null) {
                                        title = "Untitled Notice";
                                    }

                                    if (message == null) {
                                        message = "No message";
                                    }

                                    if (date == null) {
                                        date = "Not specified";
                                    }

                                    count++;

                                    report.append(
                                            "Notice "
                                    );

                                    report.append(count);

                                    report.append("\n\n");

                                    report.append(
                                            "📌 Title: "
                                    );

                                    report.append(title);

                                    report.append("\n");

                                    report.append(
                                            "📄 Message: "
                                    );

                                    report.append(message);

                                    report.append("\n");

                                    report.append(
                                            "📅 Date: "
                                    );

                                    report.append(date);

                                    report.append("\n");

                                    report.append(
                                            "━━━━━━━━━━━━━━━━━━━━\n\n"
                                    );
                                }

                                tvStudentNoticeReport.setText(
                                        report.toString()
                                );
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error) {

                                tvStudentNoticeReport.setText(
                                        "Failed to load notices."
                                );

                                Toast.makeText(
                                        StudentNoticesActivity.this,
                                        error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}