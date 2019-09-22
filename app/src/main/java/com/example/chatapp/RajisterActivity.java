package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RajisterActivity extends AppCompatActivity {
    EditText email,pass,username;
    Button register;
    private FirebaseAuth mAuth;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user");
    DatabaseReference reference;





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(this, "in", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rajister);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email=findViewById(R.id.email);
        pass=findViewById(R.id.pass);
        username=findViewById(R.id.username);
        register=findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username.getText().toString();
                String txt_pass = pass.getText().toString();
                String txt_email = email.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_pass) || TextUtils.isEmpty(txt_email)){
                    Toast.makeText(RajisterActivity.this, "Please Enter the field", Toast.LENGTH_SHORT).show();
                }else if( txt_pass.length() <6 ){
                    Toast.makeText(RajisterActivity.this, "Password length must be 8", Toast.LENGTH_SHORT).show();
                }
                else{
                    Register(txt_username,txt_pass,txt_email);
                }
            }
        });

    }

  private void Register(final String username , String pass, String email){
      mAuth.createUserWithEmailAndPassword(email, pass)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          // Sign in success, update UI with the signed-in user's information
                          Log.d("tag", "createUserWithEmail:success");
                          FirebaseUser user = mAuth.getCurrentUser();
                          String userid= user.getUid();
                          reference= myRef.child(userid);
                          HashMap<String,String> hasmap = new HashMap<>();

                          hasmap.put("id",userid);
                          hasmap.put("username",username);
                          hasmap.put("image_url","default");
                          hasmap.put("status","offline");
                          hasmap.put("search",username.toLowerCase());
                          reference.setValue(hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      Intent intent = new Intent(RajisterActivity.this,Main2Activity.class);
                                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                      startActivity(intent);
                                      finish();
                                  }
                              }
                          });

                      } else {
                          // If sign in fails, display a message to the user.
                          Log.w("tag", "createUserWithEmail:failure", task.getException());
                          Toast.makeText(RajisterActivity.this, "Authentication failed.",
                                  Toast.LENGTH_SHORT).show();

                      }

                      // ...
                  }
              });


  }



}
