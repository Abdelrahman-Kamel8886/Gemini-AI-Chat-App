package com.abdok.geminichat.UI.History;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.abdok.geminichat.Adapters.AdapterRecyclerHistory;
import com.abdok.geminichat.Enums.ConfirmationState;
import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Models.ModelChat;
import com.abdok.geminichat.R;
import com.abdok.geminichat.UI.History.details.DetailsFragment;
import com.abdok.geminichat.UI.MainActivity;
import com.abdok.geminichat.UI.Settings.SettingsFragment;
import com.abdok.geminichat.Utils.SharedModel;
import com.abdok.geminichat.databinding.FragmentHistoryBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding ;
    HistoryViewModel viewModel ;
    AdapterRecyclerHistory adapter  = new AdapterRecyclerHistory();

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHistoryBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        getData();
        onClicks();

    }

    private void onClicks(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        adapter.setOnItemClick(new AdapterRecyclerHistory.OnItemClick() {
            @Override
            public void OnClick(ModelCache contactModel) {
                SharedModel.setOldChat(contactModel.getChat());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DetailsFragment(), "details")
                        .addToBackStack("details")
                        .commit();
            }

            @Override
            public void OnModifyBtnClick(ModelCache contactModel , View v) {
                showPopupMenu(v , contactModel);
            }
        });
        binding.statChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }

    private void getData(){
        viewModel.fetchAllCaches();
        viewModel.getCachesLiveData().observe(getViewLifecycleOwner(), new Observer<List<ModelCache>>() {
            @Override
            public void onChanged(List<ModelCache> modelCaches) {
                show(modelCaches);
            }
        });
    }

    private void show(List<ModelCache> modelCaches){
        if (modelCaches.size() == 0){
            binding.noHistory.setVisibility(View.VISIBLE);
        }
        else {
            binding.noHistory.setVisibility(View.GONE);
        }
        adapter.setList((ArrayList<ModelCache>) modelCaches);
        binding.recyclerHistory.setAdapter(adapter);
        binding.recyclerHistory.scrollToPosition(modelCaches.size()-1);

    }


    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showPopupMenu(View view, ModelCache model) {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.modify_menu, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // Measure the dimensions of the popup content
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();
        int popupHeight = popupView.getMeasuredHeight();

        // Get the screen height
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // Get the location of the anchor view on the screen
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        // Calculate if the popup will be out of the screen bounds
        if (location[1] + popupHeight > screenHeight) {
            // Show the popup above the anchor view if it will be out of bounds
            popupWindow.showAsDropDown(view, 0, -view.getHeight() - popupHeight, Gravity.BOTTOM);
        } else {
            // Otherwise, show it normally
            popupWindow.showAsDropDown(view);
        }

        // Set on click listeners for the items in the popup menu
        ConstraintLayout rename = popupView.findViewById(R.id.rename_btn);
        ConstraintLayout delete = popupView.findViewById(R.id.delete_btn);

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showUpdateDialog(model);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showDeleteDialog(model);
            }
        });
    }

    private void deleteItem(ModelCache model){
        viewModel.deleteCache(model);
        viewModel.getDeleteConfirmtion().observe(getViewLifecycleOwner(), new Observer<ConfirmationState>() {
            @Override
            public void onChanged(ConfirmationState confirmationState) {

                if (confirmationState.equals(ConfirmationState.SUCCESS)){
                    getData();
                }
            }
        });
    }

    private void updateItem(ModelCache model , String title){
        viewModel.updateTitleById(model.getId() , title);
        viewModel.getUpdateConfirmtion().observe(getViewLifecycleOwner(), new Observer<ConfirmationState>() {
            @Override
            public void onChanged(ConfirmationState confirmationState) {
                if (confirmationState.equals(ConfirmationState.SUCCESS)){
                    getData();
                }
            }
        });
    }

    private void showDeleteDialog(ModelCache model) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog, null);


        Button deleteButton = dialogView.findViewById(R.id.dialog_delete);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),R.style.TransparentDialog);
        builder.setView(dialogView);

        // Handle button clicks
        final AlertDialog dialog = builder.create();

        deleteButton.setOnClickListener(v -> {
            deleteItem(model);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showUpdateDialog(ModelCache model) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_dialog, null);


        MaterialButton okButton = dialogView.findViewById(R.id.dialog_ok);
        MaterialButton cancelButton = dialogView.findViewById(R.id.dialog_cancel);

        EditText editTitle = dialogView.findViewById(R.id.name_edit);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),R.style.TransparentDialog);
        builder.setView(dialogView);

        // Handle button clicks
        final AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            if (title.isEmpty()){
                editTitle.setError("required");
            }
            else {
                updateItem(model,title);
                dialog.dismiss();
            }


        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}