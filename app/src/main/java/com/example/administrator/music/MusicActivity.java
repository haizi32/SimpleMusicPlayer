package com.example.administrator.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MusicActivity extends AppCompatActivity {

    private ListView showList;

    private Button prev;

    private Button next;

    private Button pause;

    private int currentPostion;

    private ArrayList<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    private MediaPlayer mediaPlayer = new MediaPlayer();

    public static ArrayList<Music> musicList = new ArrayList<Music>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        showList = (ListView) findViewById(R.id.music_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{Manifest
            .permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            initMediaPlayer();
        }

        if(ContextCompat.checkSelfPermission(MusicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MusicActivity.this, new String[]{Manifest
                    .permission.READ_EXTERNAL_STORAGE}, 2);
        }else {
            initMediaPlayer();
        }
        prev = (Button) findViewById(R.id.prev_button);

        next = (Button) findViewById(R.id.next);

        pause = (Button) findViewById(R.id.play);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPostion -= 1;
                if(currentPostion < 0){
                    //判断是否1是第一首歌
                    Toast.makeText(MusicActivity.this, "已经是第一首歌了", Toast.LENGTH_SHORT).show();
                }else {
                    mediaPlayer.reset();
                    String str = musicList.get(currentPostion).getUrl();
                    String[] dataStr = str.split("/");
                    String fileTruePath = "/sdcard";
                    for (int i = 4; i < dataStr.length; i++) {
                        fileTruePath = fileTruePath + "/" + dataStr[i];
                    }
                    try {
                        mediaPlayer.setDataSource(fileTruePath);
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPostion += 1;
                if(currentPostion >= musicList.size()){
                    //判断是否是最后一首歌
                    Toast.makeText(MusicActivity.this, "已经是最后一首歌了", Toast.LENGTH_SHORT).show();
                }else{
                    mediaPlayer.reset();
                    String str = musicList.get(currentPostion).getUrl();
                    String[] dataStr = str.split("/");
                    String fileTruePath = "/sdcard";
                    for(int i = 4; i < dataStr.length; i++){
                        fileTruePath = fileTruePath + "/" + dataStr[i];
                    }
                    try{
                        mediaPlayer.setDataSource(fileTruePath);
                        mediaPlayer.prepare();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    pause.setBackgroundResource(R.drawable.play);
                }else{
                    mediaPlayer.start();
                    pause.setBackgroundResource(R.drawable.pause);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                }else {
                    Toast.makeText(this, "没有权限授权", Toast.LENGTH_SHORT).show();
                }
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                }else {
                    Toast.makeText(this, "没有权限授权", Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }

    public void initMediaPlayer(){
        initListView();
        showList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mediaPlayer.reset();
                currentPostion = position;
             String str = musicList.get(position).getUrl();
                String[] dataStr = str.split("/");
                String fileTruePath = "/sdcard";
                for(int i=4;i<dataStr.length;i++){
                    fileTruePath = fileTruePath+"/"+dataStr[i];
                }
                try {
                 mediaPlayer.setDataSource(fileTruePath);
                 mediaPlayer.prepare();
             }catch (Exception e){
                 e.printStackTrace();
             }
                mediaPlayer.start();
                pause.setBackgroundResource(R.drawable.pause);
            }
        });
    }

    public ArrayList<Music> getMusicList(){

        ArrayList<Music> myList = new ArrayList<Music>();

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DATA);

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if(size > 1024 * 800) {
                    Music music = new Music();
                    music.setId(id);
                    music.setTitle(title);
                    music.setAlbum(album);
                    music.setAlbumId(albumId);
                    music.setArtist(artist);
                    music.setUrl(url);
                    music.setTime(duration);
                    music.setSize(size);

                    myList.add(music);

                }

                cursor.moveToNext();
            }
            }

        return myList;
    }

    public void initListView(){
        musicList = getMusicList();
        listems = new ArrayList<Map<String, Object>>();
        for(Iterator iterator = musicList.iterator(); iterator.hasNext();){
            Map<String,Object> map = new HashMap<String, Object>();
            Music mp3Info = (Music) iterator.next();
            map.put("title", mp3Info.getTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("album", mp3Info.getAlbum());
            map.put("duration", mp3Info.getTime());
            map.put("size", mp3Info.getSize());
            map.put("url", mp3Info.getUrl());
            map.put("bitmp", R.drawable.musicfile);
            listems.add(map);
        }
        SimpleAdapter msimpleAdapter = new SimpleAdapter(
                this,listems,
                R.layout.music_item,
                new String[]{"bitmap","title","artist","size","duration"},
                new int[]{R.id.video_imageView,R.id.video_title,R.id.video_singer,R.id.video_size,R.id.video_duration }
        );
        showList.setAdapter(msimpleAdapter);
    }

}
