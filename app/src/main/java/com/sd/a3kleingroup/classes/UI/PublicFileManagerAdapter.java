package com.sd.a3kleingroup.classes.UI;




/*
Our current public_friend_recycler_items has 2 text views and a button
 */

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
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
import com.sd.a3kleingroup.classes.Utils;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Recyclerview.Adapter to binding the data to the view
 * RecyclerView.ViewHolder to holding the view
 * display the user's files using this adapter
 * make sure that each view is clickable and that it sends you to the FileManagerViewFileInfoActivity
 * from there they can delete the file and see the friends who have currently seen it
 */
public class PublicFileManagerAdapter extends RecyclerView.Adapter<PublicFileManagerAdapter.PubViewHolder> {

    String LOG_TAG = "MY_PublicFileManagerAdapter";

    Context context;
    ArrayList<dbPublicFiles> files;


    public PublicFileManagerAdapter(Context con, ArrayList<dbPublicFiles> filesArrayList) {
        this.context = con;
        this.files = filesArrayList;
    }

    public class PubViewHolder extends RecyclerView.ViewHolder {
        TextView filename;
        ImageView viewInfo; //redundant at this point but is there
        dbPublicFiles fileToDisplay;

        public PubViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(LOG_TAG, "In pub view holder constructor");
            filename = itemView.findViewById(R.id.recycler_file_name);
            viewInfo = itemView.findViewById(R.id.recycler_view_image);

            setListeners();
        }

        /**
         * What happens when one of my things get clicked.
         */
        private void clickMe(View v) {
            Log.d(LOG_TAG, "I was clicked " + fileToDisplay.getHashmap());
            // Now we go to the info activity.
            Intent i = new Intent(context, FileManagerViewFileInfoActivity.class);
            i.putExtra("fileHashmap", Utils.getInstance().toStringHashMap(fileToDisplay.getHashmap()));
            i.putExtra("fileID", fileToDisplay.getID());

            context.startActivity(i);
        }

        private void setListeners() {
            // sets the click listener for the layout that contains everything
            View l = itemView.findViewById(R.id.file_layout);
            l.setOnClickListener(this::clickMe);
//            filename.setOnClickListener(this::clickMe);
//            viewInfo.setOnClickListener(this::clickMe);
        }

        public void setFileToDisplay(dbPublicFiles fileToDisplay) {
            this.fileToDisplay = fileToDisplay;
            updateUI();
        }

        /**
         * Get's called after we set the file, so that the textview can be updated
         */
        public void updateUI() {
            if (fileToDisplay == null) return;
            filename.setText(fileToDisplay.getFileName());
        }
    }

    @NonNull
    @Override
    public PubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.public_file_manager_items, parent, false);
        return new PubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PubViewHolder holder, int position) {
        Log.d(LOG_TAG, "binding" + files.get(position).getHashmap() + " " + files.get(position).getFileName());
//        holder.filename.setText(files.get(position).getFileName());
        holder.setFileToDisplay(files.get(position));

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}