package com.example.teachmate360;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TeacherRegisterActivity extends AppCompatActivity {

    private EditText etTeacherName;
    private EditText etTeacherRegisterEmail;
    private EditText etTeacherDepartment;
    private EditText etTeacherMobile;
    private EditText etTeacherRegisterPassword;
    private EditText etTeacherConfirmPassword;

    private Button btnTeacherRegister;
    private TextView tvTeacherLogin;

    private ScrollView scrollViewTeacher;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keyboard / Insets handling
        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                false
        );

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        setContentView(R.layout.activity_teacher_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("teachers");

        // Find views
        scrollViewTeacher =
                findViewById(R.id.scrollViewTeacher);

        etTeacherName =
                findViewById(R.id.etTeacherName);

        etTeacherRegisterEmail =
                findViewById(R.id.etTeacherRegisterEmail);

        etTeacherDepartment =
                findViewById(R.id.etTeacherDepartment);

        etTeacherMobile =
                findViewById(R.id.etTeacherMobile);

        etTeacherRegisterPassword =
                findViewById(R.id.etTeacherRegisterPassword);

        etTeacherConfirmPassword =
                findViewById(R.id.etTeacherConfirmPassword);

        btnTeacherRegister =
                findViewById(R.id.btnTeacherRegister);

        tvTeacherLogin =
                findViewById(R.id.tvTeacherLogin);

        // KEYBOARD HANDLING
        ViewCompat.setOnApplyWindowInsetsListener(
                scrollViewTeacher,
                (view, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    Insets keyboard =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.ime()
                            );

                    int bottomSpace =
                            Math.max(
                                    systemBars.bottom,
                                    keyboard.bottom
                            );

                    view.setPadding(
                            view.getPaddingLeft(),
                            systemBars.top,
                            view.getPaddingRight(),
                            bottomSpace + 60
                    );

                    return insets;
                }
        );

        // AUTO SCROLL FOR EACH FIELD
        setupAutoScroll(etTeacherName);
        setupAutoScroll(etTeacherRegisterEmail);
        setupAutoScroll(etTeacherDepartment);
        setupAutoScroll(etTeacherMobile);
        setupAutoScroll(etTeacherRegisterPassword);
        setupAutoScroll(etTeacherConfirmPassword);

        // CREATE ACCOUNT
        btnTeacherRegister.setOnClickListener(v -> {

            String name =
                    etTeacherName.getText()
                            .toString()
                            .trim();

            String email =
                    etTeacherRegisterEmail.getText()
                            .toString()
                            .trim();

            String department =
                    etTeacherDepartment.getText()
                            .toString()
                            .trim();

            String mobile =
                    etTeacherMobile.getText()
                            .toString()
                            .trim();

            String password =
                    etTeacherRegisterPassword.getText()
                            .toString();

            String confirmPassword =
                    etTeacherConfirmPassword.getText()
                            .toString();

            // VALIDATION

            if (name.isEmpty()) {
                etTeacherName.setError("Enter your name");
                etTeacherName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etTeacherRegisterEmail.setError("Enter email");
                etTeacherRegisterEmail.requestFocus();
                return;
            }

            if (department.isEmpty()) {
                etTeacherDepartment.setError("Enter department");
                etTeacherDepartment.requestFocus();
                return;
            }

            if (mobile.isEmpty()) {
                etTeacherMobile.setError("Enter mobile number");
                etTeacherMobile.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etTeacherRegisterPassword.setError("Enter password");
                etTeacherRegisterPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                etTeacherRegisterPassword.setError(
                        "Password must be at least 6 characters"
                );
                etTeacherRegisterPassword.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                etTeacherConfirmPassword.setError(
                        "Confirm your password"
                );
                etTeacherConfirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etTeacherConfirmPassword.setError(
                        "Passwords do not match"
                );
                etTeacherConfirmPassword.requestFocus();
                return;
            }

            // Disable button while registering
            btnTeacherRegister.setEnabled(false);
            btnTeacherRegister.setText("CREATING ACCOUNT...");

            // FIREBASE AUTHENTICATION
            firebaseAuth
                    .createUserWithEmailAndPassword(
                            email,
                            password
                    )
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            // Get Firebase User ID
                            String uid =
                                    firebaseAuth
                                            .getCurrentUser()
                                            .getUid();

                            // Teacher data
                            HashMap<String, Object> teacherData =
                                    new HashMap<>();

                            teacherData.put("name", name);
                            teacherData.put("email", email);
                            teacherData.put("department", department);
                            teacherData.put("mobile", mobile);
                            teacherData.put("role", "Teacher");

                            // SAVE DATA TO REALTIME DATABASE
                            databaseReference
                                    .child(uid)
                                    .setValue(teacherData)
                                    .addOnCompleteListener(
                                            databaseTask -> {

                                                if (databaseTask.isSuccessful()) {

                                                    Toast.makeText(
                                                            TeacherRegisterActivity.this,
                                                            "Teacher account created successfully!",
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    // Go back to login
                                                    finish();

                                                } else {

                                                    btnTeacherRegister
                                                            .setEnabled(true);

                                                    btnTeacherRegister
                                                            .setText("CREATE ACCOUNT");

                                                    Toast.makeText(
                                                            TeacherRegisterActivity.this,
                                                            "Account created but profile could not be saved.",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            });

                        } else {

                            btnTeacherRegister
                                    .setEnabled(true);

                            btnTeacherRegister
                                    .setText("CREATE ACCOUNT");

                            String errorMessage = "Registration failed";

                            if (task.getException() != null) {
                                errorMessage =
                                        task.getException()
                                                .getMessage();
                            }

                            Toast.makeText(
                                    TeacherRegisterActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // LOGIN
        tvTeacherLogin.setOnClickListener(v -> finish());
    }

    // CURRENT WORKING KEYBOARD METHOD
    private void setupAutoScroll(EditText editText) {

        editText.setOnFocusChangeListener(
                (view, hasFocus) -> {

                    if (!hasFocus) {
                        return;
                    }

                    scrollViewTeacher.postDelayed(() -> {

                        // Screen ka visible area
                        Rect visibleRect = new Rect();

                        getWindow()
                                .getDecorView()
                                .getWindowVisibleDisplayFrame(
                                        visibleRect
                                );

                        // EditText ki screen position
                        int[] location = new int[2];

                        editText.getLocationOnScreen(
                                location
                        );

                        int fieldTop = location[1];

                        int fieldBottom =
                                fieldTop +
                                        editText.getHeight();

                        // Keyboard ke upar thoda gap
                        int safeBottom =
                                visibleRect.bottom - 80;

                        // Agar field keyboard ke neeche chali gayi hai
                        if (fieldBottom > safeBottom) {

                            int scrollAmount =
                                    fieldBottom -
                                            safeBottom;

                            scrollViewTeacher.smoothScrollBy(
                                    0,
                                    scrollAmount
                            );
                        }

                        // Agar field bahut upar chali gayi ho
                        else if (fieldTop <
                                visibleRect.top + 50) {

                            int scrollAmount =
                                    fieldTop -
                                            visibleRect.top -
                                            50;

                            scrollViewTeacher.smoothScrollBy(
                                    0,
                                    scrollAmount
                            );
                        }

                    }, 400);
                });
    }
}