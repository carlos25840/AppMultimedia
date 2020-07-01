package com.example.appmultimedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.deep.videotrimmer.utils.FileUtils;
import com.example.appmultimedia.components.video_trimmer.VideoTrimmerActivity;


public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Intent intent = getIntent();
        String tipo = intent.getStringExtra("tipo");
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        video = findViewById(R.id.videoView1);
        String ruta = FileUtils.getPath(this, uri);
        if(tipo.equals("editar")) {
            video.setVisibility(View.GONE);
            startActivityForResult(new Intent(VideoPlayerActivity.this, VideoTrimmerActivity.class).putExtra("EXTRA_PATH", ruta), 1);
        }
        else{
            mostrarVideo(uri);
        }
    }

    public void mostrarVideo(Uri uri){

        video.setVisibility(View.VISIBLE);
        video.setVideoURI(uri);
        video.setMediaController(new MediaController(this));
        video.requestFocus();
        video.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String videoPath = data.getExtras().getString("INTENT_VIDEO_FILE");
                    Toast.makeText(VideoPlayerActivity.this, "Video stored at " + videoPath, Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse(data.getExtras().getString("uri"));
                    mostrarVideo(uri);
                }
            }
            else{
                finish();
            }
        }
    }
}
