package com.osukastudios.mobileafia.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.osukastudios.mobileafia.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Chemist extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    ImageButton imageButton;
    EditText edtfirst,edtlast;
    Button btninsert;
    private  static final  int Gallery_Code=1;
    Uri imageUrl = null;
    ProgressDialog progressDialog;
    private Toolbar chemToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemist);

        chemToolbar = findViewById(R.id.chemistToolbar);
        setSupportActionBar(chemToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Medicine");

        imageButton = findViewById(R.id.imageButton);
        edtfirst = findViewById(R.id.edtfirstname);
        edtlast = findViewById(R.id.edtlastname);
        btninsert = findViewById(R.id.btnInsert);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("Student");
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Code);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode  == Gallery_Code && resultCode == RESULT_OK){
            imageUrl = data.getData();
            imageButton.setImageURI(imageUrl);
        }

        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
               String fn =  edtfirst.getText().toString().trim();
               String ln =  edtlast.getText().toString().trim();

               if (!(fn.isEmpty() && ln.isEmpty() && imageUrl !=null)){
                   progressDialog.setTitle("Uploading...");
                   progressDialog.show();
                   StorageReference filepath = mStorage.getReference().
                           child("imagePost").child(imageUrl.getLastPathSegment());
                   filepath.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().
                                   addOnCompleteListener(new OnCompleteListener<Uri>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Uri> task) {
                                           String t = task.getResult().toString();
                                           DatabaseReference newPost = mRef.push();
                                           newPost.child("FirstName").setValue(fn);
                                           newPost.child("LastName").setValue(ln);
                                           newPost.child("image").setValue(task.getResult().toString());
                                           progressDialog.dismiss();

                                           Intent intent = new Intent(Chemist.this, RetrieveDataInRecyclerView.class);
                                           startActivity(intent);
                                       }
                                   });
                       }
                   });
               }
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