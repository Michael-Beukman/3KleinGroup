package com.sd.a3kleingroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        // just go to the sendFile act
        // todo remove
        val intent = Intent(this, SendFileActivity::class.java)
        startActivity(intent);
    }
    public fun sayGoodbye(): String{
        return "Goodbye"
    }
}
