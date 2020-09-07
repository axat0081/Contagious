package com.example.contagious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagious.MainActivity;
import com.example.contagious.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {
    private Button LoginButton;
    private EditText  UserEmail,UserPassword;
    private TextView NeedNewAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private MediaPlayer mp;
    private DatabaseReference RootRef;
    private String deviceToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        RootRef=FirebaseDatabase.getInstance().getReference();
        InitializeFields();
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter email...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter password...",Toast.LENGTH_SHORT).show();
        }
        else
        {   loadingBar.setTitle("Signing in");
            loadingBar.setMessage("Please wait while signing in");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String currentUserID=mAuth.getCurrentUser().getUid();
                        deviceToken=FirebaseInstanceId.getInstance().getInstanceId().toString();
                        RootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                                  SendUserToMainActivity();
                                  Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                  loadingBar.dismiss();
                              }
                              else
                              {
                                  String message=task.getException().toString();
                                  Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                              }
                            }
                        });
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void InitializeFields() {
        LoginButton=(Button)findViewById(R.id.login_button);
        UserEmail=(EditText)findViewById(R.id.login_email);
        UserPassword=(EditText)findViewById((R.id.login_password));
        NeedNewAccountLink=(TextView)findViewById(R.id.need_new_account_link);
        loadingBar=new ProgressDialog(this);
        mp=MediaPlayer.create(this,R.raw.coin_insert);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}