package no.ntnu.mobapp20g6.app1.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoProvider {
    public String currentPhotoPath;
    public MutableLiveData<String>currentPhotoUriLiveData;
    private Context context;
    private File createdImageFile;

    public PhotoProvider(Context ctx) {
        this.context = ctx;
        this.currentPhotoUriLiveData = new MutableLiveData<>();
    }
    public File createImageFile() throws IOException {
        if (context == null) {
            return null;
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println("bilderr" + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        createdImageFile = image;

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public boolean deleteCurrentImageFile() {
        if (createdImageFile != null) {
            if(createdImageFile.delete()) {
                createdImageFile = null;
                currentPhotoPath = null;
                this.currentPhotoUriLiveData.setValue(null);
                System.out.println("Photopath: deleted");
                return  true;
            } else {
                System.out.println("Photopath: tried to delete, but failed");
            }
        }
        return  false;
    }


    public void dispatchTakePictureIntent(Integer requestCode, Fragment fragment) {
        if (requestCode != null && context != null && fragment != null) {
            Integer REQUEST_IMAGE_CAPTURE = requestCode;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context,
                            "no.ntnu.mobapp20g6.app1.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    currentPhotoUriLiveData.setValue(currentPhotoPath);
                    System.out.println("foto uri er: " + photoURI.getPath());
                    System.out.println("Sent image to fragment " + fragment.toString());
                    fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }
}
