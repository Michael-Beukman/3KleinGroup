package com.sd.a3kleingroup.classes.UI;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.RecyclerViewClickListener;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import java.util.HashMap;
import java.util.Map;

public class PublicFilesRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // cache the user/file info agreementID -> fileModel
    private dbPublicFiles myFile;

    public FileModel joinedFileInfo;
    public TextView txtbtnFileName;
    public ImageButton btnHamburger;
    public TextView txtOwner;
    private TextView txtDate;
    private dbUser friend;

    public PublicFilesRecyclerHolder(@NonNull View itemView) {
        super(itemView);

        joinedFileInfo = new FileModel();

        btnHamburger = (ImageButton) itemView.findViewById(R.id.imgBtn_Hamburger);
        txtbtnFileName = (TextView) itemView.findViewById(R.id.txtbtn_filename);
        txtOwner = (TextView) itemView.findViewById(R.id.txt_owner);
        txtDate = itemView.findViewById(R.id.txt_date);

        txtbtnFileName.setOnClickListener(this);
        btnHamburger.setOnClickListener(this);

    }

    private void showUI() {
        Log.d("MY_HOLDER_", "Setting the filename text");
        txtbtnFileName.setText("Name: " + myFile.getFileName());
        txtOwner.setText("Owner: " + (friend == null ? "...loading" : friend.getName() ));

        // make invisible for now.
        txtDate.setVisibility(View.INVISIBLE);
        btnHamburger.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View view) {
        // TODO
        switch (view.getId()) {
            case R.id.txtbtn_filename:
                Log.d("MY_HOLDER_", "Clicked filename");
                // TODO Guy's downloading things
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
