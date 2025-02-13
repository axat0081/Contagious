package com.example.contagious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FindFriendsRecyclerList=(RecyclerView)findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar =(Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(UsersRef,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder findFriendsViewHolder,final int i, @NonNull Contacts contacts) {
                findFriendsViewHolder.userName.setText(contacts.getName());
                findFriendsViewHolder.userStatus.setText(contacts.getStatus());
                Picasso.get().load(contacts.getImage()).placeholder(R.drawable.profile_image).into(findFriendsViewHolder.profileImage);
                findFriendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(i).getKey();
                        Intent profileIntent=new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(view);
                return viewHolder;
            }
        };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {   TextView userName,userStatus;
        CircleImageView profileImage;
        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.users_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);

        }
    }
}
