package com.example.shubhampc.referapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView scoreView;
    private Button referButton;
    private String uid;
    private DocumentReference mdocRef;
    private String score;
    private Object objectScore;
    private Button buttonSignout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        final Map<String,Object> user_data = new HashMap<>();
        scoreView = findViewById(R.id.textViewScore);
        referButton = findViewById(R.id.buttonRefer);
        buttonSignout = findViewById(R.id.buttonSignOut);
        buttonSignout.setOnClickListener(this);
        referButton.setOnClickListener(this);
        uid = FirebaseAuth.getInstance().getUid();
        mdocRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        init();





    }


    void init (){


        Source source = Source.CACHE;
        final Object[] result = new Object[1];
        mdocRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot documentSnapshot = task.getResult();
                    result[0] = documentSnapshot.get("score");
                    scoreView.setText(result[0].toString());
                    Log.i("hamla",result[0].toString());




                }
                else{
                    Toast.makeText(ProfileActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });




    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.buttonRefer){
            sendMessage();
        }
        if(v.getId() == R.id.buttonSignOut){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }
    private void sendMessage(){

        String id = FirebaseAuth.getInstance().getUid();
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello Friend");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, ("http://referapp.com/"+id));
        emailIntent.setType("text/plain");
        startActivity(Intent.createChooser(emailIntent, "Send to friend"));


    }
}
