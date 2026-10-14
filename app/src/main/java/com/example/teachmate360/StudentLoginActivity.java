package com.example.teachmate360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText etStudentEmail;
    private EditText etStudentPassword;

    private Button btnStudentLogin;
    private TextView tvStudentRegister;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference studentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_login);

        firebaseAuth = FirebaseAuth.getInstance();

        studentReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        etStudentEmail =
                findViewById(R.id.etStudentEmail);

        etStudentPassword =
                findViewById(R.id.etStudentPassword);

        btnStudentLogin =
                findViewById(R.id.btnStudentLogin);

        tvStudentRegister =
                findViewById(R.id.tvStudentRegister);


        btnStudentLogin.setOnClickListener(v -> {

            String email =
                    etStudentEmail.getText()
                            .toString()
                            .trim();

            String password =
                    etStudentPassword.getText()
                            .toString();


            if (email.isEmpty()) {

                etStudentEmail.setError(
                        "Enter email"
                );

                etStudentEmail.requestFocus();

                return;
            }


            if (password.isEmpty()) {

                etStudentPassword.setError(
                        "Enter password"
                );

                etStudentPassword.requestFocus();

                return;
            }


            btnStudentLogin.setEnabled(false);

            btnStudentLogin.setText(
                    "LOGGING IN..."
            );


            // Firebase Authentication
            firebaseAuth
                    .signInWithEmailAndPassword(
                            email,
                            password
                    )
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            String uid =
                                    firebaseAuth
                                            .getCurrentUser()
                                            .getUid();


                            // Check Student Database
                            studentReference
                                    .child(uid)
                                    .get()
                                    .addOnCompleteListener(
                                            databaseTask -> {

                                                if (databaseTask.isSuccessful()
                                                        && databaseTask.getResult().exists()) {

                                                    String role =
                                                            databaseTask
                                                                    .getResult()
                                                                    .child("role")
                                                                    .getValue(
                                                                            String.class
                                                                    );


                                                    if ("Student".equals(role)) {

                                                        Toast.makeText(
                                                                StudentLoginActivity.this,
                                                                "Student login successful!",
                                                                Toast.LENGTH_SHORT
                                                        ).show();


                                                        Intent intent =
                                                                new Intent(
                                                                        StudentLoginActivity.this,
                                                                        StudentDashboardActivity.class
                                                                );

                                                        intent.setFlags(
                                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        );

                                                        startActivity(intent);

                                                        finish();

                                                    } else {

                                                        firebaseAuth.signOut();

                                                        btnStudentLogin.setEnabled(true);

                                                        btnStudentLogin.setText(
                                                                "LOGIN"
                                                        );

                                                        Toast.makeText(
                                                                StudentLoginActivity.this,
                                                                "This account is not a Student account.",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }

                                                } else {

                                                    // User exists in Authentication
                                                    // but is not a Student profile

                                                    firebaseAuth.signOut();

                                                    btnStudentLogin.setEnabled(true);

                                                    btnStudentLogin.setText(
                                                            "LOGIN"
                                                    );

                                                    Toast.makeText(
                                                            StudentLoginActivity.this,
                                                            "Please use a Student account.",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            }
                                    );

                        } else {

                            btnStudentLogin.setEnabled(true);

                            btnStudentLogin.setText(
                                    "LOGIN"
                            );

                            String message =
                                    "Login failed";

                            if (task.getException() != null) {

                                message =
                                        task.getException()
                                                .getMessage();
                            }

                            Toast.makeText(
                                    StudentLoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // Student Registration
        tvStudentRegister.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            StudentLoginActivity.this,
                            StudentRegisterActivity.class
                    );

            startActivity(intent);
        });
    }
}