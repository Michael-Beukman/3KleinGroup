package com.sd.a3kleingroup.classes.UI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.ViewFriendPublicFiles;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.callbacks.CallbackGeneric;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.encryption.AESEncryption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PublicFilesRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // cache the user/file info agreementID -> fileModel
    private dbPublicFiles myFile;

    public TextView txtbtnFileName;
    public TextView btnHamburger;
//    public TextView txtOwner;
//    private TextView txtDate;
    private dbUser friend;
//    private Context ctx;
    CallbackGeneric<dbPublicFiles> onClick;

    /**
     *
     * @param itemView
     * @param onClick -> Needs to be a callback for when the file is clicked.
     */
    public PublicFilesRecyclerHolder(@NonNull View itemView, CallbackGeneric<dbPublicFiles> onClick) {
        super(itemView);
        this.onClick = onClick;
        btnHamburger = (TextView) itemView.findViewById(R.id.imgBtn_Hamburger);
        txtbtnFileName = (TextView) itemView.findViewById(R.id.txtbtn_filename);
//        txtOwner = (TextView) itemView.findViewById(R.id.txt_owner);
//        txtDate = itemView.findViewById(R.id.txt_date);

        txtbtnFileName.setOnClickListener(this);
        btnHamburger.setOnClickListener(this);

    }

    private void showUI() {
        Log.d("MY_HOLDER_", "Setting the filename text");
        txtbtnFileName.setText("Name: " + myFile.getFileName());
//        txtOwner.setText("Owner: " + (friend == null ? "...loading" : friend.getName() ));

        // make invisible for now.
//        txtDate.setVisibility(View.INVISIBLE);
        btnHamburger.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View view) {
        // TODO
        switch (view.getId()) {
            case R.id.txtbtn_filename:
                Log.d("MY_HOLDER_", "Clicked filename");
                // TODO Guy's downloading things
//                activity.downloadFile(myFile);
                onClick.onSuccess(myFile, "");
                break;
            case R.id.imgBtn_Hamburger:
                break;
        }
    }

    public void setMyFile(dbPublicFiles myFile) {
        this.myFile = myFile;
        showUI();
    }

    public void setUserInfo(dbUser friend) {
        this.friend = friend;
        showUI();
    }

}
