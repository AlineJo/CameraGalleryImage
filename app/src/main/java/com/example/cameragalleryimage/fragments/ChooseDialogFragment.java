package com.example.cameragalleryimage.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.cameragalleryimage.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseDialogFragment extends DialogFragment {


    private ChooseDialogInterface mListener;

    public ChooseDialogFragment() {
        // Required empty public constructor
    }

    //initialize listener
    public void setChooseDialogListener(ChooseDialogInterface listener) {
        mListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_choose_dialog, container, false);

        Button btnOpenCamera = parentView.findViewById(R.id.btn_camera);
        Button btnOpenGallery = parentView.findViewById(R.id.btn_gallery);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCameraButtonClick();
                }
                dismiss();
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onGalleryButtonClick();
                }
                dismiss();
            }
        });

        return parentView;
    }

    public interface ChooseDialogInterface {

        void onGalleryButtonClick();

        void onCameraButtonClick();

    }

}
