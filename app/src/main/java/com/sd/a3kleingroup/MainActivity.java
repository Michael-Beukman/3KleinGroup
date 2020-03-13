package com.sd.a3kleingroup;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this.getApplicationContext(), "Toast", Toast.LENGTH_LONG).show();
        Log.d("LOG_MAIN_ACT", " In here");
        // test login
        Intent intent2 = new Intent(this, LoginActivity.class);
        startActivity(intent2);

    }
}
