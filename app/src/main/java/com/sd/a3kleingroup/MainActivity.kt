package com.sd.a3kleingroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        // test login
//        val intent2 = Intent(this, LoginActivity::class.java)
//        startActivity(intent2);
//        return;
        // just go to the sendFile act
        // todo remove
//        val intent = Intent(this, SendFileActivity::class.java)
//        startActivity(intent);

        val intent = Intent(this, ReceiveFilesActivity::class.java)
        startActivity(intent);
    }
    public fun sayGoodbye(): String{
        return "Goodbye"
    }
}
