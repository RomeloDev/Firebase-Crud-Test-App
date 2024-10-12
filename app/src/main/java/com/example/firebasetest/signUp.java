package com.example.firebasetest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {

    private EditText namefield, emailfield, passwordfield, cpwordfield;
    private Button signupBtn, loginBtn;
    private FirebaseDatabase database;
    private DatabaseReference usersref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        usersref = database.getReference("Users");

        namefield = findViewById(R.id.signup_name);
        emailfield = findViewById(R.id.login_email);
        passwordfield = findViewById(R.id.login_password);
        cpwordfield = findViewById(R.id.signup_confirmPassword);
        signupBtn = findViewById(R.id.submitBtn);
        loginBtn = findViewById(R.id.login_signup);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = namefield.getText().toString();
                String email = emailfield.getText().toString();
                String password = passwordfield.getText().toString();
                String cpword = cpwordfield.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(cpword)){
                    String userId = usersref.push().getKey();
                    userModel newUser = new userModel(userId, name, email, password);
                    usersref.child(userId).setValue(newUser)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(signUp.this, "User Registered", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(signUp.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else if (!password.equals(cpword)) {
                    Toast.makeText(signUp.this, "Password and Confirm Password must equal!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(signUp.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}