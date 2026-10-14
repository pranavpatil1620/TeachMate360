package com.example.teachmate360;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RoleActivity extends AppCompatActivity {

    Button btnTeacher, btnStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_role);

        btnTeacher = findViewById(R.id.btnTeacher);
        btnStudent = findViewById(R.id.btnStudent);

        btnTeacher.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RoleActivity.this,
                    TeacherLoginActivity.class
            );

            startActivity(intent);
        });

        btnStudent.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RoleActivity.this,
                    StudentLoginActivity.class
            );

            startActivity(intent);
        });
    }
}