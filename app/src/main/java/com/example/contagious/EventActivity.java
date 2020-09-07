package com.example.contagious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static android.view.View.GONE;

public class EventActivity extends AppCompatActivity {
    private TextView txtDay, txtHour, txtMinute, txtSecond,txtDetails,txtTitle;
    private TextView txtDay1, txtHour1, txtMinute1, txtSecond1,txtDetails1,txtTitle1;
    private Button tvText,tvText1;
    private int flag1=1,flag11=1;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        txtDay = (TextView) findViewById(R.id.txtDay);
        txtHour = (TextView) findViewById(R.id.txtHour);
        txtMinute = (TextView) findViewById(R.id.txtMinute);
        txtSecond = (TextView) findViewById(R.id.txtSecond);
        tvText = (Button) findViewById(R.id.textViewheader2);
        txtDetails=(TextView)findViewById(R.id.event_details);
        txtTitle=(TextView)findViewById(R.id.textViewheader1);
        txtDay1 = (TextView) findViewById(R.id.txtDay1);
        txtHour1 = (TextView) findViewById(R.id.txtHour1);
        txtMinute1 = (TextView) findViewById(R.id.txtMinute1);
        txtSecond1 = (TextView) findViewById(R.id.txtSecond1);
        tvText1 = (Button) findViewById(R.id.textViewheader21);
        txtTitle1=(TextView)findViewById(R.id.textViewheader11);
        RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("EventNotifications").child("Event1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("year"))
                {
                    flag1=0; tvText.setClickable(false); tvText.setText("Upcoming Event");
                    String y=dataSnapshot.child("year").getValue().toString();
                    String m=dataSnapshot.child("month").getValue().toString();
                    String d=dataSnapshot.child("day").getValue().toString();
                    String h=dataSnapshot.child("hours").getValue().toString();
                    String mi=dataSnapshot.child("minutes").getValue().toString();
                    String sec=dataSnapshot.child("seconds").getValue().toString();
                    String t=dataSnapshot.child("title").getValue().toString();
                    txtTitle.setText(t);
                    countDownStart(y,m,d,h,mi,sec,t);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        RootRef.child("EventNotifications").child("Event2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("year"))
                {
                    flag11=0; tvText1.setClickable(false); tvText1.setText("Upcoming Event");
                    String y=dataSnapshot.child("year").getValue().toString();
                    String m=dataSnapshot.child("month").getValue().toString();
                    String d=dataSnapshot.child("day").getValue().toString();
                    String h=dataSnapshot.child("hours").getValue().toString();
                    String mi=dataSnapshot.child("minutes").getValue().toString();
                    String sec=dataSnapshot.child("seconds").getValue().toString();
                    String t=dataSnapshot.child("title").getValue().toString();
                    txtTitle1.setText(t);
                    countDownStart1(y,m,d,h,mi,sec,t);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (flag1>0)
        {       tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setTitle("Create an Event");
                LinearLayout layout = new LinearLayout(EventActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText year = new EditText(EventActivity.this);
                year.setHint("Enter year in YYYY format");
                final EditText month = new EditText(EventActivity.this);
                month.setHint("Enter month in MM format");
                final EditText day = new EditText(EventActivity.this);
                day.setHint("Enter day in DD format");
                final EditText hour = new EditText(EventActivity.this);
                hour.setHint("Enter hour in hh format");
                final EditText min = new EditText(EventActivity.this);
                min.setHint("Enter minutes in  mm format");
                final EditText sec = new EditText(EventActivity.this);
                sec.setHint("Enter seconds in sec format");
                final EditText title=new EditText(EventActivity.this);
                title.setHint("Enter Event title, no more than 20 characters");
                title.setMaxLines(1);
                layout.addView(year);
                layout.addView(month);
                layout.addView(day);
                layout.addView(hour);
                layout.addView(min);
                layout.addView(sec);
                layout.addView(title);
                builder.setView(layout);
                builder.setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String y=year.getText().toString();
                        String m=month.getText().toString();
                        String d=day.getText().toString();
                        String h=hour.getText().toString();
                        String mi=min.getText().toString();
                        String se=sec.getText().toString();
                        String t=title.getText().toString();
                        txtTitle.setText(t);
                        countDownStart(y,m,d,h,mi,se,t); flag1=0;
                        //countDownStart(s);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        }
        if (flag11>0)
        {       tvText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setTitle("Create an Event");
                LinearLayout layout = new LinearLayout(EventActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText year = new EditText(EventActivity.this);
                year.setHint("Enter year in YYYY format");
                final EditText month = new EditText(EventActivity.this);
                month.setHint("Enter month in MM format");
                final EditText day = new EditText(EventActivity.this);
                day.setHint("Enter day in DD format");
                final EditText hour = new EditText(EventActivity.this);
                hour.setHint("Enter hour in hh format");
                final EditText min = new EditText(EventActivity.this);
                min.setHint("Enter minutes in  mm format");
                final EditText sec = new EditText(EventActivity.this);
                sec.setHint("Enter seconds in sec format");
                final EditText title=new EditText(EventActivity.this);
                title.setHint("Enter Event title, no more than 20 characters");
                title.setMaxLines(1);
                layout.addView(year);
                layout.addView(month);
                layout.addView(day);
                layout.addView(hour);
                layout.addView(min);
                layout.addView(sec);
                layout.addView(title);
                builder.setView(layout);
                builder.setPositiveButton("Create Event", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String y=year.getText().toString();
                        String m=month.getText().toString();
                        String d=day.getText().toString();
                        String h=hour.getText().toString();
                        String mi=min.getText().toString();
                        String se=sec.getText().toString();
                        String t=title.getText().toString();
                        txtTitle1.setText(t);
                        countDownStart1(y,m,d,h,mi,se,t); flag11=0;
                        //countDownStart(s);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        }
    }

    private void countDownStart(final String y,final String m,final String d,final String h,final String mi,final String sec,final String t) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("year",y);
        hashMap.put("month",m);
        hashMap.put("day",d);
        hashMap.put("hours",h);
        hashMap.put("minutes",mi);
        hashMap.put("seconds",sec);
        hashMap.put("title",t);
        RootRef.child("EventNotifications").child("Event1").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                }
                else
                {
                    String message=task.getException().toString();
                    Toast.makeText(EventActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        String s= y.concat("-");
        s=s.concat(m);
        s=s.concat("-");
        s=s.concat(d);
        s=s.concat(" ");
        s=s.concat(h);
        s=s.concat(":");
        s=s.concat(mi);
        s=s.concat(":");
        s=s.concat(sec);
        final String s1=s;
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long milliseconds=0; Date endDate;
        try {
            endDate = formatter.parse(s1);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTime=System.currentTimeMillis();
        milliseconds-=startTime;
        new CountDownTimer(milliseconds,1000){
            @Override
            public void onTick(long millisUntilFinished) {


                Long serverUptimeSeconds=(millisUntilFinished)/1000;

                String daysLeft=String.format("%d", serverUptimeSeconds / 86400);
                txtDay.setText(daysLeft);

                String hoursLeft=String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                txtHour.setText(hoursLeft);

                String minutesLeft=String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                txtMinute.setText(minutesLeft);

                String secondsLeft=String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                txtSecond.setText(secondsLeft);

            }

            @Override
            public void onFinish() {
                textViewGone(t);
                tvText.setClickable(true);
                tvText.setText("Create an Event!");
                txtTitle.setText("No Event Yet");
                txtDetails.setVisibility(View.GONE);
            }
        }.start();
    }
    private void countDownStart1(String y, String m, String d, String h, String mi, String sec,final String t) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("year",y);
        hashMap.put("month",m);
        hashMap.put("day",d);
        hashMap.put("hours",h);
        hashMap.put("minutes",mi);
        hashMap.put("seconds",sec);
        hashMap.put("title",t);
        RootRef.child("EventNotifications").child("Event2").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                }
                else
                {
                    String message=task.getException().toString();
                    Toast.makeText(EventActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        String s= y.concat("-");
        s=s.concat(m);
        s=s.concat("-");
        s=s.concat(d);
        s=s.concat(" ");
        s=s.concat(h);
        s=s.concat(":");
        s=s.concat(mi);
        s=s.concat(":");
        s=s.concat(sec);
        final String s1=s;
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long milliseconds=0; Date endDate;
        try {
            endDate = formatter.parse(s1);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startTime=System.currentTimeMillis();
        milliseconds-=startTime;
        new CountDownTimer(milliseconds,1000){
            @Override
            public void onTick(long millisUntilFinished) {


                Long serverUptimeSeconds=(millisUntilFinished)/1000;

                String daysLeft=String.format("%d", serverUptimeSeconds / 86400);
                txtDay1.setText(daysLeft);

                String hoursLeft=String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                txtHour1.setText(hoursLeft);

                String minutesLeft=String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                txtMinute1.setText(minutesLeft);

                String secondsLeft=String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                txtSecond1.setText(secondsLeft);

            }

            @Override
            public void onFinish() {
                textViewGone1(t);
                tvText1.setClickable(true);
                tvText1.setText("Create an Event!");
                txtTitle1.setText("No Event Yet");
            }
        }.start();
    }

    private void textViewGone(final String title) {
        DatabaseReference ref=RootRef.child("EventNotifications").child("Event1");
        ref.child("year").removeValue();
        ref.child("month").removeValue();
        ref.child("day").removeValue();
        ref.child("hours").removeValue();
        ref.child("minutes").removeValue();
        ref.child("seconds").removeValue();
        ref.child("title").removeValue();
        RootRef.child("NotificationKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id=(dataSnapshot.getKey());
                    new SendNotification("Event-> "+title+" has begun","Event Notification",id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        flag1=1;
    }
    private void textViewGone1(final String title) {
        DatabaseReference ref=RootRef.child("EventNotifications").child("Event2");
        ref.child("year").removeValue();
        ref.child("month").removeValue();
        ref.child("day").removeValue();
        ref.child("hours").removeValue();
        ref.child("minutes").removeValue();
        ref.child("seconds").removeValue();
        ref.child("title").removeValue();
        RootRef.child("NotificationKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id=(dataSnapshot.getKey());
                    new SendNotification("Event-> "+title+" has begun","Event Notification",id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        flag11=1;
    }
}