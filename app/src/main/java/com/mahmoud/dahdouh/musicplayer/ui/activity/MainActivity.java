package com.mahmoud.dahdouh.musicplayer.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.mahmoud.dahdouh.musicplayer.R;
import com.mahmoud.dahdouh.musicplayer.adapter.SongAdapter;
import com.mahmoud.dahdouh.musicplayer.databinding.ActivityMainBinding;
import com.mahmoud.dahdouh.musicplayer.model.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ThisIsMainActivity";
    private static final int REQUEST_SONG = 5;
    private ActivityMainBinding binding;

    // recycler songs
    private SongAdapter songAdapter;
    private List<SongModel> list;

    private MediaPlayer mediaPlayer;
    private int startTime, finalTime;

    private int swap = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // check storage permission
        checkStoragePermission();
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.shpe_of_you);
        startTime = mediaPlayer.getCurrentPosition();
        finalTime = mediaPlayer.getDuration();

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swap == -1) {
                    play();
                    binding.play.setText("Pause");
                } else {
                    pause();
                    binding.play.setText("Play");
                }
                swap *= -1;
            }
        });

        binding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    stopPlayer();
                }
            }
        });

        binding.volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                mediaPlayer.setVolume(volume, volume);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seek_5_sec = mediaPlayer.getCurrentPosition() + 5000;
                if (seek_5_sec < mediaPlayer.getDuration())
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
            }
        });
        binding.positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mediaPlayer != null) {
                        int currentPosition = (int) ((seekBar.getProgress() / 100f) * mediaPlayer.getDuration());
                        mediaPlayer.seekTo(currentPosition);

                        binding.startLength.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()),
                                TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getCurrentPosition())
                                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getCurrentPosition()))));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });
        binding.endLength.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
    }


    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void play() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.shpe_of_you);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                    binding.play.setText("Play");
                }
            });
        }
        mediaPlayer.start();
    }

    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            binding.positionBar.setProgress(0);
            binding.play.setText("Play");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }


    /// request permission
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // permission is not granted
            // show explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "checkStoragePermission: " + "No permission granted");
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_SONG);
            }

        } else {
            // permission is already granted
            setSongsInRecycler();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SONG) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // the user accept the permission
                setSongsInRecycler();
            } else {
                // The user reject the Permission
                Toast.makeText(this, "You must grant the permission", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onRequestPermissionsResult: You must grant the permission");
            }
        }
    }

    private void setSongsInRecycler() {
        list = new ArrayList<>();
        songAdapter = new SongAdapter();
        ArrayList<HashMap<String, String>> song_list =
                getAllSongs(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (song_list != null) {
            for (int i = 0; i < song_list.size(); i++) {
                list.add(new SongModel(song_list.get(i).get("file_name")));
                String fileName = song_list.get(i).get("file_name");
                String filePath = song_list.get(i).get("file_path");
                //here you will get list of file name and file path that present in your device
                Log.d("xx_asd_asd", " name =" + fileName + " path = " + filePath);
            }
            songAdapter.setList(list);
            binding.recycler.setAdapter(songAdapter);
        }

    }

    private ArrayList<HashMap<String, String>> getAllSongs(String root_path) {

        ArrayList<HashMap<String, String>> song_list = new ArrayList<>();
        try {
            File rootFolder = new File(root_path);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getAllSongs(file.getAbsolutePath()) != null) {
                        song_list.addAll(getAllSongs(file.getAbsolutePath()));
                    } else {
                        break;
                    }

                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> songs = new HashMap<>();
                    songs.put("file_path", file.getAbsolutePath());
                    songs.put("file_name", file.getName());
                    song_list.add(songs);
                }
            }
            return song_list;
        } catch (Exception e) {
            Log.d(TAG, "getAllSongs: " + e.getMessage());
            return null;
        }
    }
}
