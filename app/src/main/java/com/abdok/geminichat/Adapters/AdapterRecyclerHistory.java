package com.abdok.geminichat.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.abdok.geminichat.Models.ModelCache;
import com.abdok.geminichat.Models.ModelChat;
import com.abdok.geminichat.R;

import java.util.ArrayList;

public class AdapterRecyclerHistory extends RecyclerView.Adapter<AdapterRecyclerHistory.Holder> {


    ArrayList<ModelCache> list = new ArrayList<>();

    private OnItemClick onItemClick ;

    public void setOnItemClick (OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public void setList(ArrayList<ModelCache> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.content.setText(list.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView title , content ;
        ImageButton modifyBtn ;
        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            modifyBtn = itemView.findViewById(R.id.modify_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null){
                        onItemClick.OnClick(list.get(getLayoutPosition()));
                    }
                }
            });

            modifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnModifyBtnClick(list.get(getLayoutPosition()) , v);
                }
            });



        }
    }

    public interface OnItemClick{

        void OnClick(ModelCache contactModel);
        void OnModifyBtnClick(ModelCache contactModel , View v);

    }

}
