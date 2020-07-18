package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private EditText emailEditText,passwordEditText,usernameEditText;
    private ProgressBar progressBar;
    private Button createAcctButton;

    //connection to firestore
    private FirebaseFirestore db= FirebaseFirestore.getInstance();

    private CollectionReference collectionReference= db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);   //in order to hide elevation from above toolbar

        firebaseAuth=FirebaseAuth.getInstance();

        createAcctButton = findViewById(R.id.create_acct_buttton);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText=findViewById(R.id.email_accout);
        passwordEditText=findViewById(R.id.password_account);
        usernameEditText=findViewById(R.id.username_account);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser= firebaseAuth.getCurrentUser();

                if(currentUser!=null)
                {
                    //already logged in

                }
                else{
                    //no user
                }
            }
        };
        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailEditText.getText().toString())
                    && !TextUtils.isEmpty(passwordEditText.getText().toString())
                    && !TextUtils.isEmpty(usernameEditText.getText().toString()))
                {
                    String email=emailEditText.getText().toString().trim();
                    String password= passwordEditText.getText().toString().trim();
                    String username=usernameEditText.getText().toString().trim();

                    createUserEmailAccount(email,password,username);
                }else{
                    Toast.makeText(CreateAccountActivity.this,"Empty Fields not allowed",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username){

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username))
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //we take user to addjournal activity
                                currentUser=firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentuserId= currentUser.getUid();

                                //create usermap so we can create a user in the User collection
                                Map<String ,String>userobj=new HashMap<>();
                                userobj.put("userId",currentuserId);
                                userobj.put("username",username);

                                //save to firestore
                                collectionReference.add(userobj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        documentReference.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(Objects.requireNonNull(task.getResult()).exists()){
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            String name=task.getResult().getString("username");

                                                            JournalApi journalApi= JournalApi.getInstance();   // global api
                                                            journalApi.setUserId(currentuserId);
                                                            journalApi.setUsername(name);

                                                            Intent intent=new Intent(CreateAccountActivity.this,PostJournalActivity.class);
                                                            intent.putExtra("username",name);
                                                            intent.putExtra("userId",currentuserId);
                                                            startActivity(intent);
                                                            finish();

                                                        }else{

                                                        }
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                            else {
                                //something went wrong
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
