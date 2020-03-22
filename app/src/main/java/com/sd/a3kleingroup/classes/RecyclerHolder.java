package com.sd.a3kleingroup.classes;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;

import java.io.IOException;

public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    RecyclerViewClickListener mTextBtnListener;
    RecyclerViewClickListener mPopUpListener;

    TextView txtbtnFileName;
    ImageButton btnHamburger;
    TextView txtOwner;
    TextView txtDate;

    public RecyclerHolder(@NonNull View itemView, RecyclerViewClickListener txtbtnlistener, RecyclerViewClickListener popuplistener) {
        super(itemView);

        mTextBtnListener = txtbtnlistener;
        mPopUpListener = popuplistener;

        btnHamburger = (ImageButton)  itemView.findViewById(R.id.imgBtn_Hamburger);
        txtbtnFileName = (TextView) itemView.findViewById(R.id.txtbtn_filename);
        txtOwner = (TextView) itemView.findViewById(R.id.txt_owner);
        txtDate = (TextView) itemView.findViewById(R.id.txt_date);

        txtbtnFileName.setOnClickListener(this);
        btnHamburger.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtbtn_filename:
                mTextBtnListener.onClick(view, getAdapterPosition());
                break;
            case R.id.imgBtn_Hamburger:
                mPopUpListener.onClick(view, getAdapterPosition());
                break;
        }
    }
}
