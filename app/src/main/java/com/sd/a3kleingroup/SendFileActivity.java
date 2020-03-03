package com.sd.a3kleingroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sd.a3kleingroup.classes.MyFile;

import org.w3c.dom.Text;

import java.nio.file.Path;

public class SendFileActivity extends AppCompatActivity {
    private final int FILE_RESULT_SUCCESS = -1;
    private final int FILE_REQUEST_CODE = 1;
    private final String LOG_TAG = "SEND_FILE_ACTIVITY";

    private Button btnChooseFile;
    private Button btnSend;
    private EditText txtEmail;
    private EditText txtFilename;

    private MyFile file; // the file that will be sent.

    //what happens when the file is chosen
    private class chooseFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, FILE_REQUEST_CODE);
        }
    }

    private class sendFile implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            
        }
    }

    private class FilenameChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // set the filename when the editText changes
            file.setFilename(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_REQUEST_CODE) {
            // then we know that this was the file choosing result.
            if (resultCode == FILE_RESULT_SUCCESS) {
                // Then it is a success
                Uri fileUri = data.getData();
                String filePath = fileUri.getPath();
                file.setFilepath(filePath);

                // make filename text visible.
                txtFilename.setVisibility(View.VISIBLE);
                txtFilename.setText(filePath);

                Log.d(LOG_TAG, "User chose File: " + filePath);
            } else {
                // todo
                // Failure. Handle Error
                Log.e(LOG_TAG, "File Error with code " + resultCode);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        setElements();
        setEvents();
        file = new MyFile();
    }

    /**
     * Sets events
     */
    private void setEvents() {
        btnChooseFile.setOnClickListener(new chooseFile());
        btnSend.setOnClickListener(new sendFile());
        txtFilename.addTextChangedListener(new FilenameChange());
    }

    /**
     * Assigns the Element variables;
     */
    private void setElements() {
        btnChooseFile = findViewById(R.id.sf_btnChooseFile);
        btnSend = findViewById(R.id.sf_btnSend);
        txtEmail = findViewById(R.id.sf_txtRecipientEmail);
        txtFilename = findViewById(R.id.sf_txtFilename);

        txtFilename.setVisibility(View.INVISIBLE);
    }
}
