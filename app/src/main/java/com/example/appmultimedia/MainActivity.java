package com.example.appmultimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appmultimedia.components.audio_trimmer.RingdroidEditActivity;

import java.util.Date;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;
    private String ruta = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isPermissionGranted()){
            programaCamara();
        }

    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }

    public  boolean isPermissionGranted() {
        boolean permiso;
        //si la version es mayor de 6.0
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.RECORD_AUDIO) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WAKE_LOCK) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                permiso = true;
            } else {                                    //si no tiene permiso lo pide
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK}, MY_PERMISSIONS_REQUEST_CAMERA);
                permiso = false;
            }
        }
        else { //si la version es menor de android 6.0
            Log.v("TAG","Permission is granted");
            permiso = true;
        }
        return permiso;
    }


    //Método para comprobar que el usuario a dado permisos y ejecutar el programa
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode==1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();   //si el usuario acepta los permisos
                programaCamara();  //Ejecutamos el programa
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                //si el usuario no nos da permisos no hacemos nada
            }
        }
    }

    public void programaCamara(){
        Button buttonCapturar = findViewById(R.id.buttonCapturar);
        Button buttonReproducir = findViewById(R.id.buttonReproducir);
        Button buttonEditar = findViewById(R.id.buttonEditar);
        Button buttonGrabar = findViewById(R.id.buttonGrabar);


        buttonCapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCamera()){

                    Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                    startActivityForResult(intent, 1);

                }else{
                    Toast.makeText(MainActivity.this, "El dispositivo no tiene cámara", Toast.LENGTH_LONG);
                }
            }
        });

        buttonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoVideo(1);
            }
        });

        buttonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoVideo(2);
            }
        });

        buttonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ruta = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name)
                        + new Date().getTime() + ".wav";
                int color = getResources().getColor(R.color.color_gray_alpha);
                int requestCode = 0;

                AndroidAudioRecorder.with(MainActivity.this)
                        // Required
                        .setFilePath(ruta)
                        .setColor(color)
                        .setRequestCode(requestCode)

                        // Start recording
                        .record();
            }
        });

    }

    public void seleccionarFotoVideo(int requestCode){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*", "audio/*"});
        startActivityForResult(Intent.createChooser(intent, "Choose File"), requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_CANCELED) {
        }
        if ((resultCode == RESULT_OK)) {
            if (requestCode == 0) {
                Toast.makeText(getApplicationContext(), "Guardado en: " + ruta, Toast.LENGTH_SHORT).show();

            }
            else {

                //Procesar el resultado
                Uri uri = data.getData(); //obtener el uri content

                ContentResolver cR = this.getContentResolver();
                String type = cR.getType(uri);
                Intent intent = type.contains("image") ? new Intent(MainActivity.this, ImageActivity.class) : type.contains("audio") && requestCode == 2?
                        new Intent(MainActivity.this, RingdroidEditActivity.class) : new Intent(MainActivity.this, VideoPlayerActivity.class);
                if (requestCode == 1) {
                    intent.putExtra("tipo", "ver");
                } else if (requestCode == 2) {
                    intent.putExtra("tipo", "editar");
                }
                intent.putExtra("uri", uri.toString());
                startActivity(intent);
            }
        }
    }

}
