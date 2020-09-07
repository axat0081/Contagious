package com.example.contagious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contagious.FindFriendsActivity;
import com.example.contagious.LoginActivity;
import com.example.contagious.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter mytabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);
        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
              RootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("NotificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        RootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("NotificationKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             String key=snapshot.getValue().toString();
             RootRef.child("NotificationKey").child(key).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful())
                   {
                       Toast.makeText(MainActivity.this, "Successfully registered for notifications", Toast.LENGTH_SHORT).show();
                   }
                   else
                   {
                       String message=task.getException().toString();
                       Toast.makeText(MainActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                   }
                 }
             });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Contagious");
        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(mytabsAccessorAdapter);
        myTabLayout=(TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            Toast.makeText(this, "Login please", Toast.LENGTH_SHORT).show();
            SendUserToLoginActivity();
        }
        else
        {
            VerifyUserExistence();
        }
    }

    private void VerifyUserExistence() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))
                {

                }
                else
                {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_option)
        {
            mAuth.signOut();
            OneSignal.setSubscription(false);
            SendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_create_group_option)
        {
            RequestNewGroup();
        }
        if(item.getItemId()==R.id.main_find_friends_option)
        {
            SendUserToFindFriendsActivity();
        }
        if(item.getItemId()==R.id.play_games)
        {
           SendUserToPlayGamesActivity();
        }
        if(item.getItemId()==R.id.changeTheme)
        {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.this.getClass()));
        }
        if(item.getItemId()==R.id.events)
        {
            SendUserToEventActivity();
        }
        return true;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Team Name");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("Team Name");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupName=groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please enter valid group name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, groupName+" group is created successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent=new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity() {
        Intent settingsIntent=new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void SendUserToFindFriendsActivity() {
        Intent findFriends=new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriends);
    }
    private void SendUserToPlayGamesActivity() {
     Intent playIntent=new Intent(MainActivity.this,PlayGamesActivity.class);
     startActivity(playIntent);
    }
    private void SendUserToEventActivity() {
     Intent eventIntent=new Intent(MainActivity.this,EventActivity.class);
     startActivity(eventIntent);
    }
}
