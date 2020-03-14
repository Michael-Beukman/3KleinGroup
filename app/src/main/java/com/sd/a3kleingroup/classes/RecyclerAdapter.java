package com.sd.a3kleingroup.classes;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.ReceiveFilesActivity;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

    RecyclerViewClickListener mListener;
    ReceiveFilesActivity receiveFilesActivity;
    ArrayList<FileModel> fileModels;

    public RecyclerAdapter(ReceiveFilesActivity receiveFilesActivity, ArrayList<FileModel> fileModels, RecyclerViewClickListener listener) {
        mListener=listener;
        this.receiveFilesActivity = receiveFilesActivity;
        this.fileModels = fileModels;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(receiveFilesActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.view_recycler, null, false);
        return new RecyclerHolder(view, mListener);
    }

    //Provides a means to handle each view holder in the list
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        if (holder instanceof RecyclerHolder) {
            RecyclerHolder rowHolder = (RecyclerHolder) holder;
            //set values of data here
            rowHolder.mDownload.setText(fileModels.get(position).getFileName());
        }
    }

    @Override
    public int getItemCount() {
        return fileModels.size();
    }
}
