package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.osukastudios.mobileafia.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorRegistrationActivity extends AppCompatActivity {

    private TextInputEditText registerEmail, registerPassword, registerFullNames, registerIdNumber, registerPhoneNumber, registerEmployeeNumber;
    private Button RegisterBtn, alreadyHaveAnAccount;
    private Spinner availabilitySpinner, departmentSpinner, specialistSpinner,doctorsBranch;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private DatabaseReference userDatabaseRef;
    private  String currentUserOnlineID;
    private CircleImageView profile_image;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);

        alreadyHaveAnAccount = findViewById(R.id.registrationPageQuestion);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        RegisterBtn = findViewById(R.id.RegisterBtn);
        registerFullNames = findViewById(R.id.registerFullName);
        profile_image = findViewById(R.id.profile_image);
        registerEmployeeNumber = findViewById(R.id.registerEmployeeNumber);
        availabilitySpinner = findViewById(R.id.availabilitySpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        specialistSpinner = findViewById(R.id.specialistSpinner);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        doctorsBranch=findViewById(R.id.doctorsBranch);

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString();
                final  String password = registerPassword.getText().toString();
                final String fullNames = registerFullNames.getText().toString();
                final String idNumber = registerIdNumber.getText().toString();
                final String phoneNumber = registerPhoneNumber.getText().toString();
                final String employeeNumber = registerEmployeeNumber.getText().toString();
                final String availabilityTime  = availabilitySpinner.getSelectedItem().toString();
                final String departmentSelected = departmentSpinner.getSelectedItem().toString();
                final String specialistSelected = specialistSpinner.getSelectedItem().toString();
                final String allocatedBranch  = doctorsBranch.getSelectedItem().toString();


                if (TextUtils.isEmpty(email)){
                    registerEmail.setError("Email Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    registerPassword.setError("Password Required!");
                    return;
                }if (TextUtils.isEmpty(fullNames)){
                    registerFullNames.setError("FullNames Required!");
                    return;
                }
                if (TextUtils.isEmpty(idNumber)){
                    registerIdNumber.setError("ID Number Required!");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)){
                    registerPhoneNumber.setError("Phone Number Required!");
                    return;
                }
                if (TextUtils.isEmpty(employeeNumber)){
                    registerEmployeeNumber.setError("Phone Number Required!");
                    return;
                }
                if (availabilityTime.equals("Select Time You are Available")){
                    Toast.makeText(DoctorRegistrationActivity.this, "Select a valid availability time", Toast.LENGTH_SHORT).show();
                }
                if (departmentSelected.equals("Select Department Here")){
                    Toast.makeText(DoctorRegistrationActivity.this, "Select a department!", Toast.LENGTH_SHORT).show();
                }
                if (specialistSelected.equals("Select Specialist Here")){
                    Toast.makeText(DoctorRegistrationActivity.this, "Select a specialist!", Toast.LENGTH_SHORT).show();
                }
                if (specialistSelected.equals("Select Branch"))
                {
                    Toast.makeText(DoctorRegistrationActivity.this, "Select Branch!", Toast.LENGTH_SHORT).show();
                }
                if (resultUri ==null){
                    Toast.makeText(DoctorRegistrationActivity.this, "Your profile Image is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DoctorRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(DoctorRegistrationActivity.this, "Registration Failed: \n" + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else {
                                currentUserOnlineID = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserOnlineID);

                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserOnlineID);
                                userInfo.put("name",fullNames);
                                userInfo.put("email", email);
                                userInfo.put("idnumber", idNumber);
                                userInfo.put("phonenumber", phoneNumber);
                                userInfo.put("employeenumber", employeeNumber);
                                userInfo.put("timeavailable", availabilityTime);
                                userInfo.put("department", departmentSelected);
                                userInfo.put("branch", allocatedBranch);
                                userInfo.put("specialist", specialistSelected);
                                userInfo.put("type", "doctor");
                                userInfo.put("search", "doctor"+departmentSelected+specialistSelected);

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener(){
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(DoctorRegistrationActivity.this, "Details set successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            String error = task.getException().toString();
                                            Toast.makeText(DoctorRegistrationActivity.this, "Details upload Failed: "+ error, Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        loader.dismiss();
                                    }
                                });

                                if (resultUri !=null){
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(currentUserOnlineID);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20,byteArrayOutputStStream);
                                    byte[] data = byteArrayOutputStStream.toByteArray();
                                    UploadTask uploadTask = filepath.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            finish();
                                            return;
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null) {
                                                if (taskSnapshot.getMetadata().getReference() != null) {
                                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String imageUrl = uri.toString();
                                                            Map newImageMap = new HashMap();
                                                            newImageMap.put("profilepictureurl", imageUrl);
                                                            userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(DoctorRegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                    }else {
                                                                        String error = task.getException().toString();
                                                                        Toast.makeText(DoctorRegistrationActivity.this, "Process failed "+ error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                                    Intent intent = new Intent(DoctorRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }}
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profile_image.setImageURI(resultUri);
        }
    }
}