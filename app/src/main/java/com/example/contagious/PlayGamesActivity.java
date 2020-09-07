package com.example.contagious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlayGamesActivity extends AppCompatActivity {
private TextView play_storeText,gaming_site_Textview;
private Button gaming_site_Button;
private Spinner list;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_games);
        play_storeText=(TextView)findViewById(R.id.playstore_textview);
        list=(Spinner)findViewById(R.id.spinner);
        String items[]={"Horror","Action","Sports","Fantasy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        list.setAdapter(adapter);
         list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               switch (position) {
                   case 0:
                       Intent intent1=new Intent();
                       intent1.setAction(Intent.ACTION_VIEW);
                       intent1.addCategory(Intent.CATEGORY_BROWSABLE);
                       intent1.setData(Uri.parse("http://oceanofgamese.com/"));
                       startActivity(intent1);
                       break;
                   case 1:
                       Intent intent2=new Intent();
                       intent2.setAction(Intent.ACTION_VIEW);
                       intent2.addCategory(Intent.CATEGORY_BROWSABLE);
                       intent2.setData(Uri.parse("http://oceanofga.com/"));
                       startActivity(intent2);
                       break;
                   case 2:
                       Intent intent3=new Intent();
                       intent3.setAction(Intent.ACTION_VIEW);
                       intent3.addCategory(Intent.CATEGORY_BROWSABLE);
                       intent3.setData(Uri.parse("http://ogamese.com/"));
                       startActivity(intent3);
                   case 3:
                       Intent intent4=new Intent();
                       intent4.setAction(Intent.ACTION_VIEW);
                       intent4.addCategory(Intent.CATEGORY_BROWSABLE);
                       intent4.setData(Uri.parse("http://ocegamese.com/"));
                       startActivity(intent4);
                       break;
                   default:
                       Toast.makeText(PlayGamesActivity.this, "Select a genre please", Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {
           }
       });
        gaming_site_Textview=(TextView)findViewById(R.id.gaming_sites_textview);
        gaming_site_Button=(Button)findViewById(R.id.gaming_site_button);
        gaming_site_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent();
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.addCategory(Intent.CATEGORY_BROWSABLE);
                intent1.setData(Uri.parse("http://oceanofgamese.com/"));
                startActivity(intent1);
            }
        });
    }
}