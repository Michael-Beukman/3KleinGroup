package com.sd.a3kleingroup.classes.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sd.a3kleingroup.PublicFileManagerActivity;
import com.sd.a3kleingroup.PublicFileReceiveActivity;
import com.sd.a3kleingroup.PublicFileSendActivity;
import com.sd.a3kleingroup.R;

public class PublicFilesHomeActivity extends AppCompatActivity {

    private Button receiveBtn;
    private Button sendBtn;
    private Button manageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_file_manager);

        receiveBtn = findViewById(R.id.public_manager_receive_file_button);
        sendBtn = findViewById(R.id.public_manager_send_file_button);
        manageBtn = findViewById(R.id.public_file_manager_manage_files_button);

        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getApplicationContext(), PublicFileReceiveActivity.class);
                startActivity(I);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getApplicationContext(), PublicFileSendActivity.class);
                startActivity(I);
            }
        });

        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(getApplicationContext(), PublicFileManagerActivity.class);
                startActivity(I);
            }
        });
    }
}
