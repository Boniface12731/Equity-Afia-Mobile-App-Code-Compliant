package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.osukastudios.mobileafia.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private CircleImageView profile_image;
    private EditText fullName, email, phonenumber, idnumber, employeeNumber;
    private Spinner availabilitySpinner,departmentSpinner,specialistSpinner;
    private Button updateDetailsButton;
    private TextView backButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;
    private Uri profileImageUri;

    private String mName, mEmail, mEmpNo,mPhoneNumber, mIdNumber, mAvailability, mDept, mSpecialist,mProfilePicture = "";

    StorageTask uploadTask;
    StorageReference storageReference;
    private String imageSaveLocationUrl ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Information");

        profile_image = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phonenumber = findViewById(R.id.phonenumber);
        idnumber = findViewById(R.id.idnumber);
        employeeNumber = findViewById(R.id.employeeNumber);
        availabilitySpinner = findViewById(R.id.availabilitySpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        specialistSpinner = findViewById(R.id.specialistSpinner);
        updateDetailsButton = findViewById(R.id.updateDetailsButton);
        backButton = findViewById(R.id.backButton);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.
                getInstance().getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference("users details images");
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue(String.class);

                if (!type.equals("doctor")){
                    employeeNumber.setVisibility(View.GONE);
                    availabilitySpinner.setVisibility(View.GONE);
                    departmentSpinner.setVisibility(View.GONE);
                    specialistSpinner.setVisibility(View.GONE);

                    getUserInformation();
                }else {
                    getDoctorInformation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        updateDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performValidations();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getDoctorInformation() {
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") !=null){
                        mName = map.get("name").toString();
                        fullName.setText(mName);
                        fullName.setSelection(mName.length());
                    }
                    if (map.get("email") !=null){
                        mEmail = map.get("email").toString();
                        email.setText(mEmail);
                        email.setSelection(mEmail.length());
                    }
                    if (map.get("employeenumber") !=null){
                        mEmpNo = map.get("employeenumber").toString();
                        employeeNumber.setText(mEmpNo);
                        employeeNumber.setSelection(mEmpNo.length());
                    }
                    if (map.get("phonenumber") !=null){
                        mPhoneNumber = map.get("phonenumber").toString();
                        phonenumber.setText(mPhoneNumber);
                        phonenumber.setSelection(mPhoneNumber.length());
                    }
                    if (map.get("idnumber") !=null){
                        mIdNumber = map.get("idnumber").toString();
                        idnumber.setText(mIdNumber);
                        idnumber.setSelection(mIdNumber.length());
                    }

                    if (map.get("timeavailable") !=null){
                        mAvailability = map.get("timeavailable").toString();
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.timeframes, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        availabilitySpinner.setAdapter(adapter);
                        if (mAvailability != null) {
                            int spinnerPosition = adapter.getPosition(mAvailability);
                            availabilitySpinner.setSelection(spinnerPosition);
                        }
                    }

                    if (map.get("department") !=null){
                        mDept = map.get("department").toString();
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.departments, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(adapter);
                        if (mDept != null) {
                            int spinnerPosition = adapter.getPosition(mDept);
                            departmentSpinner.setSelection(spinnerPosition);
                        }
                    }

                    if (map.get("specialist") !=null){
                        mSpecialist = map.get("specialist").toString();
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProfileActivity.this, R.array.specialists, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(adapter);
                        if (mSpecialist != null) {
                            int spinnerPosition = adapter.getPosition(mSpecialist);
                            departmentSpinner.setSelection(spinnerPosition);
                        }
                    }

                    if (map.get("profilepictureurl") !=null){
                        mProfilePicture = map.get("profilepictureurl").toString();
                        Glide.with(getApplication()).load(mProfilePicture).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInformation(){
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") !=null){
                        mName = map.get("name").toString();
                        fullName.setText(mName);
                        fullName.setSelection(mName.length());
                    }
                    if (map.get("email") !=null){
                        mEmail = map.get("email").toString();
                        email.setText(mEmail);
                        email.setSelection(mEmail.length());
                    }

                    if (map.get("phonenumber") !=null){
                        mPhoneNumber = map.get("phonenumber").toString();
                        phonenumber.setText(mPhoneNumber);
                        phonenumber.setSelection(mPhoneNumber.length());
                    }
                    if (map.get("idnumber") !=null){
                        mIdNumber = map.get("idnumber").toString();
                        idnumber.setText(mIdNumber);
                        idnumber.setSelection(mIdNumber.length());
                    }
                    if (map.get("profilepictureurl") !=null){
                        mProfilePicture = map.get("profilepictureurl").toString();
                        Glide.with(getApplication()).load(mProfilePicture).into(profile_image);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void performValidations(){
        final String nameString  = fullName.getText().toString();
        final String emailString = email.getText().toString();
        final String phoneString = phonenumber.getText().toString();
        final String idString = idnumber.getText().toString();
        final String employeeNoString = employeeNumber.getText().toString();
        final String timeAvailableString = availabilitySpinner.getSelectedItem().toString();
        final String deptString = departmentSpinner.getSelectedItem().toString();
        final  String specialString = specialistSpinner.getSelectedItem().toString();

        if (!nameString.isEmpty() &&
                !emailString.isEmpty() &&
                !phoneString.isEmpty() &&
                !idString.isEmpty() &&
                employeeNoString.equals("") &&
                timeAvailableString.equals("") &&
                deptString.equals("") &&
                specialString.equals("") &&
                profileImageUri !=null){

            uploadUserInfo();
        }

        if (!nameString.isEmpty() &&
                !emailString.isEmpty() &&
                !phoneString.isEmpty() &&
                !idString.isEmpty() &&
                !employeeNoString.isEmpty() &&
                !timeAvailableString.isEmpty() &&
                !deptString.isEmpty() &&
                !specialString.isEmpty() &&
                profileImageUri !=null){

            uploadDoctorInfo();
        }
        else {
            Toast.makeText(this, "Check missing fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoader(){
        loader.setMessage("Uploading details. Please wait...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadUserInfo() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("name", fullName.getText().toString() );
        hashMap.put("phonenumber",phonenumber.getText().toString());
        hashMap.put("idnumber",idnumber.getText().toString());
        hashMap.put("email", email.getText().toString());

        userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Details Uploaded successfully",
                            Toast.LENGTH_SHORT).show();
                    uploadProfilePicture();
                }else {
                    Toast.makeText(ProfileActivity.this, "Failed to upload details" +
                            task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadDoctorInfo() {
        startLoader();

        HashMap<String, Object> hashMap =   new HashMap<>();
        hashMap.put("name", fullName.getText().toString() );
        hashMap.put("phonenumber",phonenumber.getText().toString());
        hashMap.put("idnumber",idnumber.getText().toString());
        hashMap.put("email", email.getText().toString());
        hashMap.put("timeavailable", availabilitySpinner.getSelectedItem().toString());
        hashMap.put("specialist", specialistSpinner.getSelectedItem().toString());
        hashMap.put("employeenumber", employeeNumber.getText().toString());
        hashMap.put("department", departmentSpinner.getSelectedItem().toString());


        userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Details Uploaded successfully", Toast.LENGTH_SHORT).show();
                    uploadProfilePicture();
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Failed to upload details" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfilePicture() {
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(profileImageUri));
        uploadTask = fileReference.putFile(profileImageUri);
        uploadTask.continueWithTask(new Continuation(){
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imageSaveLocationUrl = downloadUri.toString();

                    HashMap<String, Object> hashMap =   new HashMap<>();
                    hashMap.put("profilepictureurl", imageSaveLocationUrl);

                    userDatabaseRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "profile picture added successfully", Toast.LENGTH_SHORT).show();
                                directUserToMainActivity();
                            }
                            loader.dismiss();
                            finish();
                        }
                    });
                    // finish();

                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(ProfileActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Profile image could not be added."+ e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1 && resultCode == Activity.RESULT_OK ){
            profileImageUri = data.getData();
            profile_image.setImageURI(profileImageUri);
        }
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

    private void directUserToMainActivity(){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}