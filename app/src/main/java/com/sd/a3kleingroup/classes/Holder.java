package com.sd.a3kleingroup.classes;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;

import java.util.Map;

// This is a single item in our list
public class Holder extends RecyclerView.ViewHolder {
    public TextView main;
    public ImageButton imgButton;
    public TextView userName;
    public Callback cbUser;
    public Callback cbFile;
    public Holder(View itemView) {
        super(itemView);
        main = itemView.findViewById(R.id.msf_li_txtTitle);
        imgButton = itemView.findViewById(R.id.msf_li_imgBtn);
        userName = itemView.findViewById(R.id.msf_li_txtUserName);
        setCallbacks();
    }

    private void setCallbacks() {
        cbUser = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                userName.setText((String)data.get("name"));
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                userName.setText(error);

            }
        };

        cbFile = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                main.setText((String)data.get("filename"));
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                main.setText(error);

            }
        };
    }

    /**
     * Makes as if it is loading
     */
    public void setLoading() {
        main.setText("Loading...");
        userName.setText("Loading...");
    }
}
