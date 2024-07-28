package com.abdok.geminichat.UI.History.details;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdok.geminichat.Adapters.AdapterRecyclerChat;
import com.abdok.geminichat.R;
import com.abdok.geminichat.UI.FullScreenImage.FullScreenImageDialogFragment;
import com.abdok.geminichat.Utils.SharedModel;
import com.abdok.geminichat.databinding.FragmentDetailsBinding;


public class DetailsFragment extends Fragment {

    FragmentDetailsBinding binding;
    AdapterRecyclerChat adapter = new AdapterRecyclerChat();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentDetailsBinding.bind(view);
        refreshRecycler();
        onClicks();


    }

    private void onClicks(){

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void refreshRecycler(){
        adapter.setMessageList(SharedModel.getOldChat());
        binding.recyclerChat.setAdapter(adapter);
        binding.recyclerChat.scrollToPosition(0);
    }
}