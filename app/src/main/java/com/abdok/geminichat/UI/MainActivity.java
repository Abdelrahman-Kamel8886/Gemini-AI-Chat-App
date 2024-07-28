package com.abdok.geminichat.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.R;
import com.abdok.geminichat.UI.History.HistoryFragment;
import com.abdok.geminichat.UI.History.HistoryViewModel;
import com.abdok.geminichat.UI.Home.HomeFragment;
import com.abdok.geminichat.UI.Settings.SettingsFragment;
import com.abdok.geminichat.Utils.SharedModel;
import com.abdok.geminichat.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding ;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), new HomeFragment()).commit();
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
        binding = null;
    }


}