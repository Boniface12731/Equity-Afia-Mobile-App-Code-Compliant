package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osukastudios.mobileafia.R;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText adminEmail,adminPassword;
    private Button adminBtn;
    private TextView adminPageQuestion;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminEmail = findViewById(R.id.adminEmail);
        adminPassword = findViewById(R.id.adminPassword);
        adminBtn = findViewById(R.id.adminBtn);
        adminPageQuestion = findViewById(R.id.adminPageQuestion);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        adminPageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =  adminEmail.getText().toString();
                String password = adminPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    adminEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    adminPassword.setError("Password is required");
                    return;
                }
                else {
                    loader.setMessage("Sign in process in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if (task.isSuccessful()){

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String type = snapshot.child("type").getValue(String.class);
                                        if (type.equals("Admin")){

                                            Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(AdminLoginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(AdminLoginActivity.this, "You are not an admin, Please login in the login page", Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(AdminLoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else {
                                Toast.makeText(AdminLoginActivity.this, "Sign in process failed, please try again "+task.getException(), Toast.LENGTH_LONG).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
            }
        });
    }
}