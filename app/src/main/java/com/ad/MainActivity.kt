package com.ad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.conjoinix.adsdk.ConjoinixAd
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt.setOnClickListener {

            ConjoinixAd.Builder(this)
                .adKey("Test Key")
                .build { isSuccess, _ ->

                    txt.setText("$isSuccess")
            }
        }



    }
}


fun Log(message: String) {

    Log.e("hi welcome", "  $message ")

}


