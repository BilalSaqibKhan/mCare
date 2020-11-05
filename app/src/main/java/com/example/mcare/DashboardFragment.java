package com.example.mcare;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    Button btnEditProfile, btnExplore, btnCaptureImage, btnSelectImage, btnPostImage;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnExplore = view.findViewById(R.id.btnExplore);
        btnCaptureImage = view.findViewById(R.id.btnCapture);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnPostImage = view.findViewById(R.id.btnPostImage);


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setupIntent = new Intent(getActivity(), SetupActivity.class);
                startActivity(setupIntent);
            }
        });


        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fuseIntent = new Intent(getActivity(), ImageFuseActivity.class);
                startActivity(fuseIntent);
            }
        });


        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsIntent = new Intent(getActivity(), NewsActivity.class);
                startActivity(newsIntent);
            }
        });


        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackIntent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(feedbackIntent);
            }
        });


        btnPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postImageIntent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(postImageIntent);
            }
        });

        // Inflate the layout for this fragment
        return view;

    }

}
