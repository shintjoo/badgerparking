package com.cs407.badgerparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class AnnouncementsActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        context = this;

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        instantiateAnnounce();
    }

    /**
     * =======================================================
     * <------------------- ANNOUNCEMENTS ------------------->
     * =======================================================
     */

    private RssParser rssParser;
    private final ArrayList<String> annText = new ArrayList<>();

    public void instantiateAnnounce(){
        ParserRunnable parseRun = new ParserRunnable();
        new Thread(parseRun).start();
    }

    private class ParserRunnable implements Runnable{
        @Override
        public void run() {
            try {
                rssParser = new RssParser("https://media.cityofmadison.com/Mediasite/FileServer/Podcast/ce9107f7b34a47fa82393d9881c83d8817/feed.xml");
                RssParser.Item item;
                for (int i = 0; i < rssParser.getBounds(); i++){
                    item = rssParser.getItem(i);
                    annText.add(String.format("%s\nDate:%s\n", item.getTitle(), item.getPubDate()));
                }
            }
            catch (Exception e) {
                annText.add("no new announcements can be loaded!");
            }
            runOnUiThread(() -> {
                try {
                    Log.i("LOG", "first " + rssParser.getItem(0).getPubDate());
                    Log.i("LOG", "last " + rssParser.getItem(rssParser.getBounds()).getPubDate());
                }
                catch (Exception e){
                    Log.i("LOG", "announcements messed up");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, annText);
                ListView announcementsList = (ListView) findViewById(R.id.announcementsList);
                announcementsList.setAdapter(adapter);
            });
        }
    }
}
