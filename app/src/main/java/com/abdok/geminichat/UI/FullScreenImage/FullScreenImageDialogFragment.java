package com.abdok.geminichat.UI.FullScreenImage;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.abdok.geminichat.R;
import com.abdok.geminichat.Utils.UriTypeConverter;

@SuppressLint({"MissingInflatedId", "LocalSuppress"})
public class FullScreenImageDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE_RES_ID = "image_res_id";

    public static FullScreenImageDialogFragment newInstance(String image) {
        FullScreenImageDialogFragment fragment = new FullScreenImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_RES_ID, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_full_screen_image, container, false);

         ImageView imageView = view.findViewById(R.id.full_screen_image_view);
        if (getArguments() != null) {
            Bitmap bitmap = UriTypeConverter.loadImageFromStorage(getArguments().getString(ARG_IMAGE_RES_ID));
            imageView.setImageBitmap(bitmap);
        }

        imageView.setOnClickListener(v -> dismiss()); // Dismiss the dialog on image click

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
