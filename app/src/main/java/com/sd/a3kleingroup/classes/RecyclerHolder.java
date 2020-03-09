package com.sd.a3kleingroup.classes;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;

public class RecyclerHolder extends RecyclerView.ViewHolder {

    Button mDownload;
    TextView mFileName;

    public RecyclerHolder(@NonNull View itemView) {
        super(itemView);

        mFileName = itemView.findViewById(R.id.txt_filename);
        mDownload = itemView.findViewById(R.id.btn_download);

    }
}
