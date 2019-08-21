package com.ad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.conjoinix.adsdk.ConjoinixAd
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt.setOnClickListener {

            ConjoinixAd.Builder(this).adKey("SMARTPARENTAPP2017").build { a, b ->

                txt.setText(b)
            }
        }



    }
}


fun Log(message: String) {


    Log.e("hi welcome", "  $message ")

}


