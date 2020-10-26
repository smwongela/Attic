package com.mwongela.theattic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //Declare instances of the views
    private Button registerBtn;
    private EditText emailField, usernameField, passwordField;
    private TextView loginTxtView;
    //Declare an instance of Firebase Authentication
    private FirebaseAuth mAuth;
    //Declare an instance of FireBase Database
    private FirebaseDatabase database;
    //Declare an instance of FireBase Database Reference;
    // A Database reference is a node in our database, e.g the node users to store user details
    private DatabaseReference userDetailsReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //inflate the tool bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Initialize the views
        loginTxtView = findViewById(R.id.loginTxtView);
        registerBtn = findViewById(R.id.registerBtn);
        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);

        // Initialize an Instance of Firebase Authentication  by calling the getInstance() method
        mAuth = FirebaseAuth.getInstance();
        // Initialize an Instance of Firebase Database by calling the getInstance() method
        database = FirebaseDatabase.getInstance();
        //Initialize an Instance of Firebase Database reference by
        // calling the database instance, getting a reference  using the get reference() method on the database,
        // and creating a new child node, in our case "Users" where we will store details of registered users
        userDetailsReference = database.getReference().child("Users");

        // For already registered user we want to redirect them to the login page directly without registering them again
        //For this function , setOnClickListener on the textView  object of redirecting user to login Activity
        //Create a Login Activity first using the empty activity template
        //Implement an intent to launch this
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        //set an on click listener on your register button, on clicking this button we want to get:
        // the username, email , password the user enters on edit text fields
        // further we to open a new activity called profile activity where will allow our users to set a custom display name and their profile image


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a toast
                Toast.makeText(RegisterActivity.this, "LOADING...", Toast.LENGTH_LONG).show();
                //get the username entered
                final String username = usernameField.getText().toString().trim();
                //get the email entered
                final String email = emailField.getText().toString().trim();
                //get the password entered
                final String password = passwordField.getText().toString().trim();
                //Validate to ensure that the user has entered email and username
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    //Create a new createAccount method that takes in an email address and password, validates them,
                    // and then creates a new user with the [createUserWithEmailAndPassword] using the instance of
                    // Firebase Authentication (mAuth) We create and calling the method .addOnCompleteListener

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //override the onComplete method where we’ll store this registered user on our database with respect to their unique id’s.

                            // Create a string variable to get the  user id of currently  registered user
                            String user_id = mAuth.getCurrentUser().getUid();
                            //create a child node database reference to attach the user_id to the users node
                            DatabaseReference current_user_db = userDetailsReference.child(user_id);
                            // set the Username and Image on the users' unique path (current_users_db).
                            current_user_db.child("Username").setValue(username);
                            current_user_db.child("Image").setValue("Default");
                            // make a Toast to show the user that they’ve been successfully registered and then
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            //Create Profile Activity using empty activity template
                            //  launch the Profile activity for user to set their preferred profile
                            Intent profIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            profIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(profIntent);

                        }
                    });
                } else {

                    Toast.makeText(RegisterActivity.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

