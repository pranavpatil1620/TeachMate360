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

public class NoticesActivity extends AppCompatActivity {

    private EditText etNoticeTitle;
    private EditText etNoticeMessage;

    private Button btnSaveNotice;
    private Button btnViewNotices;

    private TextView tvNoticeReport;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String currentDate;

    // Edit ke liye
    private String editingNoticeId = "";

    // Dynamic notice container
    private LinearLayout noticeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notices);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("notices");

        // Find Views
        etNoticeTitle =
                findViewById(R.id.etNoticeTitle);

        etNoticeMessage =
                findViewById(R.id.etNoticeMessage);

        btnSaveNotice =
                findViewById(R.id.btnSaveNotice);

        btnViewNotices =
                findViewById(R.id.btnViewNotices);

        tvNoticeReport =
                findViewById(R.id.tvNoticeReport);

        // =====================================================
        // CREATE NOTICE CONTAINER
        // =====================================================

        ViewGroup parent =
                (ViewGroup) tvNoticeReport.getParent();

        noticeContainer =
                new LinearLayout(this);

        noticeContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        noticeContainer.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // Hide old TextView
        tvNoticeReport.setVisibility(
                TextView.GONE
        );

        // Add new container before old TextView
        int reportIndex =
                parent.indexOfChild(tvNoticeReport);

        parent.addView(
                noticeContainer,
                reportIndex
        );

        // Current date
        currentDate =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                ).format(new Date());

        // SAVE / UPDATE
        btnSaveNotice.setOnClickListener(
                v -> saveNotice()
        );

        // VIEW
        btnViewNotices.setOnClickListener(
                v -> viewNotices()
        );
    }

    // =========================================================
    // SAVE / UPDATE NOTICE
    // =========================================================

    private void saveNotice() {

        String title =
                etNoticeTitle.getText()
                        .toString()
                        .trim();

        String message =
                etNoticeMessage.getText()
                        .toString()
                        .trim();

        // Validation
        if (title.isEmpty()) {

            etNoticeTitle.setError(
                    "Enter notice title"
            );

            etNoticeTitle.requestFocus();

            return;
        }

        if (message.isEmpty()) {

            etNoticeMessage.setError(
                    "Enter notice message"
            );

            etNoticeMessage.requestFocus();

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

        btnSaveNotice.setEnabled(false);

        // =====================================================
        // UPDATE EXISTING NOTICE
        // =====================================================

        if (!editingNoticeId.isEmpty()) {

            Map<String, Object> updateData =
                    new HashMap<>();

            updateData.put(
                    "title",
                    title
            );

            updateData.put(
                    "message",
                    message
            );

            updateData.put(
                    "date",
                    currentDate
            );

            btnSaveNotice.setText(
                    "UPDATING..."
            );

            databaseReference
                    .child(editingNoticeId)
                    .updateChildren(updateData)
                    .addOnCompleteListener(
                            task -> {

                                btnSaveNotice.setEnabled(true);

                                btnSaveNotice.setText(
                                        "SAVE NOTICE"
                                );

                                if (task.isSuccessful()) {

                                    Toast.makeText(
                                            NoticesActivity.this,
                                            "Notice updated successfully!",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    clearFields();

                                    editingNoticeId = "";

                                } else {

                                    Toast.makeText(
                                            NoticesActivity.this,
                                            "Failed to update notice",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );

            return;
        }

        // =====================================================
        // SAVE NEW NOTICE
        // =====================================================

        btnSaveNotice.setText(
                "SAVING..."
        );

        String noticeId =
                databaseReference
                        .push()
                        .getKey();

        if (noticeId == null) {

            resetSaveButton();

            return;
        }

        Map<String, Object> noticeData =
                new HashMap<>();

        noticeData.put(
                "teacherId",
                firebaseAuth
                        .getCurrentUser()
                        .getUid()
        );

        noticeData.put(
                "title",
                title
        );

        noticeData.put(
                "message",
                message
        );

        noticeData.put(
                "date",
                currentDate
        );

        databaseReference
                .child(noticeId)
                .setValue(noticeData)
                .addOnCompleteListener(
                        task -> {

                            resetSaveButton();

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        NoticesActivity.this,
                                        "Notice saved successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                clearFields();

                                // IMPORTANT:
                                // Yaha viewNotices() nahi hai.
                                // SAVE karne par automatically display nahi hoga.

                            } else {

                                Toast.makeText(
                                        NoticesActivity.this,
                                        "Failed to save notice",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    // =========================================================
    // VIEW NOTICES
    // =========================================================

    private void viewNotices() {

        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Teacher is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // IMPORTANT:
        // Pehle purane cards remove karo
        // taaki duplicate na ho
        noticeContainer.removeAllViews();

        btnViewNotices.setEnabled(false);

        btnViewNotices.setText(
                "LOADING..."
        );

        String teacherId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        databaseReference
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            int count = 0;

                            for (DataSnapshot noticeSnapshot :
                                    snapshot.getChildren()) {

                                String savedTeacherId =
                                        noticeSnapshot
                                                .child("teacherId")
                                                .getValue(
                                                        String.class
                                                );

                                if (!teacherId.equals(
                                        savedTeacherId
                                )) {

                                    continue;
                                }

                                String noticeId =
                                        noticeSnapshot.getKey();

                                String title =
                                        noticeSnapshot
                                                .child("title")
                                                .getValue(
                                                        String.class
                                                );

                                String message =
                                        noticeSnapshot
                                                .child("message")
                                                .getValue(
                                                        String.class
                                                );

                                String date =
                                        noticeSnapshot
                                                .child("date")
                                                .getValue(
                                                        String.class
                                                );

                                if (title == null)
                                    title = "No title";

                                if (message == null)
                                    message = "No message";

                                if (date == null)
                                    date = "No date";

                                count++;

                                createNoticeCard(
                                        noticeId,
                                        title,
                                        message,
                                        date
                                );
                            }

                            btnViewNotices.setEnabled(true);

                            btnViewNotices.setText(
                                    "VIEW SAVED NOTICES"
                            );

                            if (count == 0) {

                                TextView noNotice =
                                        new TextView(
                                                NoticesActivity.this
                                        );

                                noNotice.setText(
                                        "📢 No saved notices found."
                                );

                                noNotice.setTextSize(
                                        16
                                );

                                noNotice.setTextColor(
                                        Color.rgb(
                                                23,
                                                43,
                                                77
                                        )
                                );

                                noNotice.setGravity(
                                        Gravity.CENTER
                                );

                                noNotice.setPadding(
                                        20,
                                        30,
                                        20,
                                        30
                                );

                                noticeContainer.addView(
                                        noNotice
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            btnViewNotices.setEnabled(true);

                            btnViewNotices.setText(
                                    "VIEW SAVED NOTICES"
                            );

                            Toast.makeText(
                                    NoticesActivity.this,
                                    "Unable to load notices",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // CREATE NOTICE CARD
    // =========================================================

    private void createNoticeCard(
            String noticeId,
            String title,
            String message,
            String date
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
        // TITLE
        // =====================================================

        TextView titleView =
                new TextView(this);

        titleView.setText(
                "📢 " + title
        );

        titleView.setTextSize(
                19
        );

        titleView.setTypeface(
                null,
                Typeface.BOLD
        );

        titleView.setTextColor(
                Color.rgb(
                        23,
                        43,
                        77
                )
        );

        titleView.setPadding(
                0,
                0,
                0,
                10
        );

        card.addView(
                titleView
        );

        // =====================================================
        // DATE
        // =====================================================

        TextView dateView =
                new TextView(this);

        dateView.setText(
                "📅 Date: " + date
        );

        dateView.setTextSize(
                14
        );

        dateView.setTextColor(
                Color.rgb(
                        102,
                        112,
                        133
                )
        );

        dateView.setPadding(
                0,
                0,
                0,
                10
        );

        card.addView(
                dateView
        );

        // =====================================================
        // MESSAGE
        // =====================================================

        TextView messageView =
                new TextView(this);

        messageView.setText(
                message
        );

        messageView.setTextSize(
                15
        );

        messageView.setTextColor(
                Color.rgb(
                        23,
                        43,
                        77
                )
        );

        messageView.setPadding(
                0,
                0,
                0,
                18
        );

        card.addView(
                messageView
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
                v -> editNotice(
                        noticeId
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
                        noticeId,
                        title
                )
        );

        buttonRow.addView(
                deleteButton
        );

        card.addView(
                buttonRow
        );

        // Add card to dedicated container
        noticeContainer.addView(
                card
        );
    }

    // =========================================================
    // EDIT NOTICE
    // =========================================================

    private void editNotice(
            String noticeId
    ) {

        databaseReference
                .child(noticeId)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (!snapshot.exists()) {

                                Toast.makeText(
                                        this,
                                        "Notice not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String title =
                                    snapshot
                                            .child("title")
                                            .getValue(
                                                    String.class
                                            );

                            String message =
                                    snapshot
                                            .child("message")
                                            .getValue(
                                                    String.class
                                            );

                            if (title != null) {

                                etNoticeTitle.setText(
                                        title
                                );
                            }

                            if (message != null) {

                                etNoticeMessage.setText(
                                        message
                                );
                            }

                            editingNoticeId =
                                    noticeId;

                            btnSaveNotice.setText(
                                    "UPDATE NOTICE"
                            );

                            Toast.makeText(
                                    this,
                                    "Edit notice and press UPDATE NOTICE",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    this,
                                    "Unable to load notice",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void confirmDelete(
            String noticeId,
            String title
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Notice?"
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
                                deleteNotice(
                                        noticeId
                                )
                )
                .show();
    }

    // =========================================================
    // DELETE NOTICE
    // =========================================================

    private void deleteNotice(
            String noticeId
    ) {

        databaseReference
                .child(noticeId)
                .removeValue()
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Notice deleted successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            clearFields();

                            editingNoticeId = "";

                            // Refresh only after delete
                            viewNotices();
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
    // RESET SAVE BUTTON
    // =========================================================

    private void resetSaveButton() {

        btnSaveNotice.setEnabled(
                true
        );

        btnSaveNotice.setText(
                "SAVE NOTICE"
        );
    }

    // =========================================================
    // CLEAR FIELDS
    // =========================================================

    private void clearFields() {

        etNoticeTitle.setText("");

        etNoticeMessage.setText("");

        editingNoticeId = "";

        btnSaveNotice.setText(
                "SAVE NOTICE"
        );
    }
}