package com.example.shubhampc.referapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextUserName, editTextPassword, editTextReferalCode;
    Button buttonSignup;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private CollectionReference mcolRef;
    private String username,password,score;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        String link = "";
        if(appLinkData!=null){
            link = appLinkData.toString();
        }


        mAuth = FirebaseAuth.getInstance();
        mcolRef = FirebaseFirestore.getInstance().collection("users");

        editTextUserName  = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextReferalCode = findViewById(R.id.editTextReferCode);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }


        if(link.isEmpty()){
            // do nothing
        }
        else{
            String refer_code = link.substring(20);
            editTextReferalCode.setText(refer_code);
        }

    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSignup){
            // signup
            if(editTextPassword.getText() == null || editTextUserName.getText() == null){
                Toast.makeText(this, "Please fill the username and password", Toast.LENGTH_SHORT).show();
            }
            else
            signup();
        }
        if(v.getId() == R.id.buttonLogin){
            if(editTextPassword.getText() == null || editTextUserName.getText() == null){
                Toast.makeText(this, "Please fill the username and password", Toast.LENGTH_SHORT).show();
            }
            else
                login();
        }

    }


    private void login(){


        progressDialog.setMessage("Logging in, Please Wait...");
        progressDialog.show();


        String username = editTextUserName.getText().toString();
        String password = editTextPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // update
                    finish();
                    Intent intent = new Intent(MainActivity.this,ProfileActivity.class);




                    startActivity(intent);

                }
                else{
                    Log.i("taskResult",task.getResult().toString());
                    Toast.makeText(MainActivity.this, "Please try Again!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
        
    }



    private void signup() {
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        final String username = editTextUserName.getText().toString();
        final String password = editTextPassword.getText().toString();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please gand",Toast.LENGTH_LONG).show();
            return ;
        }


        //creating a new user
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            updateInfo();
                            finish();
                            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                            if(editTextReferalCode.getText()==null || editTextReferalCode.getText().toString().isEmpty()){

                            }
                            else{
                                final String uid = editTextReferalCode.getText().toString();

                                mcolRef.document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String user_name = documentSnapshot.get("username").toString();
                                        String user_pass = documentSnapshot.get("username").toString();
                                        String user_score = documentSnapshot.get("score").toString();
                                        Long score = Long.parseLong(user_score);
                                        score+=25;
                                        String new_score = Long.toString(score);

                                        Map<String,Object> data  =  new HashMap<>();
                                        data.put("username",user_name);
                                        data.put("password",user_pass);
                                        data.put("score",new_score);

                                        mcolRef.document(uid).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                     Log.i("done boy","done");
                                            }
                                        });


                                    }
                                });
                            }

                            startActivity(intent);

                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }else{
                            //display some message here
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });



    }

    private void updateInfo() {

        username = editTextUserName.getText().toString();
        password = editTextPassword.getText().toString();
        score = "50";
       // DocumentSnapshot mdocRef = FirebaseFirestore.getInstance().collection(FirebaseAuth.getInstance().getUid().toString());
        Map<String,Object> data  =  new HashMap<>();
        data.put("username",username);
        data.put("password",password);
        data.put("score",score);

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid().toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Info","Data added success");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Info","Data Added Failed");
            }
        })
        ;



    }


}
