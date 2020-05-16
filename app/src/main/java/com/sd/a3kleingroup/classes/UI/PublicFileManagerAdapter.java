package com.sd.a3kleingroup.classes.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.PublicFile;
import com.sd.a3kleingroup.classes.db.dbPublicFileManager;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import org.w3c.dom.Text;

import java.util.ArrayList;


/*
Our current public_friend_recycler_items has 2 text views and a button
 */

public class PublicFileManagerAdapter extends RecyclerView.Adapter<PublicFileManagerAdapter.PublicViewAdapterFM>{

    private OnItemClickListener mlistener;

    private ArrayList<dbPublicFileManager> files;
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onViewInfoClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

    public static class PublicViewAdapterFM extends RecyclerView.ViewHolder {
        public TextView fileName;
        public ImageView viewInfo;
        public ImageView deleteFile;

        public PublicViewAdapterFM(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            fileName = itemView.findViewById(R.id.recycler_file_name);
            viewInfo = itemView.findViewById(R.id.recycler_view_image);
            deleteFile = itemView.findViewById(R.id.recycler_delete_image);

            viewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onViewInfoClick(position);
                        }
                    }
                }
            });
            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public PublicFileManagerAdapter(ArrayList<dbPublicFileManager> filesList){
        files = filesList;
    }


    @NonNull
    @Override
    public PublicViewAdapterFM onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_friend_recycler_item, parent, false);
        PublicViewAdapterFM curr = new PublicViewAdapterFM(v, mlistener);
        return curr;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicViewAdapterFM holder, int position) {
        dbPublicFileManager currentUser = files.get(position);
        holder.fileName.setText(currentUser.getFileName());

    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}