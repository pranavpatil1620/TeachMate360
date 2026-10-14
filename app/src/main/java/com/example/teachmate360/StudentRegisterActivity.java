package com.example.teachmate360;

import android.graphics.Rect;
import android.os.Bundle;
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

public class StudentRegisterActivity extends AppCompatActivity {

    private EditText etStudentName;
    private EditText etStudentEmail;
    private EditText etStudentRollNo;
    private EditText etStudentClass;
    private EditText etStudentPassword;
    private EditText etStudentConfirmPassword;

    private Button btnStudentRegister;
    private TextView tvStudentLogin;

    private ScrollView scrollViewStudent;

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

        setContentView(R.layout.activity_student_register);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        // Find views
        scrollViewStudent =
                findViewById(R.id.scrollViewStudent);

        etStudentName =
                findViewById(R.id.etStudentName);

        etStudentEmail =
                findViewById(R.id.etStudentEmail);

        etStudentRollNo =
                findViewById(R.id.etStudentRollNo);

        etStudentClass =
                findViewById(R.id.etStudentClass);

        etStudentPassword =
                findViewById(R.id.etStudentPassword);

        etStudentConfirmPassword =
                findViewById(R.id.etStudentConfirmPassword);

        btnStudentRegister =
                findViewById(R.id.btnStudentRegister);

        tvStudentLogin =
                findViewById(R.id.tvStudentLogin);


        // =========================================
        // KEYBOARD / INSETS HANDLING
        // =========================================

