package com.sd.a3kleingroup;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.sd.a3kleingroup.classes.BaseActivity;
import com.sd.a3kleingroup.classes.Callback;
import com.sd.a3kleingroup.classes.MyError;
import com.sd.a3kleingroup.classes.MyFile;

import java.io.File;
import java.util.HashMap;

/**
 * This is an abstract class to handle choosing a file from the device.
 */
public abstract class FileChooseActivity extends BaseActivity {
    protected final int FILE_REQUEST_CODE = 1;
    protected final int FILE_RESULT_SUCCESS = -1;
    private View btnClicking; // the button that gets clicked to trigger the choose file things

    /**
     * The callback to call after getting the file.
     * It gets called with a hashmap {"Uri":fileURI}
     */
    protected Callback afterLookAtFile;

    protected class chooseFile implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d("MY_SEND", "H ey I clicked " + LOG_TAG);
            // set btn disabled, so it doesn't double click
            btnClicking = view;
            btnClicking.setEnabled(false);
            Intent chooseFileTmp = new Intent(Intent.ACTION_GET_CONTENT);

            // use pick instead to get a file URI.
            // Intent chooseFile = new Intent(Intent.ACTION_PICK);
            chooseFileTmp.setType("*/*");
            chooseFileTmp = Intent.createChooser(chooseFileTmp, "Choose a file");
            startActivityForResult(chooseFileTmp, FILE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "Hey in here dudes with result");
        if (requestCode == FILE_REQUEST_CODE) {
            // then we know that this was the file choosing result.
            if (resultCode == FILE_RESULT_SUCCESS) {
                // Then it is a success
                Uri fileUri = data.getData();
                Log.d(LOG_TAG, "Got file URI: " + fileUri + " PATH: " + fileUri.getPath() + " Exists + " + new File(fileUri.getPath()).exists());
                if (afterLookAtFile != null) {
                    afterLookAtFile.onSuccess(new HashMap<String, Object>() {{
                        put("Uri", fileUri);
                    }}, "");
                }
            } else {
                if (afterLookAtFile != null) {
                    afterLookAtFile.onFailure("Choosing file failed with code (" + resultCode + ")", MyError.ErrorCode.TASK_FAILED);
                }
                Log.e(LOG_TAG, "File Error with code " + resultCode);
            }
            // set enabled again, since we're done
            btnClicking.setEnabled(true);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * This is a class that handles when the text is changed. It changes the file's filename property
     */
    public class FilenameChange implements TextWatcher {
        private MyFile file;

        public FilenameChange(MyFile file) {
            this.file = file;
        }

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
}
