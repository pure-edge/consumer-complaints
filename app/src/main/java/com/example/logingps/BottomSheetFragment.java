package com.example.logingps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.logingps.models.Firebase;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Created by gurleensethi on 15/01/18.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private Context context;

    private Button buttonAbout;
    private Button buttonLogout;
    private View.OnClickListener onClickListener;
    private TextView textViewName;

    public BottomSheetFragment(Context context, View.OnClickListener onClickListener) {
        super();
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet, null);
        dialog.setContentView(view);

        textViewName = view.findViewById(R.id.textViewName);
        buttonAbout = view.findViewById(R.id.buttonAbout);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        textViewName.setText(Firebase.getCurrentUser().getDisplayName());
        buttonLogout.setOnClickListener(onClickListener);
    }
}
