package com.example.teachmate360;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private EditText etSubject;

    private TextView tvAttendanceDate;
    private TextView tvAttendancePercentage;

    private CheckBox cbStudent1;
    private CheckBox cbStudent2;
    private CheckBox cbStudent3;
    private CheckBox cbStudent4;
    private CheckBox cbStudent5;

    private Button btnSaveAttendance;
    private Button btnViewPercentage;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String currentDate;

    // Edit ke liye
    private String editingAttendanceId = "";

    // Attendance records ke dynamic cards
    private LinearLayout attendanceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attendance);

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("attendance");

        // =====================================================
        // FIND VIEWS
        // =====================================================

        etSubject =
                findViewById(R.id.etSubject);

        tvAttendanceDate =
                findViewById(R.id.tvAttendanceDate);

        tvAttendancePercentage =
                findViewById(R.id.tvAttendancePercentage);

        cbStudent1 =
                findViewById(R.id.cbStudent1);

        cbStudent2 =
                findViewById(R.id.cbStudent2);

        cbStudent3 =
                findViewById(R.id.cbStudent3);

        cbStudent4 =
                findViewById(R.id.cbStudent4);

        cbStudent5 =
                findViewById(R.id.cbStudent5);

        btnSaveAttendance =
                findViewById(R.id.btnSaveAttendance);

        btnViewPercentage =
                findViewById(R.id.btnViewPercentage);

        // =====================================================
        // CREATE DYNAMIC CONTAINER
        // =====================================================

        ViewGroup parent =
                (ViewGroup) tvAttendancePercentage.getParent();

        attendanceContainer =
                new LinearLayout(this);

        attendanceContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        attendanceContainer.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // Percentage TextView ko hide rakho
        tvAttendancePercentage.setVisibility(
                TextView.GONE
        );

        // Dynamic records ko percentage TextView ke upar add karo
        int reportIndex =
                parent.indexOfChild(
                        tvAttendancePercentage
                );

        parent.addView(
                attendanceContainer,
                reportIndex
        );

        // =====================================================
        // CURRENT DATE
        // =====================================================

        currentDate =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                ).format(new Date());

        tvAttendanceDate.setText(
                "Date: " + currentDate
        );

        // =====================================================
        // SAVE
        // =====================================================

        btnSaveAttendance.setOnClickListener(
                v -> saveAttendance()
        );

        // =====================================================
        // VIEW ATTENDANCE
        // =====================================================

        btnViewPercentage.setOnClickListener(
                v -> calculateAttendancePercentage()
        );
    }

    // =========================================================
    // SAVE / UPDATE ATTENDANCE
    // =========================================================

    private void saveAttendance() {

        String subject =
                etSubject.getText()
                        .toString()
                        .trim();

        if (subject.isEmpty()) {

            etSubject.setError(
                    "Enter subject"
            );

            etSubject.requestFocus();

            return;
        }

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // ATTENDANCE DATA
        // =====================================================

        Map<String, Object> attendanceData =
                new HashMap<>();

        attendanceData.put(
                "teacherId",
                firebaseAuth
                        .getCurrentUser()
                        .getUid()
        );

        attendanceData.put(
                "subject",
                subject
        );

        attendanceData.put(
                "date",
                currentDate
        );

        attendanceData.put(
                "Pranav Jadhav",
                cbStudent1.isChecked()
                        ? "Present"
                        : "Absent"
        );

        attendanceData.put(
                "Vedarth Dhepe",
                cbStudent2.isChecked()
                        ? "Present"
                        : "Absent"
        );

        attendanceData.put(
                "Pranjal Mane",
                cbStudent3.isChecked()
                        ? "Present"
                        : "Absent"
        );

        attendanceData.put(
                "Abhi Rathod",
                cbStudent4.isChecked()
                        ? "Present"
                        : "Absent"
        );

        attendanceData.put(
                "Shreya Deshmukh",
                cbStudent5.isChecked()
                        ? "Present"
                        : "Absent"
        );

        // =====================================================
        // UPDATE EXISTING ATTENDANCE
        // =====================================================

        if (!editingAttendanceId.isEmpty()) {

            btnSaveAttendance.setEnabled(false);

            btnSaveAttendance.setText(
                    "UPDATING..."
            );

            databaseReference
                    .child(editingAttendanceId)
                    .updateChildren(attendanceData)
                    .addOnCompleteListener(task -> {

                        btnSaveAttendance.setEnabled(true);

                        btnSaveAttendance.setText(
                                "SAVE ATTENDANCE"
                        );

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    AttendanceActivity.this,
                                    "Attendance updated successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            editingAttendanceId = "";

                            clearForm();

                            // Percentage automatically show nahi hoga
                            tvAttendancePercentage.setVisibility(
                                    TextView.GONE
                            );

                        } else {

                            Toast.makeText(
                                    AttendanceActivity.this,
                                    "Failed to update attendance",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

            return;
        }

        // =====================================================
        // SAVE NEW ATTENDANCE
        // =====================================================

        btnSaveAttendance.setEnabled(false);

        btnSaveAttendance.setText(
                "SAVING..."
        );

        String attendanceId =
                databaseReference
                        .push()
                        .getKey();

        if (attendanceId == null) {

            btnSaveAttendance.setEnabled(true);

            btnSaveAttendance.setText(
                    "SAVE ATTENDANCE"
            );

            return;
        }

        databaseReference
                .child(attendanceId)
                .setValue(attendanceData)
                .addOnCompleteListener(task -> {

                    btnSaveAttendance.setEnabled(true);

                    btnSaveAttendance.setText(
                            "SAVE ATTENDANCE"
                    );

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                AttendanceActivity.this,
                                "Attendance saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        clearForm();

                        // Save ke baad automatically
                        // percentage show nahi hoga.
                        tvAttendancePercentage.setVisibility(
                                TextView.GONE
                        );

                    } else {

                        Toast.makeText(
                                AttendanceActivity.this,
                                "Failed to save attendance",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =========================================================
    // VIEW ATTENDANCE RECORDS + PERCENTAGE
    // =========================================================

    private void calculateAttendancePercentage() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Old cards clear
        attendanceContainer.removeAllViews();

        tvAttendancePercentage.setVisibility(
                TextView.GONE
        );

        btnViewPercentage.setEnabled(false);

        btnViewPercentage.setText(
                "LOADING..."
        );

        String teacherId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        databaseReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    int totalLectures = 0;

                    int pranavPresent = 0;
                    int vedarthPresent = 0;
                    int pranjalPresent = 0;
                    int abhiPresent = 0;
                    int shreyaPresent = 0;

                    // =================================================
                    // READ ALL RECORDS
                    // =================================================

                    for (DataSnapshot attendanceSnapshot :
                            snapshot.getChildren()) {

                        String savedTeacherId =
                                attendanceSnapshot
                                        .child("teacherId")
                                        .getValue(
                                                String.class
                                        );

                        if (!teacherId.equals(
                                savedTeacherId
                        )) {

                            continue;
                        }

                        totalLectures++;

                        // Pranav
                        if ("Present".equals(
                                attendanceSnapshot
                                        .child(
                                                "Pranav Jadhav"
                                        )
                                        .getValue(
                                                String.class
                                        )
                        )) {

                            pranavPresent++;
                        }

                        // Vedarth
                        if ("Present".equals(
                                attendanceSnapshot
                                        .child(
                                                "Vedarth Dhepe"
                                        )
                                        .getValue(
                                                String.class
                                        )
                        )) {

                            vedarthPresent++;
                        }

                        // Pranjal
                        if ("Present".equals(
                                attendanceSnapshot
                                        .child(
                                                "Pranjal Mane"
                                        )
                                        .getValue(
                                                String.class
                                        )
                        )) {

                            pranjalPresent++;
                        }

                        // Abhi
                        if ("Present".equals(
                                attendanceSnapshot
                                        .child(
                                                "Abhi Rathod"
                                        )
                                        .getValue(
                                                String.class
                                        )
                        )) {

                            abhiPresent++;
                        }

                        // Shreya
                        if ("Present".equals(
                                attendanceSnapshot
                                        .child(
                                                "Shreya Deshmukh"
                                        )
                                        .getValue(
                                                String.class
                                        )
                        )) {

                            shreyaPresent++;
                        }

                        // =================================================
                        // CREATE RECORD CARD
                        // =================================================

                        String attendanceId =
                                attendanceSnapshot.getKey();

                        String subject =
                                attendanceSnapshot
                                        .child("subject")
                                        .getValue(
                                                String.class
                                        );

                        String date =
                                attendanceSnapshot
                                        .child("date")
                                        .getValue(
                                                String.class
                                        );

                        String pranavStatus =
                                getStatus(
                                        attendanceSnapshot,
                                        "Pranav Jadhav"
                                );

                        String vedarthStatus =
                                getStatus(
                                        attendanceSnapshot,
                                        "Vedarth Dhepe"
                                );

                        String pranjalStatus =
                                getStatus(
                                        attendanceSnapshot,
                                        "Pranjal Mane"
                                );

                        String abhiStatus =
                                getStatus(
                                        attendanceSnapshot,
                                        "Abhi Rathod"
                                );

                        String shreyaStatus =
                                getStatus(
                                        attendanceSnapshot,
                                        "Shreya Deshmukh"
                                );

                        if (subject == null) {
                            subject = "Not specified";
                        }

                        if (date == null) {
                            date = "Not specified";
                        }

                        createAttendanceCard(
                                attendanceId,
                                subject,
                                date,
                                pranavStatus,
                                vedarthStatus,
                                pranjalStatus,
                                abhiStatus,
                                shreyaStatus
                        );
                    }

                    btnViewPercentage.setEnabled(true);

                    btnViewPercentage.setText(
                            "VIEW ATTENDANCE %"
                    );

                    // =================================================
                    // NO RECORDS
                    // =================================================

                    if (totalLectures == 0) {

                        TextView noAttendance =
                                new TextView(
                                        AttendanceActivity.this
                                );

                        noAttendance.setText(
                                "📊 No attendance records found."
                        );

                        noAttendance.setTextSize(
                                16
                        );

                        noAttendance.setTextColor(
                                Color.rgb(
                                        23,
                                        43,
                                        77
                                )
                        );

                        noAttendance.setGravity(
                                Gravity.CENTER
                        );

                        noAttendance.setPadding(
                                20,
                                30,
                                20,
                                30
                        );

                        attendanceContainer.addView(
                                noAttendance
                        );

                        return;
                    }

                    // =================================================
                    // CALCULATE PERCENTAGES
                    // =================================================

                    double pranavPercentage =
                            calculatePercentage(
                                    pranavPresent,
                                    totalLectures
                            );

                    double vedarthPercentage =
                            calculatePercentage(
                                    vedarthPresent,
                                    totalLectures
                            );

                    double pranjalPercentage =
                            calculatePercentage(
                                    pranjalPresent,
                                    totalLectures
                            );

                    double abhiPercentage =
                            calculatePercentage(
                                    abhiPresent,
                                    totalLectures
                            );

                    double shreyaPercentage =
                            calculatePercentage(
                                    shreyaPresent,
                                    totalLectures
                            );

                    // =================================================
                    // REPORT
                    // =================================================

                    String result =
                            "📊 ATTENDANCE REPORT\n\n" +

                                    "Total Lectures: " +
                                    totalLectures +
                                    "\n\n" +

                                    "Pranav Jadhav  -  " +
                                    formatPercentage(
                                            pranavPercentage
                                    ) +

                                    "\n\n" +

                                    "Vedarth Dhepe  -  " +
                                    formatPercentage(
                                            vedarthPercentage
                                    ) +

                                    "\n\n" +

                                    "Pranjal Mane  -  " +
                                    formatPercentage(
                                            pranjalPercentage
                                    ) +

                                    "\n\n" +

                                    "Abhi Rathod  -  " +
                                    formatPercentage(
                                            abhiPercentage
                                    ) +

                                    "\n\n" +

                                    "Shreya Deshmukh  -  " +
                                    formatPercentage(
                                            shreyaPercentage
                                    );

                    tvAttendancePercentage.setText(
                            result
                    );

                    tvAttendancePercentage.setVisibility(
                            TextView.VISIBLE
                    );
                })
                .addOnFailureListener(error -> {

                    btnViewPercentage.setEnabled(true);

                    btnViewPercentage.setText(
                            "VIEW ATTENDANCE %"
                    );

                    Toast.makeText(
                            AttendanceActivity.this,
                            "Unable to calculate attendance",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =========================================================
    // CREATE ATTENDANCE CARD
    // =========================================================

    private void createAttendanceCard(
            String attendanceId,
            String subject,
            String date,
            String pranavStatus,
            String vedarthStatus,
            String pranjalStatus,
            String abhiStatus,
            String shreyaStatus
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        card.setBackground(
                getDrawable(
                        R.drawable.edittext_background
                )
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        card.setLayoutParams(
                cardParams
        );

        // =====================================================
        // HEADING
        // =====================================================

        TextView heading =
                new TextView(this);

        heading.setText(
                "📋 Attendance"
        );

        heading.setTextSize(
                20
        );

        heading.setTypeface(
                null,
                Typeface.BOLD
        );

        heading.setTextColor(
                Color.rgb(
                        23,
                        43,
                        77
                )
        );

        heading.setPadding(
                0,
                0,
                0,
                12
        );

        card.addView(
                heading
        );

        // =====================================================
        // DETAILS
        // =====================================================

        card.addView(
                createDetailText(
                        "📚 Subject: " + subject
                )
        );

        card.addView(
                createDetailText(
                        "📅 Date: " + date
                )
        );

        card.addView(
                createDetailText(
                        "Pranav Jadhav: "
                                + pranavStatus
                )
        );

        card.addView(
                createDetailText(
                        "Vedarth Dhepe: "
                                + vedarthStatus
                )
        );

        card.addView(
                createDetailText(
                        "Pranjal Mane: "
                                + pranjalStatus
                )
        );

        card.addView(
                createDetailText(
                        "Abhi Rathod: "
                                + abhiStatus
                )
        );

        TextView shreya =
                createDetailText(
                        "Shreya Deshmukh: "
                                + shreyaStatus
                );

        shreya.setPadding(
                0,
                6,
                0,
                18
        );

        card.addView(
                shreya
        );

        // =====================================================
        // BUTTON ROW
        // =====================================================

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setGravity(
                Gravity.CENTER
        );

        // =====================================================
        // EDIT BUTTON
        // =====================================================

        Button editButton =
                new Button(this);

        editButton.setText(
                "✏️ EDIT"
        );

        editButton.setTextSize(
                13
        );

        editButton.setTextColor(
                Color.WHITE
        );

        editButton.setTypeface(
                null,
                Typeface.BOLD
        );

        editButton.setGravity(
                Gravity.CENTER
        );

        editButton.setMinHeight(0);

        editButton.setMinimumHeight(0);

        editButton.setPadding(
                4,
                0,
                4,
                0
        );

        editButton.setBackground(
                getDrawable(
                        R.drawable.rounded_button1
                )
        );

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(
                        0,
                        60,
                        1
                );

        editParams.setMargins(
                0,
                0,
                6,
                0
        );

        editButton.setLayoutParams(
                editParams
        );

        editButton.setOnClickListener(
                v -> editAttendance(
                        attendanceId
                )
        );

        buttonRow.addView(
                editButton
        );

        // =====================================================
        // DELETE BUTTON
        // =====================================================

        Button deleteButton =
                new Button(this);

        deleteButton.setText(
                "🗑️ DELETE"
        );

        deleteButton.setTextSize(
                13
        );

        deleteButton.setTextColor(
                Color.WHITE
        );

        deleteButton.setTypeface(
                null,
                Typeface.BOLD
        );

        deleteButton.setGravity(
                Gravity.CENTER
        );

        deleteButton.setMinHeight(0);

        deleteButton.setMinimumHeight(0);

        deleteButton.setPadding(
                4,
                0,
                4,
                0
        );

        deleteButton.setBackground(
                getDrawable(
                        R.drawable.rounded_button1
                )
        );

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0,
                        60,
                        1
                );

        deleteParams.setMargins(
                6,
                0,
                0,
                0
        );

        deleteButton.setLayoutParams(
                deleteParams
        );

        deleteButton.setOnClickListener(
                v -> confirmDelete(
                        attendanceId,
                        subject,
                        date
                )
        );

        buttonRow.addView(
                deleteButton
        );

        card.addView(
                buttonRow
        );

        attendanceContainer.addView(
                card
        );
    }

    // =========================================================
    // DETAIL TEXT
    // =========================================================

    private TextView createDetailText(
            String text
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(
                15
        );

        textView.setTextColor(
                Color.rgb(
                        23,
                        43,
                        77
                )
        );

        textView.setPadding(
                0,
                6,
                0,
                6
        );

        return textView;
    }

    // =========================================================
    // GET STATUS
    // =========================================================

    private String getStatus(
            DataSnapshot snapshot,
            String studentName
    ) {

        String status =
                snapshot
                        .child(studentName)
                        .getValue(
                                String.class
                        );

        if (status == null) {
            return "Absent";
        }

        return status;
    }

    // =========================================================
    // EDIT ATTENDANCE
    // =========================================================

    private void editAttendance(
            String attendanceId
    ) {

        databaseReference
                .child(attendanceId)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (!snapshot.exists()) {

                                Toast.makeText(
                                        this,
                                        "Attendance record not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String subject =
                                    snapshot
                                            .child("subject")
                                            .getValue(
                                                    String.class
                                            );

                            String pranavStatus =
                                    getStatus(
                                            snapshot,
                                            "Pranav Jadhav"
                                    );

                            String vedarthStatus =
                                    getStatus(
                                            snapshot,
                                            "Vedarth Dhepe"
                                    );

                            String pranjalStatus =
                                    getStatus(
                                            snapshot,
                                            "Pranjal Mane"
                                    );

                            String abhiStatus =
                                    getStatus(
                                            snapshot,
                                            "Abhi Rathod"
                                    );

                            String shreyaStatus =
                                    getStatus(
                                            snapshot,
                                            "Shreya Deshmukh"
                                    );

                            if (subject != null) {

                                etSubject.setText(
                                        subject
                                );
                            }

                            // Set checkboxes
                            cbStudent1.setChecked(
                                    "Present".equals(
                                            pranavStatus
                                    )
                            );

                            cbStudent2.setChecked(
                                    "Present".equals(
                                            vedarthStatus
                                    )
                            );

                            cbStudent3.setChecked(
                                    "Present".equals(
                                            pranjalStatus
                                    )
                            );

                            cbStudent4.setChecked(
                                    "Present".equals(
                                            abhiStatus
                                    )
                            );

                            cbStudent5.setChecked(
                                    "Present".equals(
                                            shreyaStatus
                                    )
                            );

                            editingAttendanceId =
                                    attendanceId;

                            btnSaveAttendance.setText(
                                    "UPDATE ATTENDANCE"
                            );

                            Toast.makeText(
                                    this,
                                    "Edit attendance and press UPDATE ATTENDANCE",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load attendance",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void confirmDelete(
            String attendanceId,
            String subject,
            String date
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Attendance?"
                )
                .setMessage(
                        "Are you sure you want to delete:\n\n"
                                + subject
                                + "\n"
                                + date
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) ->
                                deleteAttendance(
                                        attendanceId
                                )
                )
                .show();
    }

    // =========================================================
    // DELETE ATTENDANCE
    // =========================================================

    private void deleteAttendance(
            String attendanceId
    ) {

        databaseReference
                .child(attendanceId)
                .removeValue()
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Attendance deleted successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            editingAttendanceId = "";

                            clearForm();

                            // Refresh records
                            calculateAttendancePercentage();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Delete failed: "
                                            + error.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // =========================================================
    // CLEAR FORM
    // =========================================================

    private void clearForm() {

        etSubject.setText("");

        cbStudent1.setChecked(false);
        cbStudent2.setChecked(false);
        cbStudent3.setChecked(false);
        cbStudent4.setChecked(false);
        cbStudent5.setChecked(false);

        editingAttendanceId = "";

        btnSaveAttendance.setText(
                "SAVE ATTENDANCE"
        );
    }

    // =========================================================
    // CALCULATE PERCENTAGE
    // =========================================================

    private double calculatePercentage(
            int present,
            int total
    ) {

        return ((double) present / total) * 100;
    }

    // =========================================================
    // FORMAT PERCENTAGE
    // =========================================================

    private String formatPercentage(
            double percentage
    ) {

        return String.format(
                Locale.getDefault(),
                "%.1f%%",
                percentage
        );
    }
}