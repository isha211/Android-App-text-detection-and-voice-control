package com.example.myhome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends AppCompatActivity {
    View v;
    private String[] itemsAll;
    private ListView mSongsList;
    MaterialSearchView searchView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        mSongsList=findViewById(R.id.songsList);
        appExternalStoragePermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search songs");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                mSongsList=findViewById(R.id.songsList);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MusicPlayer.this, android.R.layout.simple_list_item_1, itemsAll);
                mSongsList.setAdapter(arrayAdapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty())
                {
                    List<String> lstFound = new ArrayList<String>();

                    for(String item : itemsAll)
                    {
                        if(item.contains(newText))
                        {
                            lstFound.add(item);
                        }

                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MusicPlayer.this, android.R.layout.simple_list_item_1, lstFound);
                    mSongsList.setAdapter(arrayAdapter);

                }
                else
                {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MusicPlayer.this, android.R.layout.simple_list_item_1, itemsAll);
                    mSongsList.setAdapter(arrayAdapter);
                }
                return  true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    private void appExternalStoragePermission() {
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
//                    {
//                        displayAudioSongsName();
//                    }
//
//                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
//                    {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//
//                }).check();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {displayAudioSongsName();}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for(File individualFile: allFiles)
        {
            if(individualFile.isDirectory() && !individualFile.isHidden())
            {
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }
            else
            {
                if(individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma"))
                {
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }

    private void displayAudioSongsName()
    {
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for(int songCounter = 0; songCounter < audioSongs.size(); songCounter++)
        {
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MusicPlayer.this, android.R.layout.simple_list_item_1, itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = mSongsList.getItemAtPosition(i).toString();

                Intent intent = new Intent(MusicPlayer.this, SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }
}
