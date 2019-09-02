package com.ad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.conjoinix.adsdk.ConjoinixAd
import com.conjoinix.adsdk.show
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt.setOnClickListener {

           /* ConjoinixAd.Builder(this)
                .adKey("1gywg0vz7G")
                .isBulk("1")
                .buildBulk { isSuccess, data ->
                    txt.setText("Done $isSuccess")
                    print("hii $isSuccess")
                    print("Data model ${data?.size}")


                }*/


        }

        bannerView.loadAd(this){

            if(it)  {
                bannerView.show()
            }
        }



    }
}


fun Log(message: String) {

    Log.e("hi welcome", "  $message ")

}


