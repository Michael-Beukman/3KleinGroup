package com.sd.a3kleingroup.classes;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;

public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewClickListener mListener;
    Button mDownload;

    public RecyclerHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);

        mListener = listener;
        mDownload = itemView.findViewById(R.id.btn_download);
        mDownload.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }
}