        ViewCompat.setOnApplyWindowInsetsListener(
                scrollViewStudent,
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


        // =========================================
        // AUTO SCROLL FOR EACH FIELD
        // =========================================

        setupAutoScroll(etStudentName);

        setupAutoScroll(etStudentEmail);

        setupAutoScroll(etStudentRollNo);

        setupAutoScroll(etStudentClass);

        setupAutoScroll(etStudentPassword);

        setupAutoScroll(etStudentConfirmPassword);


        // =========================================
        // STUDENT REGISTER
        // =========================================

        btnStudentRegister.setOnClickListener(v -> {

            String name =
                    etStudentName.getText()
                            .toString()
                            .trim();

            String email =
                    etStudentEmail.getText()
                            .toString()
                            .trim();

            String rollNo =
                    etStudentRollNo.getText()
                            .toString()
                            .trim();

            String studentClass =
                    etStudentClass.getText()
                            .toString()
                            .trim();

            String password =
                    etStudentPassword.getText()
                            .toString();

            String confirmPassword =
                    etStudentConfirmPassword.getText()
                            .toString();


            // =========================================
            // VALIDATION
            // =========================================

            if (name.isEmpty()) {

                etStudentName.setError(
                        "Enter your name"
                );

                etStudentName.requestFocus();

                return;
            }


            if (email.isEmpty()) {

                etStudentEmail.setError(
                        "Enter email"
                );

                etStudentEmail.requestFocus();

                return;
            }


            if (rollNo.isEmpty()) {

                etStudentRollNo.setError(
                        "Enter roll number"
                );

                etStudentRollNo.requestFocus();

                return;
            }


            if (studentClass.isEmpty()) {

                etStudentClass.setError(
                        "Enter class / division"
                );

                etStudentClass.requestFocus();

                return;
            }


            if (password.isEmpty()) {

                etStudentPassword.setError(
                        "Enter password"
                );

                etStudentPassword.requestFocus();

                return;
            }


            if (password.length() < 6) {

                etStudentPassword.setError(
                        "Password must be at least 6 characters"
                );

                etStudentPassword.requestFocus();

                return;
            }


            if (confirmPassword.isEmpty()) {

                etStudentConfirmPassword.setError(
                        "Confirm your password"
                );

                etStudentConfirmPassword.requestFocus();

                return;
            }


            if (!password.equals(confirmPassword)) {

                etStudentConfirmPassword.setError(
                        "Passwords do not match"
                );

                etStudentConfirmPassword.requestFocus();

                return;
            }


            // =========================================
            // DISABLE BUTTON
            // =========================================

            btnStudentRegister.setEnabled(false);

            btnStudentRegister.setText(
                    "CREATING ACCOUNT..."
            );


            // =========================================
            // FIREBASE AUTHENTICATION
            // =========================================

            firebaseAuth
                    .createUserWithEmailAndPassword(
                            email,
                            password
                    )
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {


                            // =========================================
                            // GET USER ID
                            // =========================================

                            String uid =
                                    firebaseAuth
                                            .getCurrentUser()
                                            .getUid();


                            // =========================================
                            // STUDENT DATA
                            // =========================================

                            HashMap<String, Object>
                                    studentData =
                                    new HashMap<>();

                            studentData.put(
                                    "name",
                                    name
                            );

                            studentData.put(
                                    "email",
                                    email
                            );

                            studentData.put(
                                    "rollNo",
                                    rollNo
                            );

                            studentData.put(
                                    "studentClass",
                                    studentClass
                            );

                            studentData.put(
                                    "role",
                                    "Student"
                            );


                            // =========================================
                            // SAVE TO REALTIME DATABASE
                            // =========================================

                            databaseReference
                                    .child(uid)
                                    .setValue(studentData)
                                    .addOnCompleteListener(
                                            databaseTask -> {

                                                if (databaseTask.isSuccessful()) {

                                                    Toast.makeText(
                                                            StudentRegisterActivity.this,
                                                            "Student account created successfully!",
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    // Go back to Login
                                                    finish();

                                                } else {

                                                    btnStudentRegister
                                                            .setEnabled(true);

                                                    btnStudentRegister
                                                            .setText(
                                                                    "REGISTER"
                                                            );

                                                    Toast.makeText(
                                                            StudentRegisterActivity.this,
                                                            "Account created but profile could not be saved.",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            }
                                    );

                        } else {


                            // =========================================
                            // AUTHENTICATION FAILED
                            // =========================================

                            btnStudentRegister
                                    .setEnabled(true);

                            btnStudentRegister
                                    .setText(
                                            "REGISTER"
                                    );


                            String errorMessage =
                                    "Registration failed";


                            if (task.getException() != null) {

                                errorMessage =
                                        task.getException()
                                                .getMessage();
                            }


                            Toast.makeText(
                                    StudentRegisterActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // =========================================
        // LOGIN
        // =========================================

        tvStudentLogin.setOnClickListener(
                v -> finish()
        );
    }


    // =========================================
    // WORKING KEYBOARD AUTO-SCROLL METHOD
    // SAME AS TEACHER REGISTRATION
    // =========================================

    private void setupAutoScroll(EditText editText) {

        editText.setOnFocusChangeListener(
                (view, hasFocus) -> {

                    if (!hasFocus) {
                        return;
                    }


                    scrollViewStudent.postDelayed(
                            () -> {

                                // Screen visible area
                                Rect visibleRect =
                                        new Rect();


                                getWindow()
                                        .getDecorView()
                                        .getWindowVisibleDisplayFrame(
                                                visibleRect
                                        );


                                // EditText screen position
                                int[] location =
                                        new int[2];


                                editText.getLocationOnScreen(
                                        location
                                );


                                int fieldTop =
                                        location[1];


                                int fieldBottom =
                                        fieldTop +
                                                editText.getHeight();


                                // Keyboard ke upar gap
                                int safeBottom =
                                        visibleRect.bottom -
                                                80;


                                // Field keyboard ke neeche chali gayi
                                if (fieldBottom >
                                        safeBottom) {

                                    int scrollAmount =
                                            fieldBottom -
                                                    safeBottom;


                                    scrollViewStudent
                                            .smoothScrollBy(
                                                    0,
                                                    scrollAmount
                                            );
                                }


                                // Field bahut upar chali gayi
                                else if (fieldTop <
                                        visibleRect.top + 50) {

                                    int scrollAmount =
                                            fieldTop -
                                                    visibleRect.top -
                                                    50;


                                    scrollViewStudent
                                            .smoothScrollBy(
                                                    0,
                                                    scrollAmount
                                            );
                                }

                            },
                            400
                    );
                }
        );
    }
}