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

    private ArrayList<dbUser> userList;


    public static class PublicViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public TextView friendEmail;
        public Button btn;
        public PublicViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.recycler_friend_friendName);
            friendEmail = itemView.findViewById(R.id.recycler_friend_friendEmail);
            btn = itemView.findViewById(R.id.recycler_friend_view_button);
        }
    }

    public PublicRecyclerViewAdapter(ArrayList<dbUser> dbUserList){
        userList = dbUserList;
    }


    @NonNull
    @Override
    public PublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_friend_recycler_item, parent, false);
        PublicViewHolder curr = new PublicViewHolder(v);
        return curr;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicViewHolder holder, int position) {
        dbUser currentUser = userList.get(position);
        holder.friendEmail.setText(currentUser.getEmail());
        holder.friendName.setText(currentUser.getName());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the intent for that user that is clicked here.
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
