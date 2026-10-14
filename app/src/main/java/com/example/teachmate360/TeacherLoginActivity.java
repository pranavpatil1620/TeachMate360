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

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText etTeacherEmail;
    private EditText etTeacherPassword;

    private Button btnTeacherLogin;
    private TextView tvTeacherRegister;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference teacherReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teacher_login);

        firebaseAuth = FirebaseAuth.getInstance();

        teacherReference =
                FirebaseDatabase.getInstance()
                        .getReference("teachers");

        etTeacherEmail =
                findViewById(R.id.etTeacherEmail);

        etTeacherPassword =
                findViewById(R.id.etTeacherPassword);

        btnTeacherLogin =
                findViewById(R.id.btnTeacherLogin);

        tvTeacherRegister =
                findViewById(R.id.tvTeacherRegister);


        btnTeacherLogin.setOnClickListener(v -> {

            String email =
                    etTeacherEmail.getText()
                            .toString()
                            .trim();

            String password =
                    etTeacherPassword.getText()
                            .toString();


            if (email.isEmpty()) {

                etTeacherEmail.setError(
                        "Enter email"
                );

                etTeacherEmail.requestFocus();

                return;
            }


            if (password.isEmpty()) {

                etTeacherPassword.setError(
                        "Enter password"
                );

                etTeacherPassword.requestFocus();

                return;
            }


            btnTeacherLogin.setEnabled(false);

            btnTeacherLogin.setText(
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


                            // Check Teacher Database
                            teacherReference
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


                                                    if ("Teacher".equals(role)) {

                                                        Toast.makeText(
                                                                TeacherLoginActivity.this,
                                                                "Teacher login successful!",
                                                                Toast.LENGTH_SHORT
                                                        ).show();


                                                        Intent intent =
                                                                new Intent(
                                                                        TeacherLoginActivity.this,
                                                                        TeacherDashboardActivity.class
                                                                );

                                                        intent.setFlags(
                                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        );

                                                        startActivity(intent);

                                                        finish();

                                                    } else {

                                                        firebaseAuth.signOut();

                                                        btnTeacherLogin.setEnabled(true);

                                                        btnTeacherLogin.setText(
                                                                "LOGIN"
                                                        );

                                                        Toast.makeText(
                                                                TeacherLoginActivity.this,
                                                                "This account is not a Teacher account.",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }

                                                } else {

                                                    // User exists in Authentication
                                                    // but is not a Teacher profile

                                                    firebaseAuth.signOut();

                                                    btnTeacherLogin.setEnabled(true);

                                                    btnTeacherLogin.setText(
                                                            "LOGIN"
                                                    );

                                                    Toast.makeText(
                                                            TeacherLoginActivity.this,
                                                            "Please use a Teacher account.",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            }
                                    );

                        } else {

                            btnTeacherLogin.setEnabled(true);

                            btnTeacherLogin.setText(
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
                                    TeacherLoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // Teacher Registration
        tvTeacherRegister.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TeacherLoginActivity.this,
                            TeacherRegisterActivity.class
                    );

            startActivity(intent);
        });
    }
}