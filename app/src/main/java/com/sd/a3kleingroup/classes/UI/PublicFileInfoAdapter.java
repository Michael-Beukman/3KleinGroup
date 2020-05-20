package com.sd.a3kleingroup.classes.UI;




/*
Our current public_friend_recycler_items has 2 text views and a button
 */

import android.content.Context;
import android.content.Intent;
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
    ArrayList<dbPublicFiles> files;


    public PublicFileInfoAdapter(Context con, ArrayList<dbPublicFiles> filesArrayList){
        this.context = con;
        this.files = filesArrayList;
    }

    public class PubViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;



        public PubViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.recycler_friend_friendName);

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
        holder.friendName.setText(files.get(position).getFileName()); // TODO: 2020/05/20 public files needs to have a friends array...
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}