package com.example.logingps;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.logingps.models.CrewWorkOrder;

import java.text.SimpleDateFormat;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class WorkOrderDetailsOverviewFragment extends Fragment {
    private CrewWorkOrder crewWorkOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.work_order_overview, container, false);

        // TODO: get work from intent
        Bundle bundle = getArguments();
        if (bundle != null) {
            crewWorkOrder = (CrewWorkOrder) bundle.getSerializable(WorkOrderDetailsActivity.WORK_ORDER);
        }

        Button btnDirection = view.findViewById(R.id.buttonLocate);
        Button btnCall = view.findViewById(R.id.buttonCall);
        Button btnMessage = view.findViewById(R.id.buttonMessage);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewName = view.findViewById(R.id.textViewMCOName);
        TextView textViewAccountNumber = view.findViewById(R.id.textViewAccountNumber);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewContactNumber = view.findViewById(R.id.textViewContact);
        TextView textViewOtherDetails = view.findViewById(R.id.textViewOtherDetails);

        textViewDescription.setText(crewWorkOrder.getDescription());
        textViewAccountNumber.setText(crewWorkOrder.getMember_consumer_owner().getAccountNumber());
        textViewName.setText(crewWorkOrder.getMember_consumer_owner().getName());
        textViewContactNumber.setText(crewWorkOrder.getMember_consumer_owner().getContactNumber());
        textViewAddress.setText(crewWorkOrder.getMember_consumer_owner().getAddress());

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(crewWorkOrder.getMemberConsumerOwner().getAddress()));
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(crewWorkOrder.getMember_consumer_owner().getAddress()));
                Intent i = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + crewWorkOrder.getMember_consumer_owner().getContactNumber());
                Intent i = new Intent(Intent.ACTION_DIAL, u);
                startActivity(i);
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                //smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setData(Uri.parse("smsto:" + crewWorkOrder.getMember_consumer_owner().getContactNumber()));
                startActivity(smsIntent);
            }
        });

        String otherDetails = "Assigned on ";
        String dateTime = new SimpleDateFormat("MMM dd, yyyy @hh:mm a").format(crewWorkOrder.getDate_assigned());
        String otherDetails2 = " by ";
        String createdBy = crewWorkOrder.getAssigned_by();
        String fullText = otherDetails + dateTime + otherDetails2 + createdBy;

        SpannableString spannableString = new SpannableString(fullText);
        spannableString.setSpan(new StyleSpan(ITALIC), 0, otherDetails.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(BOLD), fullText.indexOf(dateTime), fullText.indexOf(otherDetails2), SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(ITALIC), fullText.indexOf(otherDetails2), fullText.indexOf(createdBy), SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), fullText.indexOf(createdBy), fullText.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewOtherDetails.setText(spannableString);

        return view;
    }
}
