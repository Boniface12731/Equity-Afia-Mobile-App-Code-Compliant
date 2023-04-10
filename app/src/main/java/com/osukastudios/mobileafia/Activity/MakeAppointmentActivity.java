package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.osukastudios.mobileafia.Model.IDOFTHEADMIN;
import com.osukastudios.mobileafia.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MakeAppointmentActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private EditText descriptionText;
    private Spinner departmentSpinner, specialistSpinner,availabilitySpinner,branchSpinner;
    private Button btnCancel, makeAppointmentButton;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;
    private DatabaseReference postedByRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);

        descriptionText = findViewById(R.id.descriptionText);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        specialistSpinner = findViewById(R.id.specialistSpinner);
        availabilitySpinner = findViewById(R.id.availabilitySpinner);
        branchSpinner = findViewById(R.id.branchSpinner);
        btnCancel = findViewById(R.id.btnCancel);
        makeAppointmentButton = findViewById(R.id.makeAppointmentButton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Book an Appointment");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        makeAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performIdeaValidations();
            }
        });

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        descriptionText.setText(prefs.getString("autoSave", ""));
        descriptionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            @Override
            public void afterTextChanged(Editable s){
                prefs.edit().putString("autoSave", s.toString()).commit();
            }
        });
    }
    private void startLoader(){
        loader.setMessage("Saving. Please wait");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

    private void performIdeaValidations(){
        String appointmentDetails = descriptionText.getText().toString();
        String appointmentDept = departmentSpinner.getSelectedItem().toString();
        String appointmentSpecialist = specialistSpinner.getSelectedItem().toString();
        String appointmentTime = availabilitySpinner.getSelectedItem().toString();
        String patientBranch = branchSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(appointmentDetails)){
            descriptionText.setError("Description required!");
        }
        if (appointmentDept.equals("Select Department Here")){
            Toast.makeText(this, "Select a department!", Toast.LENGTH_SHORT).show();
        }
        if (appointmentSpecialist.equals("Select Specialist Here")){
            Toast.makeText(this, "Select a specialist!", Toast.LENGTH_SHORT).show();
        }
        if (appointmentTime.equals("Select Time You Are Available")){
            Toast.makeText(this, "Select a valid time!", Toast.LENGTH_SHORT).show();
        }

        else {
            startLoader();
            String mDate = DateFormat.getDateInstance().format(new Date());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appointments");
            final String appointmentid = reference.push().getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", appointmentid);
            hashMap.put("details", appointmentDetails);
            hashMap.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            hashMap.put("time", appointmentTime);
            hashMap.put("specialist", appointmentSpecialist);
            hashMap.put("department", appointmentDept);
            hashMap.put("date", mDate);
            //19/9/22
            hashMap.put("search", patientBranch);


            assert appointmentid != null;
            reference.child(appointmentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        loader.dismiss();
                        Toast.makeText(MakeAppointmentActivity.this, "Booking Made successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MakeAppointmentActivity.this, MainActivity.class));
                        finish();

                        String text = "Made an appointment booking";
                        addNotifications(appointmentid, text);
                    }
                    else
                    {
                        String e = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(MakeAppointmentActivity.this, "Could not be posted" + e, Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    }
                }
            });
        }
    }
    private void addNotifications(String postid, String text){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications")
                .child(IDOFTHEADMIN.getIdOfTheAdmin());
        HashMap<String, Object>hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("userid", mAuth.getCurrentUser().getUid());
        hashMap.put("text", text);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Making An Appointment")
                .setMessage("Are you sure you want to leave?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MakeAppointmentActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}