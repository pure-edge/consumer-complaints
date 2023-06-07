package com.example.logingps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.logingps.models.CrewWorkOrder;
import com.example.logingps.models.CrewWorkOrdersDAO;
import com.example.logingps.models.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WorkOrderDetailsActivity extends AppCompatActivity {

    public static final String WORK_ORDER = "work_order";
    private FirebaseStorage storage;
    private ProgressBar progressBar;
    private Button buttonMarkDone;
    private CrewWorkOrder crewWorkOrder;
    private List<Uri> attachments;
    private boolean uploadToStorageSuccessful = false;
    private boolean workOrderFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_details);

        setTitle(R.string.work_orders_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        buttonMarkDone = findViewById(R.id.buttonMarkDone);
        progressBar = findViewById(R.id.progressBarUpload);

        // attach tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);

        final WorkOrderDetails_PagerAdapter adapter = new WorkOrderDetails_PagerAdapter(getSupportFragmentManager());

        WorkOrderDetailsOverviewFragment overviewFragment = new WorkOrderDetailsOverviewFragment();
        Intent intent = getIntent();
        if (intent != null) {
            crewWorkOrder = (CrewWorkOrder) intent.getSerializableExtra(HomeActivity.WORK_ORDER);
            Bundle bundle = new Bundle();
            bundle.putSerializable(WORK_ORDER, crewWorkOrder);
            overviewFragment.setArguments(bundle);
        }

        final WorkOrderDetailsAttachmentsFragment attachmentsFragment = new WorkOrderDetailsAttachmentsFragment();

        // add your fragments
        adapter.addFragment(overviewFragment, "Overview");
        adapter.addFragment(attachmentsFragment, "Attachments");

        // set adapter on viewpager
        viewPager.setAdapter(adapter);

        buttonMarkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri signaturePhotoUri = attachmentsFragment.getSignaturePhoto();
                attachments = new ArrayList<>(attachmentsFragment.getCapturedPhotoUris());

                if (attachments.size() == 0 && signaturePhotoUri == null) {
                    Toast.makeText(WorkOrderDetailsActivity.this,
                            "Please attach photos and MCO's signature as proof of service.",
                            Toast.LENGTH_LONG).show();
                } else if (attachments.size() == 0) {
                    Toast.makeText(WorkOrderDetailsActivity.this,
                            "Please attach some photos as proof of service.",
                            Toast.LENGTH_LONG).show();
                } else if (signaturePhotoUri == null) {
                    Toast.makeText(WorkOrderDetailsActivity.this,
                            "Please attach MCO's signature as proof of service.",
                            Toast.LENGTH_LONG).show();
                } else {
                    new AlertDialog.Builder(WorkOrderDetailsActivity.this)
                            .setTitle("Confirm work order as finished?")
                            .setMessage("You won't able to view this work order again as this will be uploaded to the main server.")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    buttonMarkDone.setEnabled(false);
                                    buttonMarkDone.setText("Sending attachments...");

                                    // upload the signature and camera photos
                                    attachments.add(signaturePhotoUri);
                                    int uploadIncrement = 0;
                                    uploadToFirebaseStorage(uploadIncrement);
                                    updateCrewWorkOrder(attachmentsFragment.getCapturedPhotoUris(), signaturePhotoUri);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            }
        });
    }

    private void updateCrewWorkOrder(List<Uri> photoUris, Uri signaturePhotoUri) {
        CrewWorkOrdersDAO dao = new CrewWorkOrdersDAO(WorkOrderDetailsActivity.this);
        dao.confirmWorkOrderFinished(crewWorkOrder,
                Calendar.getInstance().getTime(),
                new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                },
                null);
    }

    private void uploadToFirebaseStorage(int uploadIncrement) {
        final int[] i = {uploadIncrement};
        if (uploadIncrement >= attachments.size()) {
            workOrderFinished = true;
            buttonMarkDone.setEnabled(true);
            buttonMarkDone.setText(R.string.mark_as_done);
            Toast.makeText(WorkOrderDetailsActivity.this,
                    "Your confirmation has been uploaded successfully.", Toast.LENGTH_LONG).show();
            // TODO: return to HomeActivity and display new work order updates
            finish();
            return;
        }

        Uri uri = attachments.get(uploadIncrement);
        //final String uid = Firebase.getCurrentUser().getUid();
        StorageReference filePath = storage.getReference().child(crewWorkOrder.getWork_order_id()).child(uri.getLastPathSegment());
        filePath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadToStorageSuccessful = true;
                        i[0]++;
                        uploadToFirebaseStorage(i[0]);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //buttonMarkDone.setEnabled(true);
                        //buttonMarkDone.setText(R.string.mark_as_done);
                        //Toast.makeText(WorkOrderDetailsActivity.this,
                        //        "Failed to upload confirmation. Please check if you have an Internet connection on your phone.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
