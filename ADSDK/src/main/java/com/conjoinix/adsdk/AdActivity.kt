package com.conjoinix.adsdk

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_ad.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.os.Handler
import okhttp3.*
import okio.Buffer
import java.io.IOException
import kotlin.math.abs


/**
 * Created by deepakkanyan on 2019-08-20 , 14:34.
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AdActivity : AppCompatActivity()  {

    companion object {

        var myListener: MyListener? = null

        fun setUpListener(Listener: MyListener) {
            myListener = Listener
        }


    }

    val IMAGE = "IMAGE"
    val VIDEO = "VIDEO"
    val WEB   = "HTML"

    interface MyListener {
        fun onSuccess(boolean: Boolean = true)
    }
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
                videoRl.setOnClickListener {
                    openUrl(model.clickUrl)
                }
                videoRl.show()
                initVideoView()
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


    /**
     * If ad is video Type
     * */

    private fun initVideoView() {
        prograss.show()
       requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
       Handler().postDelayed({
           videoView.setVideoURI(Uri.parse(model.adUrl))

           videoView.setOnPreparedListener {
               prograss.hide()
               startTimer()
               videoView.start()
           }

       },100)


        videoView.setOnCompletionListener {
            adCompleted(true)
            Log.e("videoView 1 ", "setOnCompletionListener")
        }





        videoView.setOnErrorListener {
            adCompleted(false)
            false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    private fun openUrl(url : String){

        adViewed()

        try {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(myIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application can handle this request." + " Please install a web browser", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun adViewed(){

        val param = HashMap<String, String>()

        param["apiKey"] = model.adID!!
        param["logKey"] = model.logKey

        val builder = FormBody.Builder()
        val it = param.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()


        val client = OkHttpClient()
        val request = Request.Builder()
            .url(AD_URL_CLICKED)
            .post(formBody)
            .build()


        val buffer = Buffer()
        request.body()?.writeTo(buffer)
        Log.e("Hi post ", buffer.readUtf8())
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                Log.e("onFailure" ,"Ad Cliked")
            }

            override fun onResponse(call: Call, response: Response) {

                Log.e("onResponse " ,"Ad Cliked ${response.body()!!.string()}")
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebsite()
    {
        web.settings.javaScriptEnabled = true
        web.loadUrl(model.adUrl)
        web.webViewClient = WebViewController()
    }

    private fun startTimer() {

        val  totalDuration = if(model.adType == VIDEO){
            videoView.duration
        }else{
            model.maxduration.milli().toLong()
        }

        Log.e("totalDuration ", "$totalDuration")

        val minDuration = model.minDuration.milli().toLong()


        val timer = object : CountDownTimer(totalDuration, 1000) {

            override fun onTick(click: Long) {


                if ((totalDuration-click) >= minDuration) {
                    canSkipAd = true
                    textTimer.text = "Skip"
                    onFinish()

                } else {
                    textTimer.text = "skip in ${abs(totalDuration - (click + minDuration)) / 1000}"
                }

            }

            override fun onFinish() {

                textTimer.text = "Skip"
               // adCompleted()
            }
        }
        timer.start()

    }

    private fun adCompleted(boolean: Boolean = true) {
        myListener!!.onSuccess(boolean)
        finish()

    }

    inner class WebViewController : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

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
    this.post {
        visibility = View.VISIBLE
    }


}

 fun View.hide(){
    visibility = View.GONE

}

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