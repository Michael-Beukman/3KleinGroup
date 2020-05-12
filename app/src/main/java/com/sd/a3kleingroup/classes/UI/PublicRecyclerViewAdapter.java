package com.sd.a3kleingroup.classes.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbUser;

import org.w3c.dom.Text;

import java.util.ArrayList;


/*
Our current public_friend_recycler_items has 2 text views and a button
 */

public class PublicRecyclerViewAdapter extends RecyclerView.Adapter<PublicRecyclerViewAdapter.PublicViewHolder>{

    private OnItemClickListener mlistener;

    private ArrayList<dbUser> userList;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class PublicViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public TextView friendEmail;

        public PublicViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            friendName = itemView.findViewById(R.id.recycler_friend_friendName);
            friendEmail = itemView.findViewById(R.id.recycler_friend_friendEmail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public PublicRecyclerViewAdapter(ArrayList<dbUser> dbUserList){
        userList = dbUserList;
    }


    @NonNull
    @Override
    public PublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_friend_recycler_item, parent, false);
        PublicViewHolder curr = new PublicViewHolder(v, mlistener);
        return curr;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicViewHolder holder, int position) {
        dbUser currentUser = userList.get(position);
        holder.friendEmail.setText(currentUser.getEmail());
        holder.friendName.setText(currentUser.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
