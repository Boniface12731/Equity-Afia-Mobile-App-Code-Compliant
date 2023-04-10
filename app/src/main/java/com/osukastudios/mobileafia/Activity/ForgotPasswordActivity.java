package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.osukastudios.mobileafia.R;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText newEmail;
    private Button resetPassword, forgotPasswordBack;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.forgotpasswordtoolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        newEmail = findViewById(R.id.forgotPasswordEmail);
        resetPassword = findViewById(R.id.forgotPasswordButton);
        forgotPasswordBack = findViewById(R.id.forgotPasswordBack);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = newEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    newEmail.setError("Your Email is required!");
                    return;
                } else {
                    dialog.setTitle("Requesting...");
                    dialog.setMessage("Please wait as a new password is being requested");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent to your email. Please check it out.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Error in sending reset email.." + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        forgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}