package com.example.logingps;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class WorkOrderDetailsAttachmentsFragment extends Fragment {
    private static final int REQUEST_SETINGS = 0;
    private final int REQUEST_CAPTURE_CAMERA_IMAGE = 1;
    private final int REQUEST_ADD_EDIT_SIGNATURE = 2;
    private ImageView imageViewSignature;
    private PhotosAdapter adapter;
    private String imageFilePath;
    private Uri signaturePhotoUri;
    private Uri cameraPhotoURI;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.work_order_attachments, container, false);

        Button buttonAddPhotos = view.findViewById(R.id.buttonAddPhotos);
        buttonAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        Button buttonSignature = view.findViewById(R.id.buttonSignature);
        buttonSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkOrderDetailsAttachmentsFragment.this.getActivity(), SignatureActivity.class);
                startActivityForResult(intent, REQUEST_ADD_EDIT_SIGNATURE);
            }
        });

        imageViewSignature = view.findViewById(R.id.imageViewSignature);

        GridView gridViewPhotos = view.findViewById(R.id.gridviewPhotos);
        adapter = new PhotosAdapter(getActivity());
        gridViewPhotos.setAdapter(adapter);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAPTURE_CAMERA_IMAGE:
                if (resultCode == Activity.RESULT_OK) {

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(WorkOrderDetailsAttachmentsFragment.this.getActivity(),
                                    new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            ExifInterface exif = null;
                                            try {
                                                //Log.d("E-BILECO", "cameraPhotoURI.getPath(): " + cameraPhotoURI);
                                                exif = new ExifInterface(imageFilePath);
                                                exif.setGpsInfo(location);
                                                exif.saveAttributes();
                                                adapter.addItem(cameraPhotoURI);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            .addOnFailureListener(WorkOrderDetailsAttachmentsFragment.this.getActivity(),
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(WorkOrderDetailsAttachmentsFragment.this.getActivity(),
                                                    "Failed to add new photo. Please capture another photo after the phone has captured its last GPS location.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                }
                break;
            case REQUEST_ADD_EDIT_SIGNATURE:
                if (resultCode == Activity.RESULT_OK) {
                    signaturePhotoUri = data.getParcelableExtra(SignatureActivity.EXTRA_SIGNATURE);
                    Glide.with(this).asBitmap().load(signaturePhotoUri).into(imageViewSignature);
                }
                break;
        }
    }

    private void requestCameraPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                // TODO: add geolocation info to this file
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                cameraPhotoURI = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoURI);
                startActivityForResult(intent, REQUEST_CAPTURE_CAMERA_IMAGE);
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private File createImageFile() throws IOException {
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_SETINGS);
    }

    public List<Uri> getCapturedPhotoUris() {
        return adapter.getCapturedPhotoUris();
    }

    public Uri getSignaturePhoto() {
        return signaturePhotoUri;
    }
}
