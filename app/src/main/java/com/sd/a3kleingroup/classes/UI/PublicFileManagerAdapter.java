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
 */
public class PublicFileManagerAdapter extends RecyclerView.Adapter<PublicFileManagerAdapter.PubViewHolder>{

    Context context;
    ArrayList<dbPublicFiles> files;
    OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public PublicFileManagerAdapter(Context con, ArrayList<dbPublicFiles> filesArrayList){
        this.context = con;
        this.files = filesArrayList;
    }

    public class PubViewHolder extends RecyclerView.ViewHolder{
        TextView filename;
        ImageView delete;
        ImageView viewInfo;
        public PubViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            filename = itemView.findViewById(R.id.recycler_file_name);
            delete = itemView.findViewById(R.id.recycler_delete_image);
            viewInfo = itemView.findViewById(R.id.recycler_view_image);
        }
    }

    @NonNull
    @Override
    public PubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.public_file_manager_items, parent, false);
        return new PubViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PubViewHolder holder, int position) {
        holder.filename.setText(files.get(position).getFileName());

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}