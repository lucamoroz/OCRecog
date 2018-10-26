package com.example.luca.ocrecog;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;


    private Button takePictureButton;
    private TextView showTextView;
    private ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        showTextView = (TextView) findViewById(R.id.showTextView);
        takePictureButton = (Button) findViewById(R.id.takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();
            }
        });

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String pathToImageInternalStorage = sharedPref.getString(getString(R.string.savedPhotoPathKey), "");

        if(pathToImageInternalStorage != "")
            loadImageFromStorage(pathToImageInternalStorage);
    }


    //Launch an intent to take a picture
    //Stores the image to external memory and save path in mCurrentPhotoPath
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Error creating file.", Toast.LENGTH_SHORT);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                photoFile.delete();
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Show image on activity and save it in internal storage as bitmap, modifies photoPath in shared preference
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);


            Matrix matrix = new Matrix();
            matrix.postRotate(90); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);

            photoImageView.setImageBitmap(bitmap);

            String imagePathInternalStorage = saveToInternalStorage(bitmap);
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.savedPhotoPathKey), imagePathInternalStorage);
            editor.apply();


            
        }

    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File path = new File(directory,getString(R.string.photoFileName));

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, getString(R.string.photoFileName));
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            photoImageView.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), "No photo data found.", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }

    }

}
