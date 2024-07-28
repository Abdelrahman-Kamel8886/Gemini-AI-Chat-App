package com.abdok.geminichat.UI.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.abdok.geminichat.R;
import com.abdok.geminichat.Utils.Consts;
import com.abdok.geminichat.databinding.FragmentSettingsBinding;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentSettingsBinding.bind(view);

        int savedTextSize = getTextSize();
        int progress = getProgressForSize(savedTextSize);
        binding.fontValue.setText(savedTextSize+" pt");
        binding.textPreview.setTextSize(savedTextSize);
        binding.fontSizeSlider.setProgress(progress);
        onClicks();

    }

    private void onClicks(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        binding.fontCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fontCard1.setVisibility(View.GONE);
                binding.fontCard2.setVisibility(View.VISIBLE);
            }
        });

        binding.fontCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fontCard2.setVisibility(View.GONE);
                binding.fontCard1.setVisibility(View.VISIBLE);
            }
        });

        binding.fontSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newSize = Consts.TEXT_SIZES[progress];
                binding.fontValue.setText(newSize+" pt");
                binding.textPreview.setTextSize(newSize);
                saveTextSize(newSize);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    private void shareApp() {
        String appPackageName = requireActivity().getPackageName(); // Get the package name of your app
        String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: " + appLink);
        shareIntent.setType("text/plain");

        // Ensure that there is an app to handle the intent
        if (shareIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share app via"));
        } else {
            // Handle the case where no app can handle the intent
            Toast.makeText(requireContext(), "No app available to share", Toast.LENGTH_SHORT).show();
        }
    }


    private int getProgressForSize(int size) {
        for (int i = 0; i < Consts.TEXT_SIZES.length; i++) {
            if (Consts.TEXT_SIZES[i] == size) {
                return i;
            }
        }
        return 0; // Default progress if size is not found
    }

    public void saveTextSize(int textSize) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("textSize", textSize);
        editor.apply();
    }

    public int getTextSize() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return preferences.getInt("textSize", 14); // Default size is 14
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}