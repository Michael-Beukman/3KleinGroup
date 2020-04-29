package com.sd.a3kleingroup.classes;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // cache the user/file info agreementID -> fileModel
    public static HashMap<String, FileModel> cache = new HashMap<>();

    public RecyclerViewClickListener mTextBtnListener;
    public RecyclerViewClickListener mPopUpListener;

    public FileModel joinedFileInfo;
    public TextView txtbtnFileName;
    public ImageButton btnHamburger;
    public TextView txtOwner;
    public TextView txtDate;
    public Callback cbOwner;
    public Callback cbFile;
    /**
     * The count of how much data we have (if it is 2, we have received the user and file data)
     */
    public RecyclerHolder(@NonNull View itemView, RecyclerViewClickListener txtbtnlistener, RecyclerViewClickListener popuplistener) {
        super(itemView);

        joinedFileInfo = new FileModel();

        mTextBtnListener = txtbtnlistener;
        mPopUpListener = popuplistener;

        btnHamburger = (ImageButton)  itemView.findViewById(R.id.imgBtn_Hamburger);
        txtbtnFileName = (TextView) itemView.findViewById(R.id.txtbtn_filename);
        txtOwner = (TextView) itemView.findViewById(R.id.txt_owner);
        txtDate = (TextView) itemView.findViewById(R.id.txt_date);

        txtbtnFileName.setOnClickListener(this);
        btnHamburger.setOnClickListener(this);

        setCallbacks();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtbtn_filename:
                mTextBtnListener.onClick(view, getAdapterPosition(), this);
                break;
            case R.id.imgBtn_Hamburger:
                mPopUpListener.onClick(view, getAdapterPosition(), this);
                break;
        }
    }

    private void setCallbacks() {
        cbOwner = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                txtOwner.setText((String)data.get("name"));
                dbUser owner = new dbUser((String)data.get("email"),(String)data.get("name"),(String)data.get("notificationToken"));
                joinedFileInfo.setOwner(owner);
                afterGetPieceOfData();
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                txtOwner.setText(error);

            }
        };

        cbFile = new Callback() {
            @Override
            public void onSuccess(Map<String, Object> data, String message) {
                txtbtnFileName.setText((String)data.get("filename"));
                joinedFileInfo.setFileName((String)data.get("filename"));
                joinedFileInfo.setUrl((String)data.get("storageURL"));
                joinedFileInfo.setPath((String)data.get("filepath"));
                joinedFileInfo.setEncryptionKey((String)data.get("encryptionKey"));
                afterGetPieceOfData();
            }

            @Override
            public void onFailure(String error, MyError.ErrorCode errorCode) {
                txtbtnFileName.setText(error);

            }
        };
    }

    /**
     * This should get called after we get either the file, or user.
     * If we have both, we set the correct entry in cache
     */
    private void afterGetPieceOfData() {
        if (joinedFileInfo.isAllDataRetrieved()){
            cache.put(joinedFileInfo.agreement.getId(), joinedFileInfo);
        }
    }

    public void setLoading() {
        txtDate.setText("Loading...");
        txtOwner.setText("Loading...");
        txtbtnFileName.setText("Loading...");
    }

    /**
     * Populates the views with the data in fileModel
     * @param fileModel
     */
    public void populateViews(FileModel fileModel) {
        this.joinedFileInfo = fileModel;
        txtbtnFileName.setText((String)fileModel.getFileName());
        txtOwner.setText(fileModel.owner.getName());
    }
}
