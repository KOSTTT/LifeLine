package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    TextView emptytv;

    private ArrayList<AudioModel> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        rv = findViewById(R.id.song_list);

        if (checkPermission() == false){
            requestPermission();
            return;
        }

        String[] project = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,project,selection,null,null);
        while (cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2) );
            if (new File(songData.getPath()).exists())
                songList.add(songData);
        }
        if (songList.size() == 0){
            emptytv.setVisibility(View.VISIBLE);
        }else {
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new MusicListAdapter(songList, getApplicationContext()));
        }
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this,"Требуется разрешение на чтение, пожалуйста, разрешите в настройках.",Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
    }

    public void onClickSearch(View v) {
        Intent intent = new Intent(this, SearchScreen.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(rv!=null){
            rv.setAdapter(new MusicListAdapter(songList,getApplicationContext()));
        }
    }

}
