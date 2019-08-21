package com.conjoinix.adsdk

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_ad.*
import android.net.Uri.fromParts
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.conjoinix.adsdk.AdActivity.WebViewController
import android.animation.ObjectAnimator
import android.widget.ImageView


/**
 * Created by deepakkanyan on 2019-08-20 , 14:34.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AdActivity : AppCompatActivity() {

    val IMAGE = "IMAGE"
    val VIDEO = "VIDEO"
    val WEB = "HTML"


    lateinit var model: AdResponse

    var canSkipAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_ad)

        model = intent.getBundleExtra("Bundle").getSerializable("data") as AdResponse




        when (model.adType) {

            IMAGE -> {

                fullImageView.show()
                fullImageView.load(this, model.adUrl)

                fullImageView.setOnClickListener {
                    openUrl(model.clickUrl)
                }
                startTimer()
            }
            VIDEO -> {
            }
            WEB -> {
                web.show()
                loadWebsite()
                startTimer()
            }
        }

        layout.setOnClickListener {

            if (canSkipAd) {
                adCompleted()

            }

        }

        call.setOnClickListener {


            val phoneNumber = model.saleContact


            if(phoneNumber.isNotBlank())
            {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            }



        }

        btnCloseNow.setOnClickListener {

            adCompleted()
        }
        call.vibrate()



    }


    fun openUrl(url : String){
        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application can handle this request." + " Please install a web browser", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }







    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebsite()
    {
        web.settings.javaScriptEnabled = true
        web.loadUrl(model.adUrl)
        web.webViewClient = WebViewController()
    }

    private fun startTimer() {


        val totalDuration = model.maxduration.milli().toLong()
        val minDuration = model.minDuration.milli().toLong()
        val differenceTime = totalDuration - minDuration


        val timer = object : CountDownTimer(totalDuration, 1000) {

            override fun onTick(click: Long) {

                Log.e("Tick ", "$click  $differenceTime")
                if (click < differenceTime) {
                    canSkipAd = true
                    textTimer.text = "Skip"
                     onFinish()

                } else {
                    textTimer.text = "skip in ${click / 1000}"
                }

            }

            override fun onFinish() {

                textTimer.text = "Skip"
               // adCompleted()
            }
        }
        timer.start()

    }


    companion object {

        var myListener: MyListener? = null

        fun setUpListener(Listener: MyListener) {
            myListener = Listener
        }


    }
    private fun adCompleted() {
        myListener!!.onSuccess()
        finish()

    }


    inner class WebViewController : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}




interface MyListener {
    fun onSuccess()
}





//Extensions

fun AppCompatImageView.load(context: Context, url: String) {
    Glide.with(context)
            .load(url)
            .skipMemoryCache(true)
            .into(this)
}


fun Int.milli(): Int {
    return this * 1000
}


fun View.show(){
    visibility = View.VISIBLE

}

/*fun View.gone(){
    visibility = View.GONE

}*/

fun View.vibrate(){
    val rotate = ObjectAnimator.ofFloat(
        this,
        "rotation",
        0f,
        15f,
        0f,
        -15f,
        0f
    ) // rotate o degree then 20 degree and so on for one loop of rotation.
// animateView (View object)
    rotate.repeatCount = 10 // repeat the loop 20 times
    rotate.duration = 5000 // animation play time 100 ms
    rotate.start()
}