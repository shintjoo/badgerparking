package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AnnouncementsActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        context = this;

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        instantiateAnnounce();
    }

    /**
     * =======================================================
     * <------------------- ANNOUNCEMENTS ------------------->
     * =======================================================
     */

    private RssParser rssParser;
    private AnnouncementsActivity.ParserRunnable parseRun;
    private ArrayList<String> annText = new ArrayList<>();
    private Button toAnnButton;

    public void instantiateAnnounce(){
        parseRun = new AnnouncementsActivity.ParserRunnable();
        new Thread(parseRun).start();
    }

    private class ParserRunnable implements Runnable{
        @Override
        public void run() {
            try {
                rssParser = new RssParser("https://www.cityofmadison.com/feed/news/traffic-engineering");
                RssParser.Item item;
                for (int i = 0; i < rssParser.getBounds(); i++){
                    item = rssParser.getItem(i);
                    annText.add(String.format("%s\nDate:%s\n", item.getTitle(), item.getPubDate()));
                }
            }
            catch (Exception e) {
                annText.add("no new announcements can be loaded!");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, annText);
                    ListView announcementsList = (ListView) findViewById(R.id.announcementsList);
                    announcementsList.setAdapter(adapter);

                }
            });
        }
    }
}