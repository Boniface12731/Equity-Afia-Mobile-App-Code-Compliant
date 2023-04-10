package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.osukastudios.mobileafia.Model.User;
import com.osukastudios.mobileafia.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignDoctorToPatientActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private RecyclerView allInvestorsRecyclerView;
    private TextView allInvestorsCount;
    String publisherid = "";
    String postid = "";
    String department = "";
    String specialist = "";
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_doctor_to_patient);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Connecting doctor to patient");

        publisherid = getIntent().getStringExtra("publisherid").toString();
        postid = getIntent().getStringExtra("postid").toString();
        department = getIntent().getStringExtra("department").toString();
        specialist = getIntent().getStringExtra("specialist").toString();
        loader = new ProgressDialog(this);

        allInvestorsCount = findViewById(R.id.allInvestorsCount);
        allInvestorsRecyclerView = findViewById(R.id.allInvestorsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //allInvestorsRecyclerView.setHasFixedSize(true);
        allInvestorsRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("users");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(reference.orderByChild("search").equalTo("doctor"+department+specialist), User.class)
                .build();

        FirebaseRecyclerAdapter<User, MyViewHolder> adapter = new FirebaseRecyclerAdapter<User,
                AssignDoctorToPatientActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AssignDoctorToPatientActivity.MyViewHolder holder,
                                            final int position, @NonNull final User model) {

                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userTypeBtn.setText(model.getType());
                Glide.with(getApplicationContext()).load(model.getProfilepictureurl()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String userid = getRef(position).getKey();

                        AlertDialog.Builder myDialog = new AlertDialog.Builder(AssignDoctorToPatientActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(AssignDoctorToPatientActivity.this);

                        View myView = inflater.inflate(R.layout.assigment_layout, null);

                        myDialog.setView(myView);

                        final AlertDialog dialog = myDialog.create();
                        dialog.setCancelable(false);

                        Button cancelBtn = myView.findViewById(R.id.btnCancel);
                        Button assignIdeaBtn = myView.findViewById(R.id.btnAssignIdea);
                        TextView item = myView.findViewById(R.id.item);

                        item.setText("Are You Sure You Want To Assign Patient To doctor " +model.getName()+"?");

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        assignIdeaBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loader.setMessage("Making the assignments");
                                loader.setCanceledOnTouchOutside(false);
                                loader.show();
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("appointments").child(postid);
                                reference2.child("assignedto").setValue(userid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            final DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("users").child(userid);
                                            reference3.child("assignedpatient").setValue(postid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(AssignDoctorToPatientActivity.this, "Successfully made the assignments", Toast.LENGTH_SHORT).show();

                                                        String text3 = "Your Booking was successful.";
                                                        addNotifications(postid, userid, text3);

                                                        String text = "Check out your new patient!";
                                                        addNotificationsForInvestorOrPartner(userid, postid, text);

                                                        loader.dismiss();
                                                    }
                                                }
                                            });
                                        }else {
                                            Toast.makeText(AssignDoctorToPatientActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            loader.dismiss();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }
            @NonNull
            @Override
            public AssignDoctorToPatientActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AssignDoctorToPatientActivity.this).inflate(R.layout.all_users_display_layout,parent,false);
                return new AssignDoctorToPatientActivity.MyViewHolder(view);
            }
        };
        allInvestorsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userEmail;
        CircleImageView profileImage;
        Button userTypeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userEmail = itemView.findViewById(R.id.user_email);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            userTypeBtn = itemView.findViewById(R.id.userTypeBtn);
        }
    }

    private void addNotifications(String postid, String userid,  String text){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications").child(publisherid);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", text);
        hashMap.put("userid", userid);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);
        reference.push().setValue(hashMap);
    }

    private void addNotificationsForInvestorOrPartner(String userid, String postid, String text){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("notifications").child(userid);
        HashMap<String, Object> hashMap = new HashMap<>();
        String mDate = DateFormat.getDateInstance().format(new Date());
        hashMap.put("text", text);
        hashMap.put("userid", userid);
        hashMap.put("postid", postid);
        hashMap.put("ispost",true);
        hashMap.put("date", mDate);

        reference.push().setValue(hashMap);
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