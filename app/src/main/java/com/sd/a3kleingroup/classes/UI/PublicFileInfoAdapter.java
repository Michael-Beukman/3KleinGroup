package com.sd.a3kleingroup.classes.UI;




/*
Our current public_friend_recycler_items has 2 text views and a button
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.FileManagerViewFileInfoActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.util.ArrayList;


/**
 * Recyclerview.Adapter to binding the data to the view
 * RecyclerView.ViewHolder to holding the view
 * This class is meant to get the file's information but specifically display the friends who viewed the file
 * The friend receive side should be appending you to a the viewed Arraylist for those who viewed it
 * At current dbPublic files doesn't have a Viewed Arraylist. so getting those names and populating them requires it
 * Friends do not need to be clickable
 */
public class PublicFileInfoAdapter extends RecyclerView.Adapter<PublicFileInfoAdapter.PubViewHolder>{

    Context context;
    ArrayList<String> friends;
    String LOG_TAG = "MY_PublicFileInfoAdapter";

    public PublicFileInfoAdapter(Context con, ArrayList<String> friends){
        this.context = con;
        this.friends = friends;
    }

    public class PubViewHolder extends RecyclerView.ViewHolder {
        TextView txtFriendName;
        String friendName;
        public PubViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(LOG_TAG, "HOLDER  creating " + friendName);

            txtFriendName = itemView.findViewById(R.id.recycler_friend_friendName);
        }

        public void setFriendName(String friendName) {
            this.friendName = friendName;
            Log.d(LOG_TAG, "HOLDER  setting2 " + friendName);

            updateUI();
        }

        private void updateUI(){
            txtFriendName.setText(friendName);
            Log.d(LOG_TAG, "HOLDER  setting " + friendName);
        }
    }


    @NonNull
    @Override
    public PubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.public_friend_recycler_item, parent, false);
        return new PubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PubViewHolder holder, int position) {
        holder.setFriendName(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}