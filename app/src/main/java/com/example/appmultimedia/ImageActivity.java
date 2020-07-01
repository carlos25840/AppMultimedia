package com.example.appmultimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Rectangle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView = findViewById(R.id.imagen);

        Intent intent = getIntent();
        String tipo = intent.getStringExtra("tipo");
        uri = Uri.parse(intent.getStringExtra("uri"));
        imageView.setImageURI(uri);

        if(tipo.equals("editar")) {
            editar();
        }

    }

    public void editar(){
        Button btnCrop = findViewById(R.id.btnCrop);
        Button btnSave = findViewById(R.id.btnSave);
        btnCrop.setVisibility(View.VISIBLE);

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity(uri).start(ImageActivity.this);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSave();
            }
        });



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.imagen);
        Button btnSave = findViewById(R.id.btnSave);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                imageView.setImageURI(uri);
                btnSave.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_LONG).show();
            }
        }
    }

    void saveFile(String nombre)
    {

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            MediaStore.Images.Media.insertImage(getContentResolver(), bm, nombre , nombre + "copy");

            Toast.makeText(getApplicationContext(), "Image saved" ,Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_LONG).show();
        }
    }

    public void showDialogSave(){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ImageActivity.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_save, null);


        builder.setTitle(Html.fromHtml("<b> SAVE AS </b>"));
        builder.setView(v);

        final EditText editTextName = v.findViewById(R.id.editTextName);

        builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveFile(editTextName.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

}


