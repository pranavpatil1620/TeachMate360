package com.example.teachmate360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherProfileActivity extends AppCompatActivity {

    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfileDepartment;
    private TextView tvProfileMobile;

    private Button btnProfileLogout;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_teacher_profile
        );

        firebaseAuth =
                FirebaseAuth.getInstance();

        tvProfileName =
                findViewById(R.id.tvProfileName);

        tvProfileEmail =
                findViewById(R.id.tvProfileEmail);

        tvProfileDepartment =
                findViewById(R.id.tvProfileDepartment);

        tvProfileMobile =
                findViewById(R.id.tvProfileMobile);

        btnProfileLogout =
                findViewById(R.id.btnProfileLogout);

        loadTeacherProfile();

        btnProfileLogout.setOnClickListener(
                v -> logoutTeacher()
        );
    }

    // =================================================
    // LOAD TEACHER PROFILE
    // =================================================

    private void loadTeacherProfile() {

        if (firebaseAuth.getCurrentUser() == null) {

            return;
        }

        String uid =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        FirebaseDatabase
                .getInstance()
                .getReference("teachers")
                .child(uid)
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (snapshot.exists()) {

                                String name =
                                        snapshot
                                                .child("name")
                                                .getValue(
                                                        String.class
                                                );

                                String email =
                                        snapshot
                                                .child("email")
                                                .getValue(
                                                        String.class
                                                );

                                String department =
                                        snapshot
                                                .child("department")
                                                .getValue(
                                                        String.class
                                                );

                                String mobile =
                                        snapshot
                                                .child("mobile")
                                                .getValue(
                                                        String.class
                                                );

                                if (name != null) {

                                    tvProfileName.setText(
                                            name
                                    );
                                }

                                if (email != null) {

                                    tvProfileEmail.setText(
                                            email
                                    );
                                }

                                if (department != null) {

                                    tvProfileDepartment.setText(
                                            department
                                    );
                                }

                                if (mobile != null) {

                                    tvProfileMobile.setText(
                                            mobile
                                    );
                                }

                            } else {

                                Toast.makeText(
                                        TeacherProfileActivity.this,
                                        "Profile not found",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    TeacherProfileActivity.this,
                                    "Unable to load profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // =================================================
    // LOGOUT
    // =================================================

    private void logoutTeacher() {

        firebaseAuth.signOut();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent =
                new Intent(
                        TeacherProfileActivity.this,
                        RoleActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}