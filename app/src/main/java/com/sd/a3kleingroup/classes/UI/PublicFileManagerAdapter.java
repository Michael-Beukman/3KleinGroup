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

public class PublicFileManagerAdapter extends RecyclerView.Adapter<PublicFileManagerAdapter.pubFileManagerViewHolder>{

    private ArrayList<dbPublicFiles> files = new ArrayList<>();
    public PublicFileManagerAdapter(ArrayList<dbPublicFiles> Files){ this.files = Files;}

    public static class pubFileManagerViewHolder extends RecyclerView.ViewHolder{

        public TextView fileName;
        public ImageView viewInfo;
        public ImageView deleteInfo;

        public pubFileManagerViewHolder(View view){
            super(view);
            viewInfo = view.findViewById(R.id.recycler_view_image);
            deleteInfo = view.findViewById(R.id.recycler_delete_image);
            fileName = view.findViewById(R.id.recycler_file_name);
        }
    }

    @NonNull
    @Override
    public pubFileManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_file_manager_items, parent, false);
        pubFileManagerViewHolder newView = new pubFileManagerViewHolder(v);
        return  newView;
    }

    @Override
    public void onBindViewHolder(@NonNull pubFileManagerViewHolder holder, int position) {
        dbPublicFiles currFile = files.get(position);
        holder.fileName.setText(currFile.getFileName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}