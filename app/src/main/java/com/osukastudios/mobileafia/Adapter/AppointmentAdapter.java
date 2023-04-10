package com.osukastudios.mobileafia.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osukastudios.mobileafia.Model.Appointment;
import com.osukastudios.mobileafia.Activity.AssignDoctorToPatientActivity;
import com.osukastudios.mobileafia.Model.IDOFTHEADMIN;
import com.osukastudios.mobileafia.R;
import com.osukastudios.mobileafia.Model.User;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public Context mContext;
    public List<Appointment> mAppointment;
    private FirebaseUser firebaseUser;

    public AppointmentAdapter(Context mContext, List<Appointment> mAppointment) {
        this.mContext = mContext;
        this.mAppointment = mAppointment;
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.appointments_retrieved_layout,parent,
                false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Appointment appointment = mAppointment.get(position);

        if (appointment.getAssignedto() !=null){
            holder.appointmentTimeTv.setVisibility(View.VISIBLE);
            holder.doctorDetails.setVisibility(View.VISIBLE);
            holder.doctorTitle.setVisibility(View.VISIBLE);
            getDoctorInformation(appointment.getAssignedto(), holder.doctorImageView, holder.doctorName,
                    holder.doctorEmail, holder.doctorPhone, holder.doctorDepartment, holder.doctorSpecialist);
        }

        holder.dateBookedTextView.setText(appointment.getDate());
        holder.patientDescription.setText(appointment.getDetails());
        getPatientInformation(appointment.getPublisher(), holder.patientImageView, holder.patientName,
                holder.patientEmail,holder.patientPhone);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                editPost(appointment.getId());
                                return true;
                            case R.id.delete:
                                new androidx.appcompat.app.AlertDialog.Builder(mContext)
                                        .setTitle("Appointment Deletion")
                                        .setMessage("Are you sure you want to delete your appointment?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                FirebaseDatabase.getInstance().getReference("appointments")
                                                        .child(appointment.getId()).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    Toast.makeText(mContext, "Appointment deleted successfully",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }else {
                                                                    Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                return true;

                            case R.id.report:
                                Intent intent = new Intent(mContext, AssignDoctorToPatientActivity.class);
                                intent.putExtra("publisherid", appointment.getPublisher());
                                intent.putExtra("postid", appointment.getId());
                                intent.putExtra("department", appointment.getDepartment());
                                intent.putExtra("specialist", appointment.getSpecialist());
                                mContext.startActivity(intent);

                                return true;

                            case R.id.cancelAppointment:
                                final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("appointments").child(appointment.getId());
                                reference2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists() && snapshot.hasChild("assignedto") ){
                                            String assignedToString = snapshot.child("assignedto").getValue().toString();

                                            if (assignedToString.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                reference2.child("assignedto").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            final DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("users")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            reference3.child("assignedpatient").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(mContext, "Appointment canceled!", Toast.LENGTH_SHORT).show();
                                                                        String text3 = "Your appointment was cancelled!";
                                                                        addNotifications(appointment.getId(), appointment.getPublisher(), text3);
                                                                        String text = "You've cancelled the appointment";
                                                                        addNotificationsForInvestorOrPartner(FirebaseAuth.getInstance().
                                                                                getCurrentUser().getUid(), appointment.getId(), text);
                                                                    }
                                                                }
                                                            });
                                                        }else {
                                                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if (!appointment.getPublisher().equals(firebaseUser.getUid()) && ! IDOFTHEADMIN.getIdOfTheAdmin().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.cancelAppointment).setVisible(true);
                }
                if (!IDOFTHEADMIN.getIdOfTheAdmin().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mAppointment.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView patientImageView, doctorImageView, more;
        public TextView patientName, patientEmail, patientPhone, patientDescription;
        public TextView doctorName, doctorEmail,doctorPhone, doctorDepartment, doctorSpecialist;
        public TextView appointmentTimeTv, dateBookedTextView, doctorTitle;
        public LinearLayout doctorDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientImageView = itemView.findViewById(R.id.patientImageView);
            doctorImageView = itemView.findViewById(R.id.doctorImageView);
            more = itemView.findViewById(R.id.more);
            patientName = itemView.findViewById(R.id.patientName);
            patientEmail = itemView.findViewById(R.id.patientEmail);
            patientPhone = itemView.findViewById(R.id.patientPhone);
            patientDescription = itemView.findViewById(R.id.patientDescription);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorEmail = itemView.findViewById(R.id.doctorEmail);
            doctorPhone = itemView.findViewById(R.id.doctorPhone);
            doctorDepartment = itemView.findViewById(R.id.doctorDepartment);
            doctorSpecialist = itemView.findViewById(R.id.doctorSpecialist);
            appointmentTimeTv = itemView.findViewById(R.id.appointmentTimeTv);
            dateBookedTextView = itemView.findViewById(R.id.dateBookedTextView);
            doctorTitle = itemView.findViewById(R.id.doctorTitle);
            doctorDetails = itemView.findViewById(R.id.doctorDetails);
        }
    }
    private void getPatientInformation(String publisher,
                                       final ImageView patientImageView,
                                       final TextView  patientName,
                                       final TextView patientEmail,
                                       final TextView  patientPhone) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(publisher);
        reference.addValueEventListener(new ValueEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getProfilepictureurl()).into(patientImageView);
                patientName.setText(user.getName());
                patientEmail.setText(user.getEmail());
                patientPhone.setText(user.getPhonenumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDoctorInformation(String assignedto,
                                      final ImageView doctorImageView,
                                      final TextView doctorName,
                                      final TextView doctorEmail,
                                      final TextView doctorPhone,
                                      final TextView doctorDepartment,
                                      final TextView doctorSpecialist)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(assignedto);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (isValidContextForGlide(mContext)){
                    Glide.with(mContext).load(user.getProfilepictureurl()).into(doctorImageView);
                }
                doctorName.setText(user.getName());
                doctorEmail.setText(user.getEmail());
                doctorPhone.setText(user.getPhonenumber());
                doctorDepartment.setText(user.getDepartment());
                doctorSpecialist.setText(user.getSpecialist());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        editText.setLayoutParams(lp);
        alertDialog.setView(editText);
        getText(postid,editText);
        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String idea = editText.getText().toString();
                        if (idea.isEmpty()){
                            Toast.makeText(mContext, "You must type something!", Toast.LENGTH_SHORT).show();
                        }else {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("details", editText.getText().toString());
                            FirebaseDatabase.getInstance().getReference("appointments").child(postid).updateChildren(hashMap);
                        }
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("appointments")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Appointment.class).getDetails());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addNotifications(String postid, String userid,  String text){
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
}
