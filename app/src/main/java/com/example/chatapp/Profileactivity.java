package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profileactivity extends AppCompatActivity {

    TextView username;
    CircleImageView profile_pic;
    FirebaseUser fuser;
    DatabaseReference reference;
    StorageReference storageReference;
    private static  final int Image_Request= 1;
    private Uri imageuri;
    private StorageTask uploadtask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profileactivity.this,Main2Activity.class));
                finish();
            }
        });


        username= findViewById(R.id.username);

        profile_pic= findViewById(R.id.profile_pic);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if(user.getImage_url().equals("default")){
                        profile_pic.setImageResource(R.mipmap.ic_launcher);
                    }else{

                        Picasso.get().load(user.getImage_url()).into(profile_pic);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

    }
    private void openImage(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,Image_Request);

        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Image_Request);*/
    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = Profileactivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void uploadImage(){
        final ProgressDialog pd =  new ProgressDialog(Profileactivity.this);
        pd.setMessage("Uploading");
        pd.show();
        if(imageuri != null){

            final StorageReference fileRefrence= storageReference.child(System.currentTimeMillis()+"."+"."+getFileExtention(imageuri));
            uploadtask= fileRefrence.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation< UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();
                    }
                    return fileRefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        Uri downloadUri=task.getResult();
                        String mUri= downloadUri.toString();

                        reference= FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image_url", mUri);
                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }else{
                        Toast.makeText(Profileactivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profileactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{

            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Image_Request&&resultCode==RESULT_OK && data!=null&& data.getData()!=null){
            imageuri = data.getData();

            if(uploadtask != null&& uploadtask.isInProgress()){

                Toast.makeText(this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }
        }

    }
}
