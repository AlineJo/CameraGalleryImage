package com.example.cameragalleryimage.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.cameragalleryimage.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImageFragment extends Fragment implements ChooseDialogFragment.ChooseDialogInterface {


    private static final int PICK_IMAGE = 100;
    private static final int CAPTURE_IMAGE = 200;
    private Context mContext;
    private Uri mImageUri;
    private ImageView ivImg;

    private TextView tvProgress;
    private ProgressBar progressBar;

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




        return parentView;
    }






    @Override
    public void openGallery() {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose App"), PICK_IMAGE);
    }

    @Override
    public void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if image from Camera
        if (requestCode == CAPTURE_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while capturing the picture!", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                //TODO :: fix data.getData() return null
                mImageUri = data.getData(); //uri
                Log.d("image-uri", mImageUri.toString());
                ivImg.setImageBitmap(capturedImage);

            }

        } else if (requestCode == PICK_IMAGE) {

            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    Uri imgUri = data.getData();//1
                    InputStream imageStream = mContext.getContentResolver().openInputStream(imgUri);//2
                    Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);//3}
                    mImageUri = imgUri;
                    ivImg.setImageBitmap(selectedImageBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
