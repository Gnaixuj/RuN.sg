package com.example.cz2006trial.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cz2006trial.database.DatabaseManager;
import com.example.cz2006trial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mUsername, mEmail, mPassword, mConfirmPassword;
    private Button mRegisterButton;
    private TextView mLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        mRegisterButton = findViewById(R.id.registerButton);
        mLogin = findViewById(R.id.fromRegisterToLogin);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        // Check If the User is Already Login
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }

        // register user via firebase authentication system when cregister button is clicked
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mUsername.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                // when username field is empty
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is Required");
                    return;
                }
                // when username is less than 8 characters long
                if (username.length() < 8) {
                    mUsername.setError("Username Must Be At Least 8 Characters Long");
                    return;
                }
                // when email field is empty
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    return;
                }
                // when password field is empty
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required");
                    return;
                }
                // when password is less than 8 characters long
                if (password.length() < 8) {
                    mPassword.setError("Password Must Be At Least 8 Characters Long");
                    return;
                }
                // when confirm password does not match with password
                if (!confirmPassword.equals(password)) {
                    mConfirmPassword.setError("Confirm Password Fill is Different from the Password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                // listen to firebase and wait till firebase has registered the user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // upon successful registration, direct user to the Maps page
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            DatabaseManager.updateProfileData(username, email);
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                            finish();
                        }
                        // upon unsuccessful registration, display error message
                        else {
                            Toast.makeText(SignUpActivity.this, "Error " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}
