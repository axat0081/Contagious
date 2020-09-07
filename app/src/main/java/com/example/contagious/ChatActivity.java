package com.example.contagious;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.CharacterCodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID,messageReceiverName,messageReceiverImage,messageSenderID;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton,SendFilesButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MeesageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private String currentTime,currentDate,checker="",myUrl="";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;
    private String senderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage=getIntent().getExtras().get("visit_image").toString();
        InitializeControllers();
        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]=new CharSequence[]
                        {
                                "Images",
                        };
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File Type");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                            checker="image";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                    }
                });
                builder.show();
            }
        });
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeControllers() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        //userLastSeen = (TextView) findViewById(R.id.custom_user_last_scene);
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton=(ImageButton)findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);
        messageAdapter=new MeesageAdapter(messagesList);
        userMessagesList=(RecyclerView)findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        loadingBar=new ProgressDialog(this);
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd,yyyy");
        currentDate=currentDateFormat.format(calForDate.getTime());
        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
        currentTime=currentTimeFormat.format(calForTime.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==438 && resultCode==RESULT_OK && data!=null&& data.getData()!=null)
        {
            loadingBar.setTitle("Sending Image");
            loadingBar.setMessage("Please wait while image is sent");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileUri=data.getData();
            if(!checker.equals("image"))
            {
                Toast.makeText(this, "Please select an image to send", Toast.LENGTH_SHORT).show();
            }
            else if(checker.equals("image"))
            {
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef="Messages/"+messageSenderID+"/"+messageReceiverID;
                final String messageReceiverRef="Messages/"+messageReceiverID+"/"+messageSenderID;
                DatabaseReference userMessageKeRef=RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();
                final String messagePushID=userMessageKeRef.getKey();
                final StorageReference filePath=storageReference.child(messagePushID+"."+"jpg");
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task <Uri>task) {
                        if(task.isSuccessful())
                        {
                            Uri downloadUrl=task.getResult();
                            myUrl=downloadUrl.toString();
                            Map messageTextBody=new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("to",messageReceiverID);
                            messageTextBody.put("messageKey",messagePushID);
                            messageTextBody.put("time",currentTime);
                            messageTextBody.put("date",currentDate);
                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(messageSenderRef+"/"+messagePushID,messageTextBody);
                            messageBodyDetails.put(messageReceiverRef+"/"+messagePushID,messageTextBody);
                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "image sent", Toast.LENGTH_SHORT).show();
                                        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             if(snapshot.hasChild("NotificationKey"))
                                             {
                                                 final String key=snapshot.child("NotificationKey").getValue().toString();

                                                 RootRef.child("Users").child(messageSenderID).child("name").addValueEventListener(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                      senderName= Objects.requireNonNull(snapshot.getValue()).toString();
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {

                                                     }
                                                 });
                                                 new SendNotification("You have a message from "+senderName,"New Message",key);
                                             }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else
                                    {   loadingBar.dismiss();
                                        String message=task.getException().toString();
                                        Toast.makeText(ChatActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });
                        }
                    }
                });
            }
            else
            {   loadingBar.dismiss();
                Toast.makeText(this, "No Item selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void SendMessage()
    {
        String messageText=MessageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "write a message here", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef="Messages/"+messageSenderID+"/"+messageReceiverID;
            String messageReceiverRef="Messages/"+messageReceiverID+"/"+messageSenderID;
            DatabaseReference userMessageKeRef=RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();
            String messagePushID=userMessageKeRef.getKey();
            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);
            messageTextBody.put("to",messageReceiverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",currentTime);
            messageTextBody.put("date",currentDate);
            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushID,messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushID,messageTextBody);
            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild("NotificationKey"))
                                {
                                    final String key=snapshot.child("NotificationKey").getValue().toString();

                                    RootRef.child("Users").child(messageSenderID).child("name").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            senderName= Objects.requireNonNull(snapshot.getValue()).toString();
                                            new SendNotification("You have a message from "+senderName,"New Message",key);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        String message=task.getException().toString();
                        Toast.makeText(ChatActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }
}
