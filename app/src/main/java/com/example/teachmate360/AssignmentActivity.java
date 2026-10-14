package com.example.teachmate360;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AssignmentActivity extends AppCompatActivity {

    private EditText etAssignmentSubject;
    private EditText etAssignmentTitle;
    private EditText etAssignmentDescription;

    private TextView tvDueDate;

    private Button btnSaveAssignment;
    private Button btnViewAssignments;

    private LinearLayout assignmentContainer;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference assignmentsReference;

    private String editingAssignmentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assignment);

        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        assignmentsReference =
                FirebaseDatabase.getInstance()
                        .getReference("assignments");

        // =====================================================
        // FIND VIEWS
        // =====================================================

        etAssignmentSubject =
                findViewById(R.id.etAssignmentSubject);

        etAssignmentTitle =
                findViewById(R.id.etAssignmentTitle);

        etAssignmentDescription =
                findViewById(R.id.etAssignmentDescription);

        tvDueDate =
                findViewById(R.id.tvDueDate);

        btnSaveAssignment =
                findViewById(R.id.btnSaveAssignment);

        btnViewAssignments =
                findViewById(R.id.btnViewAssignments);

        assignmentContainer =
                findViewById(R.id.assignmentContainer);

        // =====================================================
        // DATE PICKER
        // =====================================================

        tvDueDate.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            AssignmentActivity.this,
                            (view, year, month, day) -> {

                                Calendar selected =
                                        Calendar.getInstance();

                                selected.set(
                                        year,
                                        month,
                                        day
                                );

                                SimpleDateFormat format =
                                        new SimpleDateFormat(
                                                "dd/MM/yyyy",
                                                Locale.getDefault()
                                        );

                                tvDueDate.setText(
                                        "📅 "
                                                + format.format(
                                                selected.getTime()
                                        )
                                );
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            dialog.show();
        });

        // =====================================================
        // SAVE / UPDATE
        // =====================================================

        btnSaveAssignment.setOnClickListener(
                v -> saveAssignment()
        );

        // =====================================================
        // VIEW
        // =====================================================

        btnViewAssignments.setOnClickListener(
                v -> loadAssignments()
        );
    }

    // =========================================================
    // SAVE / UPDATE ASSIGNMENT
    // =========================================================

    private void saveAssignment() {

        String subject =
                etAssignmentSubject
                        .getText()
                        .toString()
                        .trim();

        String title =
                etAssignmentTitle
                        .getText()
                        .toString()
                        .trim();

        String description =
                etAssignmentDescription
                        .getText()
                        .toString()
                        .trim();

        String dueDate =
                tvDueDate
                        .getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (subject.isEmpty()) {

            etAssignmentSubject.setError(
                    "Enter subject"
            );

            etAssignmentSubject.requestFocus();

            return;
        }

        if (title.isEmpty()) {

            etAssignmentTitle.setError(
                    "Enter assignment title"
            );

            etAssignmentTitle.requestFocus();

            return;
        }

        if (description.isEmpty()) {

            etAssignmentDescription.setError(
                    "Enter description"
            );

            etAssignmentDescription.requestFocus();

            return;
        }

        if (dueDate.isEmpty()
                || dueDate.equals(
                "📅 Select Due Date"
        )) {

            Toast.makeText(
                    this,
                    "Select due date",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // TEACHER ID
        // =====================================================

        String teacherId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        // =====================================================
        // ASSIGNMENT DATA
        // =====================================================

        HashMap<String, Object> assignment =
                new HashMap<>();

        assignment.put(
                "teacherId",
                teacherId
        );

        assignment.put(
                "subject",
                subject
        );

        assignment.put(
                "title",
                title
        );

        assignment.put(
                "description",
                description
        );

        assignment.put(
                "dueDate",
                dueDate
        );

        // =====================================================
        // UPDATE EXISTING ASSIGNMENT
        // =====================================================

        if (!editingAssignmentId.isEmpty()) {

            btnSaveAssignment.setEnabled(false);

            btnSaveAssignment.setText(
                    "UPDATING..."
            );

            assignmentsReference
                    .child(editingAssignmentId)
                    .updateChildren(assignment)
                    .addOnSuccessListener(unused -> {

                        btnSaveAssignment.setEnabled(
                                true
                        );

                        btnSaveAssignment.setText(
                                "SAVE ASSIGNMENT"
                        );

                        Toast.makeText(
                                this,
                                "Assignment updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        clearForm();

                        // Update ke baad list refresh
                        loadAssignments();
                    })
                    .addOnFailureListener(error -> {

                        btnSaveAssignment.setEnabled(
                                true
                        );

                        btnSaveAssignment.setText(
                                "UPDATE ASSIGNMENT"
                        );

                        Toast.makeText(
                                this,
                                "Update failed: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

            return;
        }

        // =====================================================
        // CREATE NEW ASSIGNMENT
        // =====================================================

        btnSaveAssignment.setEnabled(false);

        btnSaveAssignment.setText(
                "SAVING..."
        );

        String assignmentId =
                assignmentsReference
                        .push()
                        .getKey();

        if (assignmentId == null) {

            btnSaveAssignment.setEnabled(true);

            btnSaveAssignment.setText(
                    "SAVE ASSIGNMENT"
            );

            Toast.makeText(
                    this,
                    "Unable to create assignment",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        assignment.put(
                "createdDate",
                System.currentTimeMillis()
        );

        assignmentsReference
                .child(assignmentId)
                .setValue(assignment)
                .addOnSuccessListener(unused -> {

                    btnSaveAssignment.setEnabled(
                            true
                    );

                    btnSaveAssignment.setText(
                            "SAVE ASSIGNMENT"
                    );

                    Toast.makeText(
                            this,
                            "Assignment saved successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    clearForm();

                    // IMPORTANT:
                    // Assignment automatically view nahi hoga.
                    // VIEW ASSIGNMENTS button dabane par hi dikhega.
                })
                .addOnFailureListener(error -> {

                    btnSaveAssignment.setEnabled(
                            true
                    );

                    btnSaveAssignment.setText(
                            "SAVE ASSIGNMENT"
                    );

                    Toast.makeText(
                            this,
                            "Save failed: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // LOAD ASSIGNMENTS
    // =========================================================

    private void loadAssignments() {

        // Old cards clear
        assignmentContainer.removeAllViews();

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String teacherId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        btnViewAssignments.setEnabled(
                false
        );

        btnViewAssignments.setText(
                "LOADING..."
        );

        assignmentsReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    int count = 0;

                    // =================================================
                    // READ ASSIGNMENTS
                    // =================================================

                    for (DataSnapshot assignment :
                            snapshot.getChildren()) {

                        String savedTeacherId =
                                assignment
                                        .child("teacherId")
                                        .getValue(
                                                String.class
                                        );

                        // Only current teacher's assignments
                        if (!teacherId.equals(
                                savedTeacherId
                        )) {

                            continue;
                        }

                        String assignmentId =
                                assignment.getKey();

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

                        createAssignmentCard(
                                assignmentId,
                                subject,
                                title,
                                description,
                                dueDate
                        );
                    }

                    btnViewAssignments.setEnabled(
                            true
                    );

                    btnViewAssignments.setText(
                            "VIEW ASSIGNMENTS"
                    );

                    // =================================================
                    // NO ASSIGNMENTS
                    // =================================================

                    if (count == 0) {

                        TextView noAssignment =
                                new TextView(this);

                        noAssignment.setText(
                                "No assignments available."
                        );

                        noAssignment.setTextSize(
                                16
                        );

                        noAssignment.setTextColor(
                                Color.rgb(
                                        23,
                                        43,
                                        77
                                )
                        );

                        noAssignment.setGravity(
                                Gravity.CENTER
                        );

                        noAssignment.setPadding(
                                20,
                                30,
                                20,
                                30
                        );

                        assignmentContainer.addView(
                                noAssignment
                        );
                    }
                })
                .addOnFailureListener(error -> {

                    btnViewAssignments.setEnabled(
                            true
                    );

                    btnViewAssignments.setText(
                            "VIEW ASSIGNMENTS"
                    );

                    Toast.makeText(
                            this,
                            "Failed to load assignments: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // CREATE ASSIGNMENT CARD
    // =========================================================

    private void createAssignmentCard(
            String assignmentId,
            String subject,
            String title,
            String description,
            String dueDate
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
                "📋 Assignment"
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
                        "📚 Subject: "
                                + subject
                )
        );

        card.addView(
                createDetailText(
                        "📝 Title: "
                                + title
                )
        );

        card.addView(
                createDetailText(
                        "📄 Description: "
                                + description
                )
        );

        TextView due =
                createDetailText(
                        "📅 Due Date: "
                                + dueDate
                );

        due.setPadding(
                0,
                8,
                0,
                18
        );

        card.addView(
                due
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

        // Edit/Delete ke liye rounded_button1
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
                v -> editAssignment(
                        assignmentId
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

        // Edit/Delete ke liye rounded_button1
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
                        assignmentId,
                        title
                )
        );

        buttonRow.addView(
                deleteButton
        );

        card.addView(
                buttonRow
        );

        assignmentContainer.addView(
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
    // EDIT ASSIGNMENT
    // =========================================================

    private void editAssignment(
            String assignmentId
    ) {

        assignmentsReference
                .child(assignmentId)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (!snapshot.exists()) {

                                Toast.makeText(
                                        this,
                                        "Assignment not found",
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

                            String title =
                                    snapshot
                                            .child("title")
                                            .getValue(
                                                    String.class
                                            );

                            String description =
                                    snapshot
                                            .child("description")
                                            .getValue(
                                                    String.class
                                            );

                            String dueDate =
                                    snapshot
                                            .child("dueDate")
                                            .getValue(
                                                    String.class
                                            );

                            if (subject != null) {

                                etAssignmentSubject
                                        .setText(
                                                subject
                                        );
                            }

                            if (title != null) {

                                etAssignmentTitle
                                        .setText(
                                                title
                                        );
                            }

                            if (description != null) {

                                etAssignmentDescription
                                        .setText(
                                                description
                                        );
                            }

                            if (dueDate != null) {

                                tvDueDate.setText(
                                        dueDate
                                );
                            }

                            editingAssignmentId =
                                    assignmentId;

                            btnSaveAssignment.setText(
                                    "UPDATE ASSIGNMENT"
                            );

                            Toast.makeText(
                                    this,
                                    "Edit details and press UPDATE ASSIGNMENT",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load assignment",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void confirmDelete(
            String assignmentId,
            String title
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Assignment?"
                )
                .setMessage(
                        "Are you sure you want to delete:\n\n"
                                + title
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) ->
                                deleteAssignment(
                                        assignmentId
                                )
                )
                .show();
    }

    // =========================================================
    // DELETE ASSIGNMENT
    // =========================================================

    private void deleteAssignment(
            String assignmentId
    ) {

        assignmentsReference
                .child(assignmentId)
                .removeValue()
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Assignment deleted successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            clearForm();

                            // Delete ke baad list refresh
                            loadAssignments();
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

        etAssignmentSubject.setText("");

        etAssignmentTitle.setText("");

        etAssignmentDescription.setText("");

        tvDueDate.setText(
                "📅 Select Due Date"
        );

        editingAssignmentId = "";

        btnSaveAssignment.setText(
                "SAVE ASSIGNMENT"
        );
    }
}