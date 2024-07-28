package com.abdok.geminichat.Adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdok.geminichat.Models.ModelChat;
import com.abdok.geminichat.R;
import com.abdok.geminichat.Utils.Consts;
import com.abdok.geminichat.Utils.UriTypeConverter;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
public class AdapterRecyclerChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    static Context context;


    private List<ModelChat> mMessageList = Collections.emptyList(); // Initialize to empty list
    private float textSize = 14; // Default text size

    public void setMessageList(List<ModelChat> messageList) {
        this.mMessageList = messageList;
    }

    public void setTextSize(float size) {
        this.textSize = size;
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    private static OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ModelChat message = mMessageList.get(position);
        return message.getSender().equals(Consts.YOU) ?
                VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.item_chat_me, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_chat_other, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelChat message = mMessageList.get(position);
        if (holder instanceof SentMessageHolder) {
            try {
                ((SentMessageHolder) holder).bind(message, textSize);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (holder instanceof ReceivedMessageHolder) {
            ((ReceivedMessageHolder) holder).bind(message, textSize);
        }
    }

    private static class SentMessageHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final ImageView selectedImage;
        private final MaterialCardView imageCard;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
            selectedImage = itemView.findViewById(R.id.selected_image);
            imageCard = itemView.findViewById(R.id.image_card);
        }

        void bind(ModelChat message, float textSize) throws IOException {

            if (message.getImagePath()!=null){
                imageCard.setVisibility(View.VISIBLE);
                Bitmap bitmap = UriTypeConverter.loadImageFromStorage(message.getImagePath());
                selectedImage.setImageBitmap(bitmap);
                imageCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClick != null) {
                            onItemClick.OnImageClick(message.getImagePath());
                        }
                    }
                });
            }
            else {
                imageCard.setVisibility(View.GONE);
            }

            if (!message.getMessage().isEmpty()){
                messageText.setText(message.getMessage());
                messageText.setTextSize(textSize);
                messageText.setOnLongClickListener(v -> {
                    if (onItemClick != null) {
                        onItemClick.OnClick(messageText.getText().toString());
                    }
                    return true;
                });
            }
            else{
                messageText.setVisibility(View.GONE);
            }


        }
    }

    private static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
        }

        void bind(ModelChat message, float textSize) {
            messageText.setText(message.getMessage());
            messageText.setTextSize(textSize);
            messageText.setOnLongClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.OnClick(messageText.getText().toString());
                }
                return true;
            });
        }
    }

    public interface OnItemClick {
        void OnClick(String text);
        void OnImageClick(String imagePath);
    }
}
