package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.osukastudios.mobileafia.Model.User;
import com.osukastudios.mobileafia.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsActivity extends AppCompatActivity {
    private Toolbar settingsToolbar;
    private RecyclerView allDoctorsRecyclerView;
    private TextView allDoctorsCount;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All Doctors");

        allDoctorsCount = findViewById(R.id.allDoctorsCount);

        allDoctorsRecyclerView = findViewById(R.id.allDoctorsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //allInvestorsRecyclerView.setHasFixedSize(true);
        allDoctorsRecyclerView.setLayoutManager(layoutManager);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("type").equalTo("doctor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                long numberOfUsers = datasnapshot.getChildrenCount();
                int noOfInvestors = (int) numberOfUsers;
                allDoctorsCount.setText("Total Doctors: " +noOfInvestors);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(usersRef.orderByChild("type").equalTo("doctor"), User.class)
                .build();

        FirebaseRecyclerAdapter<User, MyViewHolder> adapter = new FirebaseRecyclerAdapter<User, DoctorsActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorsActivity.MyViewHolder holder, final int position, @NonNull User model) {
                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                Glide.with(DoctorsActivity.this).load(model.getProfilepictureurl()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String userid = getRef(position).getKey();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Intent intent = new Intent(DoctorsActivity.this, UserDetailsActivity.class);
                                intent.putExtra("userid", userid);
                                intent.putExtra("usertype", "doctor");
                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                });
            }
            @NonNull
            @Override
            public DoctorsActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(DoctorsActivity.this).inflate(R.layout.users_display_layout,parent,false);
                return new DoctorsActivity.MyViewHolder(view);
            }
        };
        allDoctorsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userEmail;
        CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userEmail = itemView.findViewById(R.id.user_email);
            profileImage = itemView.findViewById(R.id.users_profile_image);

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
}