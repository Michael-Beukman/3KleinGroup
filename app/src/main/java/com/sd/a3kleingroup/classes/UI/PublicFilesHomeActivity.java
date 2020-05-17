package com.sd.a3kleingroup.classes.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.sd.a3kleingroup.PublicFileManagerActivity;
import com.sd.a3kleingroup.PublicFileReceiveActivity;
import com.sd.a3kleingroup.PublicFileSendActivity;
import com.sd.a3kleingroup.R;
import com.sd.a3kleingroup.classes.BaseActivity;

public class PublicFilesHomeActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_manager);

        Button receiveBtn = findViewById(R.id.public_manager_receive_file_button); //for guy's side
        Button sendBtn = findViewById(R.id.public_manager_send_file_button);
        Button manageBtn = findViewById(R.id.public_file_manager_manage_files_button);

        receiveBtn.setOnClickListener(v -> {
            Intent I = new Intent(getApplicationContext(), PublicFileReceiveActivity.class);
            startActivity(I); //for guy's side
        });

        sendBtn.setOnClickListener(v -> {
            Intent I = new Intent(getApplicationContext(), PublicFileSendActivity.class);
            startActivity(I);
        });

        manageBtn.setOnClickListener(v -> {
            Intent I = new Intent(getApplicationContext(), PublicFileManagerActivity.class);
            startActivity(I);
        });
        doNavigation();
    }
}
