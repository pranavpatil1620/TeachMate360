package com.example.teachmate360;

import android.app.AlertDialog;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MarksActivity extends AppCompatActivity {

    private EditText etMarksSubject;
    private EditText etExamName;

    private EditText etPranavMarks;
    private EditText etVedarthMarks;
    private EditText etPranjalMarks;
    private EditText etAbhiMarks;
    private EditText etShreyaMarks;

    private Button btnSaveMarks;
    private Button btnViewMarks;

    private TextView tvMarksReport;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String currentDate;

    // Edit ke liye
    private String editingMarksId = "";

    // Dynamic marks cards ke liye
    private LinearLayout marksContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marks);

        firebaseAuth =
                FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("marks");

        // =====================================================
        // FIND VIEWS
        // =====================================================

        etMarksSubject =
                findViewById(R.id.etMarksSubject);

        etExamName =
                findViewById(R.id.etExamName);

        etPranavMarks =
                findViewById(R.id.etPranavMarks);

        etVedarthMarks =
                findViewById(R.id.etVedarthMarks);

        etPranjalMarks =
                findViewById(R.id.etPranjalMarks);

        etAbhiMarks =
                findViewById(R.id.etAbhiMarks);

        etShreyaMarks =
                findViewById(R.id.etShreyaMarks);

        btnSaveMarks =
                findViewById(R.id.btnSaveMarks);

        btnViewMarks =
                findViewById(R.id.btnViewMarks);

        tvMarksReport =
                findViewById(R.id.tvMarksReport);

        // =====================================================
        // CREATE DYNAMIC CONTAINER
        // =====================================================

        ViewGroup parent =
                (ViewGroup) tvMarksReport.getParent();

        marksContainer =
                new LinearLayout(this);

        marksContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        marksContainer.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // Old TextView hide
        tvMarksReport.setVisibility(
                TextView.GONE
        );

        // Add dynamic container
        int reportIndex =
                parent.indexOfChild(tvMarksReport);

        parent.addView(
                marksContainer,
                reportIndex
        );

        currentDate =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                ).format(new Date());

        // SAVE / UPDATE
        btnSaveMarks.setOnClickListener(
                v -> saveMarks()
        );

        // VIEW
        btnViewMarks.setOnClickListener(
                v -> viewSavedMarks()
        );
    }

    // =========================================================
    // SAVE / UPDATE MARKS
    // =========================================================

    private void saveMarks() {

        String subject =
                etMarksSubject.getText()
                        .toString()
                        .trim();

        String examName =
                etExamName.getText()
                        .toString()
                        .trim();

        String pranav =
                etPranavMarks.getText()
                        .toString()
                        .trim();

        String vedarth =
                etVedarthMarks.getText()
                        .toString()
                        .trim();

        String pranjal =
                etPranjalMarks.getText()
                        .toString()
                        .trim();

        String abhi =
                etAbhiMarks.getText()
                        .toString()
                        .trim();

        String shreya =
                etShreyaMarks.getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (subject.isEmpty()) {

            etMarksSubject.setError(
                    "Enter subject"
            );

            etMarksSubject.requestFocus();

            return;
        }

        if (examName.isEmpty()) {

            etExamName.setError(
                    "Enter exam name"
            );

            etExamName.requestFocus();

            return;
        }

        if (pranav.isEmpty()) {

            etPranavMarks.setError(
                    "Enter marks"
            );

            etPranavMarks.requestFocus();

            return;
        }

        if (vedarth.isEmpty()) {

            etVedarthMarks.setError(
                    "Enter marks"
            );

            etVedarthMarks.requestFocus();

            return;
        }

        if (pranjal.isEmpty()) {

            etPranjalMarks.setError(
                    "Enter marks"
            );

            etPranjalMarks.requestFocus();

            return;
        }

        if (abhi.isEmpty()) {

            etAbhiMarks.setError(
                    "Enter marks"
            );

            etAbhiMarks.requestFocus();

            return;
        }

        if (shreya.isEmpty()) {

            etShreyaMarks.setError(
                    "Enter marks"
            );

            etShreyaMarks.requestFocus();

            return;
        }

        // =====================================================
        // CHECK LOGIN
        // =====================================================

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // CHECK MARKS RANGE
        // =====================================================

        double pranavValue;
        double vedarthValue;
        double pranjalValue;
        double abhiValue;
        double shreyaValue;

        try {

            pranavValue =
                    Double.parseDouble(pranav);

            vedarthValue =
                    Double.parseDouble(vedarth);

            pranjalValue =
                    Double.parseDouble(pranjal);

            abhiValue =
                    Double.parseDouble(abhi);

            shreyaValue =
                    Double.parseDouble(shreya);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Enter valid numeric marks",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (pranavValue < 0 || pranavValue > 100 ||
                vedarthValue < 0 || vedarthValue > 100 ||
                pranjalValue < 0 || pranjalValue > 100 ||
                abhiValue < 0 || abhiValue > 100 ||
                shreyaValue < 0 || shreyaValue > 100) {

            Toast.makeText(
                    this,
                    "Marks must be between 0 and 100",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // MARKS DATA
        // =====================================================

        Map<String, Object> marksData =
                new HashMap<>();

        marksData.put(
                "teacherId",
                firebaseAuth
                        .getCurrentUser()
                        .getUid()
        );

        marksData.put(
                "subject",
                subject
        );

        marksData.put(
                "examName",
                examName
        );

        marksData.put(
                "date",
                currentDate
        );

        marksData.put(
                "Pranav Jadhav",
                pranavValue
        );

        marksData.put(
                "Vedarth Dhepe",
                vedarthValue
        );

        marksData.put(
                "Pranjal Mane",
                pranjalValue
        );

        marksData.put(
                "Abhi Rathod",
                abhiValue
        );

        marksData.put(
                "Shreya Deshmukh",
                shreyaValue
        );

        // =====================================================
        // UPDATE EXISTING MARKS
        // =====================================================

        if (!editingMarksId.isEmpty()) {

            btnSaveMarks.setEnabled(false);

            btnSaveMarks.setText(
                    "UPDATING..."
            );

            databaseReference
                    .child(editingMarksId)
                    .updateChildren(marksData)
                    .addOnCompleteListener(task -> {

                        btnSaveMarks.setEnabled(true);

                        btnSaveMarks.setText(
                                "SAVE MARKS"
                        );

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    MarksActivity.this,
                                    "Marks updated successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            clearFields();

                            editingMarksId = "";

                        } else {

                            Toast.makeText(
                                    MarksActivity.this,
                                    "Failed to update marks",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

            return;
        }

        // =====================================================
        // SAVE NEW MARKS
        // =====================================================

        btnSaveMarks.setEnabled(false);

        btnSaveMarks.setText(
                "SAVING..."
        );

        String marksId =
                databaseReference
                        .push()
                        .getKey();

        if (marksId == null) {

            btnSaveMarks.setEnabled(true);

            btnSaveMarks.setText(
                    "SAVE MARKS"
            );

            return;
        }

        databaseReference
                .child(marksId)
                .setValue(marksData)
                .addOnCompleteListener(task -> {

                    btnSaveMarks.setEnabled(true);

                    btnSaveMarks.setText(
                            "SAVE MARKS"
                    );

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                MarksActivity.this,
                                "Marks saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        clearFields();

                        // IMPORTANT:
                        // Save ke baad automatically
                        // view nahi hoga.

                    } else {

                        Toast.makeText(
                                MarksActivity.this,
                                "Failed to save marks",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    // =========================================================
    // VIEW SAVED MARKS
    // =========================================================

    private void viewSavedMarks() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // IMPORTANT:
        // Old cards clear karo
        // duplicate nahi honge
        marksContainer.removeAllViews();

        btnViewMarks.setEnabled(false);

        btnViewMarks.setText(
                "LOADING..."
        );

        String teacherId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        databaseReference
                .get()
                .addOnSuccessListener(snapshot -> {

                    int count = 0;

                    for (DataSnapshot marksSnapshot :
                            snapshot.getChildren()) {

                        String savedTeacherId =
                                marksSnapshot
                                        .child("teacherId")
                                        .getValue(
                                                String.class
                                        );

                        if (!teacherId.equals(
                                savedTeacherId
                        )) {

                            continue;
                        }

                        String marksId =
                                marksSnapshot.getKey();

                        String subject =
                                marksSnapshot
                                        .child("subject")
                                        .getValue(
                                                String.class
                                        );

                        String examName =
                                marksSnapshot
                                        .child("examName")
                                        .getValue(
                                                String.class
                                        );

                        String date =
                                marksSnapshot
                                        .child("date")
                                        .getValue(
                                                String.class
                                        );

                        Double pranav =
                                getDoubleValue(
                                        marksSnapshot
                                                .child(
                                                        "Pranav Jadhav"
                                                )
                                );

                        Double vedarth =
                                getDoubleValue(
                                        marksSnapshot
                                                .child(
                                                        "Vedarth Dhepe"
                                                )
                                );

                        Double pranjal =
                                getDoubleValue(
                                        marksSnapshot
                                                .child(
                                                        "Pranjal Mane"
                                                )
                                );

                        Double abhi =
                                getDoubleValue(
                                        marksSnapshot
                                                .child(
                                                        "Abhi Rathod"
                                                )
                                );

                        Double shreya =
                                getDoubleValue(
                                        marksSnapshot
                                                .child(
                                                        "Shreya Deshmukh"
                                                )
                                );

                        if (subject == null)
                            subject = "Not specified";

                        if (examName == null)
                            examName = "Not specified";

                        if (date == null)
                            date = "Not specified";

                        count++;

                        createMarksCard(
                                marksId,
                                subject,
                                examName,
                                date,
                                pranav,
                                vedarth,
                                pranjal,
                                abhi,
                                shreya
                        );
                    }

                    btnViewMarks.setEnabled(true);

                    btnViewMarks.setText(
                            "VIEW SAVED MARKS"
                    );

                    if (count == 0) {

                        TextView noMarks =
                                new TextView(
                                        MarksActivity.this
                                );

                        noMarks.setText(
                                "📊 No saved marks found."
                        );

                        noMarks.setTextSize(
                                16
                        );

                        noMarks.setTextColor(
                                Color.rgb(
                                        23,
                                        43,
                                        77
                                )
                        );

                        noMarks.setGravity(
                                Gravity.CENTER
                        );

                        noMarks.setPadding(
                                20,
                                30,
                                20,
                                30
                        );

                        marksContainer.addView(
                                noMarks
                        );
                    }
                })
                .addOnFailureListener(error -> {

                    btnViewMarks.setEnabled(true);

                    btnViewMarks.setText(
                            "VIEW SAVED MARKS"
                    );

                    Toast.makeText(
                            MarksActivity.this,
                            "Unable to load marks",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =========================================================
    // CREATE MARKS CARD
    // =========================================================

    private void createMarksCard(
            String marksId,
            String subject,
            String examName,
            String date,
            Double pranav,
            Double vedarth,
            Double pranjal,
            Double abhi,
            Double shreya
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
                15
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
                "📊 Marks"
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
                        "📝 Exam: " + examName
                )
        );

        card.addView(
                createDetailText(
                        "📅 Date: " + date
                )
        );

        card.addView(
                createDetailText(
                        "👨‍🎓 Pranav Jadhav: "
                                + formatMarks(pranav)
                                + "/100"
                )
        );

        card.addView(
                createDetailText(
                        "👨‍🎓 Vedarth Dhepe: "
                                + formatMarks(vedarth)
                                + "/100"
                )
        );

        card.addView(
                createDetailText(
                        "👨‍🎓 Pranjal Mane: "
                                + formatMarks(pranjal)
                                + "/100"
                )
        );

        card.addView(
                createDetailText(
                        "👨‍🎓 Abhi Rathod: "
                                + formatMarks(abhi)
                                + "/100"
                )
        );

        TextView shreyaView =
                createDetailText(
                        "👩‍🎓 Shreya Deshmukh: "
                                + formatMarks(shreya)
                                + "/100"
                );

        shreyaView.setPadding(
                0,
                6,
                0,
                18
        );

        card.addView(
                shreyaView
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

        // Only Edit/Delete use rounded_button1
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
                v -> editMarks(marksId)
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
                        marksId,
                        subject,
                        examName
                )
        );

        buttonRow.addView(
                deleteButton
        );

        card.addView(
                buttonRow
        );

        // Add card
        marksContainer.addView(
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
    // EDIT MARKS
    // =========================================================

    private void editMarks(
            String marksId
    ) {

        databaseReference
                .child(marksId)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (!snapshot.exists()) {

                                Toast.makeText(
                                        this,
                                        "Marks record not found",
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

                            String examName =
                                    snapshot
                                            .child("examName")
                                            .getValue(
                                                    String.class
                                            );

                            Double pranav =
                                    getDoubleValue(
                                            snapshot.child(
                                                    "Pranav Jadhav"
                                            )
                                    );

                            Double vedarth =
                                    getDoubleValue(
                                            snapshot.child(
                                                    "Vedarth Dhepe"
                                            )
                                    );

                            Double pranjal =
                                    getDoubleValue(
                                            snapshot.child(
                                                    "Pranjal Mane"
                                            )
                                    );

                            Double abhi =
                                    getDoubleValue(
                                            snapshot.child(
                                                    "Abhi Rathod"
                                            )
                                    );

                            Double shreya =
                                    getDoubleValue(
                                            snapshot.child(
                                                    "Shreya Deshmukh"
                                            )
                                    );

                            // Put data into form
                            if (subject != null) {

                                etMarksSubject.setText(
                                        subject
                                );
                            }

                            if (examName != null) {

                                etExamName.setText(
                                        examName
                                );
                            }

                            etPranavMarks.setText(
                                    formatMarks(pranav)
                            );

                            etVedarthMarks.setText(
                                    formatMarks(vedarth)
                            );

                            etPranjalMarks.setText(
                                    formatMarks(pranjal)
                            );

                            etAbhiMarks.setText(
                                    formatMarks(abhi)
                            );

                            etShreyaMarks.setText(
                                    formatMarks(shreya)
                            );

                            editingMarksId =
                                    marksId;

                            btnSaveMarks.setText(
                                    "UPDATE MARKS"
                            );

                            Toast.makeText(
                                    this,
                                    "Edit marks and press UPDATE MARKS",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load marks",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void confirmDelete(
            String marksId,
            String subject,
            String examName
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Marks?"
                )
                .setMessage(
                        "Are you sure you want to delete:\n\n"
                                + subject
                                + " - "
                                + examName
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) ->
                                deleteMarks(
                                        marksId
                                )
                )
                .show();
    }

    // =========================================================
    // DELETE MARKS
    // =========================================================

    private void deleteMarks(
            String marksId
    ) {

        databaseReference
                .child(marksId)
                .removeValue()
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Marks deleted successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            clearFields();

                            editingMarksId = "";

                            // Refresh after delete
                            viewSavedMarks();
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
    // GET NUMBER SAFELY
    // =========================================================

    private Double getDoubleValue(
            DataSnapshot snapshot
    ) {

        Object value =
                snapshot.getValue();

        if (value instanceof Number) {

            return ((Number) value)
                    .doubleValue();
        }

        if (value != null) {

            try {

                return Double.parseDouble(
                        value.toString()
                );

            } catch (Exception ignored) {
            }
        }

        return 0.0;
    }

    // =========================================================
    // FORMAT MARKS
    // =========================================================

    private String formatMarks(
            Double marks
    ) {

        if (marks == null) {
            return "0";
        }

        if (marks % 1 == 0) {

            return String.valueOf(
                    marks.intValue()
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f",
                marks
        );
    }

    // =========================================================
    // CLEAR FIELDS
    // =========================================================

    private void clearFields() {

        etMarksSubject.setText("");

        etExamName.setText("");

        etPranavMarks.setText("");

        etVedarthMarks.setText("");

        etPranjalMarks.setText("");

        etAbhiMarks.setText("");

        etShreyaMarks.setText("");

        editingMarksId = "";

        btnSaveMarks.setText(
                "SAVE MARKS"
        );
    }
}