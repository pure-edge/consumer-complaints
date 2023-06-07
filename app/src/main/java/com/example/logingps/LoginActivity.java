package com.example.logingps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logingps.utils.Connectivity;
import com.example.logingps.utils.ScreenSize;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    ".{6,}" +               //at Least 6 characters
                    "$");

    private EditText textViewEmail;
    private EditText textViewPassword;
    private TextView textViewMessage;
    private Button btnSignIn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        textViewEmail = findViewById(R.id.username);
        textViewPassword = findViewById(R.id.password);
        textViewMessage = findViewById(R.id.textViewMessage);
        progressBar = findViewById(R.id.progressBar);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mCurrentUser = mAuth.getCurrentUser();
                if (mCurrentUser != null) {
                    Toast.makeText(LoginActivity.this, getString(R.string.you_are_logged_in), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                } else {
                    //Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn = findViewById(R.id.click);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textViewEmail.getText().toString().trim();
                String pwd = textViewPassword.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
                btnSignIn.setEnabled(false);
                btnSignIn.setText(R.string.logging_in);

                mAuth.signInWithEmailAndPassword(username, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (!Connectivity.isConnected(LoginActivity.this)) {
                                openNoInternetDialog();
                            }
                            else if (ScreenSize.getScreenSize(LoginActivity.this)
                                    <= Configuration.SCREENLAYOUT_SIZE_NORMAL)  // NORMAL AND SMALL screen sizes
                                Toast.makeText(LoginActivity.this, R.string.message, Toast.LENGTH_LONG).show();
                            else
                                textViewMessage.setText(R.string.message);

                            progressBar.setVisibility(View.INVISIBLE);
                            btnSignIn.setEnabled(true);
                            btnSignIn.setText(R.string.log_in);

                        } else {
                            textViewMessage.setText("");
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            finish();
                        }
                    }
                });
            }

        });

        textViewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSignInSetEnabled();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                buttonSignInSetEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonSignInSetEnabled();
            }
        });
        textViewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSignInSetEnabled();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                buttonSignInSetEnabled();
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonSignInSetEnabled();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
    }

    private void openNoInternetDialog() {
        NoInternetDialogFragment dialogFragment = new NoInternetDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "no internet dialog");
    }

    private void buttonSignInSetEnabled() {
        String inputEmail = textViewEmail.getText().toString().trim();
        String inputPassword = textViewPassword.getText().toString().trim();
        if (inputEmail.length() == 0 || inputPassword.length() == 0) {
            btnSignIn.setEnabled(false);
        } else {
            btnSignIn.setEnabled(true);
        }
    }
}