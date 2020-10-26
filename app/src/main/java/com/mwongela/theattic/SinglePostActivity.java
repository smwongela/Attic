package com.mwongela.theattic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SinglePostActivity extends AppCompatActivity {


    private ImageView singelImage;
    private TextView singleTitle, singleDesc, postComment;
    String post_key = null;
    private DatabaseReference mDatabase,commentRef,mDatabaseUsers;
    private Button deleteBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    EditText makeComment;
    private FirebaseRecyclerAdapter adapter;
    String currentUserID =null;

    //String currentUserID =null;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        //inflate the tool bar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        singelImage = findViewById(R.id.singleImageview);
        singleTitle = findViewById(R.id.singleTitle);
        singleDesc = findViewById(R.id.singleDesc);

        //initialize recyclerview
        recyclerView = findViewById(R.id.comment_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //Reverse  the layout so as to display the most recent post at the top
       // linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);



        postComment = findViewById(R.id.postComment);
        makeComment = findViewById(R.id.editTextcomment);

        post_key = getIntent().getExtras().getString("PostID");

        //Initialize the database reference/node where you will be storing posts
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_key);
        //Initialize an instance of  Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        //Initialize the instance of the firebase user
        mCurrentUser = mAuth.getCurrentUser();
        //Get currently logged in user
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        deleteBtn = findViewById(R.id.deleteBtn);
        mAuth = FirebaseAuth.getInstance();
        deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();

                Intent mainintent = new Intent(SinglePostActivity.this, MainActivity.class);
                startActivity(mainintent);
            }
        });


        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("postImage").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                singleTitle.setText(post_title);
                singleDesc.setText(post_desc);
                Picasso.with(SinglePostActivity.this).load(post_image).into(singelImage);
                if (mAuth.getCurrentUser().getUid().equals(post_uid)) {

                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    currentUserID=user.getUid();
                } else {
                    Toast.makeText(SinglePostActivity.this,"please login",Toast.LENGTH_SHORT).show();

                }*/
                //Lis
                Toast.makeText(SinglePostActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
                //get the comment from the edit texts
                final String comment = makeComment.getText().toString().trim();
                //get the date and time of the post

                java.util.Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                final String saveCurrentDate = currentDate.format(calendar.getTime());

                java.util.Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                final String saveCurrentTime = currentTime.format(calendar1.getTime());
                // do a check for empty fields
                if (!TextUtils.isEmpty(comment)){
                    final DatabaseReference newComment = commentRef.push();
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            newComment.child("comment").setValue(comment);
                            newComment.child("uid").setValue(mCurrentUser.getUid());
                            newComment.child("time").setValue(saveCurrentTime);
                            newComment.child("date").setValue(saveCurrentDate);
                            newComment.child("profilePhoto").setValue(dataSnapshot.child("profilePhoto").getValue());
                            newComment.child("displayName").setValue(dataSnapshot.child("displayName").getValue());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });
    }
    @Override
    protected void onStart() {
        //
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //if user is logged in populate the Ui With card views
            updateUI(currentUser);
            adapter.startListening();

        }



    }
    private void updateUI(final FirebaseUser currentUser) {
        //create and initialize an instance of Query that retrieves all posts uploaded
        Query query = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_key);
        // Create and initialize and instance of Recycler Options passing in your model class and
        FirebaseRecyclerOptions<CommentModel> options = new FirebaseRecyclerOptions.Builder<CommentModel>().
                setQuery(query, new SnapshotParser<CommentModel>() {
                    @NonNull
                    @Override
                    //Create a snap shot of your model
                    public CommentModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new CommentModel(snapshot.child("displayName").getValue().toString(),
                                snapshot.child("profilePhoto").getValue().toString(),
                                snapshot.child("comment").getValue().toString(),
                                snapshot.child("time").getValue().toString(),
                                snapshot.child("date").getValue().toString());

                    }
                })
                .build();
        // crate a fire base adapter passing in the model, an a View holder
        // Create a  new ViewHolder as a public inner class that extends RecyclerView.Holder, outside the create , start and update the Ui methods.
        //Then implement the methods onCreateViewHolder and onBindViewHolder
        //Complete all the steps in the AtticViewHolder before proceeding to  the methods onCreateViewHolder, and onBindViewHolder
        adapter = new FirebaseRecyclerAdapter<CommentModel, SinglePostActivity.commentModelViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull commentModelViewHolder holder, int i, @NonNull CommentModel model) {
                holder.setUserName(model.getDisplayName());
                holder.setProfilePhoto(getApplicationContext(), model.getProfilePhoto());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setComment(model.getComment());

            }

            @NonNull
            @Override
            public SinglePostActivity.commentModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //inflate the layout where you have the card view items
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
                return new SinglePostActivity.commentModelViewHolder(view);
            }

        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            adapter.stopListening();

        }

    }


    public static  class commentModelViewHolder extends RecyclerView.ViewHolder{
        //Declare the view objects in the card view

        public TextView commenterName;
        public ImageView commenterimage;
        public TextView commentTime;
        public TextView commentDate;
        public TextView  the_comment;

        //Declare a string variable to hold  the user ID of currently logged in user
        String currentUserID;
        //Declare an instance of firebase authentication
        FirebaseAuth mAuth;
        //Declare a database reference where you are saving  the likes
        DatabaseReference likesRef;
        //create constructor matching super
        public commentModelViewHolder(@NonNull View itemView) {
            super(itemView);
            //Initialize the card view item objects

            commenterName = itemView.findViewById(R.id.commenterName);
            commenterimage = itemView.findViewById(R.id.commenterImage);
            commentTime = itemView.findViewById(R.id.commentTime);
            commentDate = itemView.findViewById(R.id.commentDate);
            the_comment = itemView.findViewById(R.id.the_comment);

        }
        // create yos setters, you will use this setter in you onBindViewHolder method
        public void setComment(String comment){

            the_comment.setText(comment);
        }


        public void setUserName(String displayName){

            commenterName.setText(displayName);
        }
        public void  setProfilePhoto(Context context,String profilePhoto){
            Picasso.with(context).load(profilePhoto).into(commenterimage);

        }
        public void setTime(String time) {
            commentTime.setText(time);
        }
        public void setDate(String date) {
            commentDate.setText(date);
        }

        }
    }

