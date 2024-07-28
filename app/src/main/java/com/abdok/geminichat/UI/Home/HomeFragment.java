package com.abdok.geminichat.UI.Home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.abdok.geminichat.Adapters.AdapterRecyclerChat;
import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Models.ModelChat;
import com.abdok.geminichat.R;
import com.abdok.geminichat.UI.FullScreenImage.FullScreenImageDialogFragment;
import com.abdok.geminichat.UI.History.HistoryFragment;
import com.abdok.geminichat.UI.Settings.SettingsFragment;
import com.abdok.geminichat.Utils.Consts;
import com.abdok.geminichat.Utils.NetworkUtil;
import com.abdok.geminichat.Utils.SharedModel;
import com.abdok.geminichat.Utils.UriTypeConverter;
import com.abdok.geminichat.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {


    FragmentHomeBinding binding;
    HomeViewModel viewModel;
    AdapterRecyclerChat adapter;

    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    Uri resultUri;
    int i = 2;
    int textSize = 14;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        adapter = new AdapterRecyclerChat();

        initializeLaunchers();
        refreshRecycler();
        onClicks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeLaunchers(){
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            resultUri = uri;
                            binding.selectedImage.setImageURI(resultUri);
                            binding.imageCard.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );


        // Initialize the ActivityResultLauncher for requesting permissions
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        onClicks();

    }

    private void onClicks(){

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        binding.page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardAndClearFocus();
            }
        });

        binding.recyclerChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardAndClearFocus();
            }
        });

        binding.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        adapter.setOnItemClick(new AdapterRecyclerChat.OnItemClick() {
            @Override
            public void OnClick(String text) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("Copied Text", text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnImageClick(String imagePath) {
                DialogFragment dialogFragment = FullScreenImageDialogFragment.newInstance(imagePath);
                dialogFragment.show(requireActivity().getSupportFragmentManager(), "FullScreenImageDialog");
            }

        });

        binding.photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
        });
        binding.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultUri = null;
                binding.imageCard.setVisibility(View.GONE);
            }
        });


    }

    private void validation(){
        String msg = binding.msgEdit.getText().toString().trim();

        if (resultUri != null){
            try {
                ContentResolver contentResolver = requireActivity().getContentResolver();
                Bitmap bitmap = UriTypeConverter.getBitmapFromUri( resultUri ,contentResolver);

                String imagePath = UriTypeConverter.saveImageToStorage(bitmap, requireActivity());

                ModelChat model = new ModelChat(Consts.YOU, msg , imagePath);
                if (NetworkUtil.isConnected(requireActivity())) {
                    binding.bar.setVisibility(View.VISIBLE);
                    SharedModel.activeChat.add(model);
                    refreshRecycler();
                    sendImageMsg(model,bitmap);

                } else {
                    showCustomDialog();
                }
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error happen", Toast.LENGTH_SHORT).show();
            }
        }

        else if (!msg.isEmpty()){
            ModelChat model = new ModelChat(Consts.YOU, msg,null);
            if (NetworkUtil.isConnected(requireActivity())) {
                binding.bar.setVisibility(View.VISIBLE);
                SharedModel.activeChat.add(model);
                refreshRecycler();
                sendMsg(model);

            } else {
                showCustomDialog();
            }


        }

    }

    private void openGallery() {

        getContentLauncher.launch("image/*");
    }

    private void sendMsg(ModelChat model){
        hideKeyboardAndClearFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            viewModel.getMessage(model , requireActivity().getMainExecutor());
            viewModel.confirmationState.observe(requireActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (SharedModel.activeChat.size() == i){
                        refreshRecycler();
                        binding.bar.setVisibility(View.GONE);
                        i+=2;
                        saveCache();
                    }
                }
            });

        }
    }

    private void sendImageMsg(ModelChat model , Bitmap bitmap){
        hideKeyboardAndClearFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            viewModel.getImageMessage(model , requireActivity().getMainExecutor(),bitmap);
            viewModel.confirmationState.observe(requireActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (SharedModel.activeChat.size() == i){
                        refreshRecycler();
                        binding.bar.setVisibility(View.GONE);
                        i+=2;
                        saveCache();
                    }
                }
            });
        }
    }

    private void hideKeyboardAndClearFocus() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.msgEdit.getWindowToken(), 0);
        }
        binding.msgEdit.setText("");
        binding.msgEdit.clearFocus();
    }

    private void refreshRecycler(){
        adapter.setMessageList(SharedModel.activeChat);
        textSize = getTextSize();
        resultUri = null;
        adapter.setTextSize(textSize);
        binding.recyclerChat.setAdapter(adapter);
        binding.imageCard.setVisibility(View.GONE);

    }

    private void showCustomDialog() {
        hideKeyboardAndClearFocus();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.TransparentDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_custom, null);
        builder.setView(dialogView);

        MaterialButton dialogButton = dialogView.findViewById(R.id.dialogButton);


        AlertDialog alertDialog = builder.create();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showPopupMenu(View view) {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.main_menu, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // Set on click listeners for the items in the popup menu
        ConstraintLayout newChat = popupView.findViewById(R.id.new_chat_btn);
        ConstraintLayout history = popupView.findViewById(R.id.history_btn);
         ConstraintLayout settings = popupView.findViewById(R.id.settings_btn);

        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                clearCurrentChat();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HistoryFragment() , "history")
                        .addToBackStack("history")
                        .commit();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingsFragment() , "settings")
                        .addToBackStack("settings")
                        .commit();
            }
        });

        // Show the popup window
        popupWindow.showAsDropDown(view, 0, 0);
    }

    private void clearCurrentChat(){
        binding.bar.setVisibility(View.GONE);
        i = 2 ;
        SharedModel.setActiveChat(new ArrayList<>());
        refreshRecycler();
    }

    private void saveCache(){
        if (i ==4){
            viewModel.insertCache(new ModelCache(SharedModel.activeChat));
        }
        else if (i >4 && i%2 == 0){
            viewModel.getLastitem(new ModelCache(SharedModel.activeChat));
        }
    }

    public int getTextSize() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return preferences.getInt("textSize", 14);
    }




}