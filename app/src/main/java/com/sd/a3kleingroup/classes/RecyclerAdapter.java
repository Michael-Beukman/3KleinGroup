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

    ReceiveFilesActivity receiveFilesActivity;
    ArrayList<FileModel> fileModels;

    public RecyclerAdapter(ReceiveFilesActivity receiveFilesActivity, ArrayList<FileModel> fileModels) {
        this.receiveFilesActivity = receiveFilesActivity;
        this.fileModels = fileModels;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(receiveFilesActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.view_recycler, null, false);
        return new RecyclerHolder(view);
    }

    //Provides a means to handle each view holder in the list
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        holder.mFileName.setText(fileModels.get(position).getFileName());

        //Make sure
        holder.mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileModel file = fileModels.get(position);
                downloadFile(holder.mFileName.getContext(),file.getFileName(),file.getFormat(),DIRECTORY_DOWNLOADS,file.getUri());
            }
        });
    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, Uri uri) {

        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return fileModels.size();
    }
}
