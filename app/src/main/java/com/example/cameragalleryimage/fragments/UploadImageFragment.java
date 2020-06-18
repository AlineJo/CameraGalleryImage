package com.example.cameragalleryimage.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.cameragalleryimage.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImageFragment extends Fragment implements ChooseDialogFragment.ChooseDialogInterface {


    private static final int PICK_IMAGE = 100;
    private static final int CAPTURE_IMAGE = 200;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    private Context mContext;
    private Uri mImageUri;
    private ImageView ivImg;

    private TextView tvProgress;
    private ProgressBar progressBar;
    private String mImagePath;

    public UploadImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_upload_image, container, false);

        ivImg = parentView.findViewById(R.id.iv_img);
        tvProgress = parentView.findViewById(R.id.tv_progress);
        progressBar = parentView.findViewById(R.id.progressBar);


        Button btnChoose = parentView.findViewById(R.id.btn_choose);
        Button btnUpload = parentView.findViewById(R.id.btn_upload);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseDialogFragment dialog = new ChooseDialogFragment();
                dialog.setChooseDialogListener(UploadImageFragment.this);
                dialog.show(getChildFragmentManager(), ChooseDialogFragment.class.getSimpleName());
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUri == null) {
                    Toast.makeText(mContext, "Please take an image", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Image URI Found !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return parentView;
    }


    @Override
    public void onGalleryButtonClick() {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose App"), PICK_IMAGE);
    }

    @Override
    public void onCameraButtonClick() {
        if (isPermissionGranted()) {
            openCamera();
        } else {
            showRunTimePermission();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("capture_error", ex.toString());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageUri = FileProvider.getUriForFile(mContext,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAPTURE_IMAGE) {//img from camera
                Bundle extras = data.getExtras();// TODO this Produce null pointer exception!
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivImg.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_IMAGE) {// img from gallery
                try {
                    Uri imgUri = data.getData();
                    InputStream imageStream = mContext.getContentResolver().openInputStream(imgUri);//2
                    Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);//3}
                    mImageUri = imgUri;
                    ivImg.setImageBitmap(selectedImageBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();

        }


    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);// mContext.getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mImagePath = image.getAbsolutePath();
        return image;
    }


    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void showRunTimePermission() {
        // Permission is not Granted !
        // we should Request the Permission!
        // put all permissions you need in this Screen into string array
        String[] permissionsArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //here we requet the permission
        requestPermissions(permissionsArray, STORAGE_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // user grants the Permission!
            // you can call the function to write/read to storage here!
            openCamera();

        } else {
            // user didn't grant the Permission we need
            Toast.makeText(mContext, "Please Grant the Permission To use this Feature!", Toast.LENGTH_LONG).show();
        }
    }


}
