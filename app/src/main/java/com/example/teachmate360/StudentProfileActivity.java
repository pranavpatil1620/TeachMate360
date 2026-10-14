package com.example.teachmate360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView tvStudentProfileName;
    private TextView tvStudentProfileEmail;
    private TextView tvStudentProfileRollNo;
    private TextView tvStudentProfileClass;

    private Button btnStudentProfileLogout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference studentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        studentReference =
                FirebaseDatabase.getInstance()
                        .getReference("students");

        tvStudentProfileName =
                findViewById(R.id.tvStudentProfileName);

        tvStudentProfileEmail =
                findViewById(R.id.tvStudentProfileEmail);

        tvStudentProfileRollNo =
                findViewById(R.id.tvStudentProfileRollNo);

        tvStudentProfileClass =
                findViewById(R.id.tvStudentProfileClass);

        btnStudentProfileLogout =
                findViewById(R.id.btnStudentProfileLogout);

        loadStudentProfile();

        btnStudentProfileLogout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent =
                    new Intent(
                            StudentProfileActivity.this,
                            RoleActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    private void loadStudentProfile() {

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String uid =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        studentReference
                .child(uid)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()
                            && task.getResult().exists()) {

                        String name =
                                task.getResult()
                                        .child("name")
                                        .getValue(String.class);

                        String email =
                                task.getResult()
                                        .child("email")
                                        .getValue(String.class);

                        String rollNo =
                                task.getResult()
                                        .child("rollNo")
                                        .getValue(String.class);

                        String studentClass =
                                task.getResult()
                                        .child("studentClass")
                                        .getValue(String.class);

                        if (name == null) {
                            name = "Not available";
                        }

                        if (email == null) {
                            email = "Not available";
                        }

                        if (rollNo == null) {
                            rollNo = "Not available";
                        }

                        if (studentClass == null) {
                            studentClass = "Not available";
                        }

                        tvStudentProfileName.setText(name);
                        tvStudentProfileEmail.setText(email);
                        tvStudentProfileRollNo.setText(rollNo);
                        tvStudentProfileClass.setText(studentClass);

                    } else {

                        Toast.makeText(
                                StudentProfileActivity.this,
                                "Unable to load profile.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}