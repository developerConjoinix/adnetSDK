package com.conjoinix.adsdk

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.santalu.autoviewpager.AutoViewPager
import okhttp3.*
import okio.Buffer
import java.io.IOException


/**
 * Created by deepakkanyan on 2019-09-02 , 15:24.
 */
class BannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {


    lateinit var mContext: Activity
    lateinit var viewPager: AutoViewPager



    lateinit var adKey: String
    private var latitude: String? = "0"
    private var longitude: String? = "0"

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.banner_ad, this)
        viewPager = view.findViewById(R.id.viewpager)
        adKey = a.getString(R.styleable.BannerView_adKey)!!
        latitude = a.getString(R.styleable.BannerView_latitude)
        longitude = a.getString(R.styleable.BannerView_longitude)

        a.recycle()

    }


   public fun setDuration(time : Int){

       viewPager.duration = time
    }

    fun loadAd(activity: Activity, onload : (isloaded : Boolean) -> Unit) {


        mContext = activity
        val param = HashMap<String, String>()
        param["apiKey"] = adKey
        param["adType"] = "BANNERCOUPON"
        param["zoneID"] = "0"
        param["screenType"] = "BANNERCOUPON"

        param["latitude"] = if (!latitude.isNullOrBlank()) {
            latitude!!
        } else "0"


        param["longitude"] = if (!longitude.isNullOrBlank()) longitude!! else "0"

        param["isBulk"] = "1"

        val builder = FormBody.Builder()
        val it = param.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()


        val client = OkHttpClient()
        val request = Request.Builder()
            .url(AD_URL)
            .post(formBody)
            .build()


        val buffer = Buffer()
        request.body()?.writeTo(buffer)

        Log.e("Hi post ", buffer.readUtf8())

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {


                Log.e("Banner Ad  ", "Error to load ad")
                onload(false)

            }

            override fun onResponse(call: Call, response: Response) {
                val responsea = response.body()!!.string().toString()


                val json = Gson()
                val data = json.fromJson(responsea, AdHeaderMulti::class.java)



                if (data.code == 1001) {
                    onload(true)
                    mContext.runOnUiThread {


                        val myData =  data.data!!

                       if(myData.size  > 1){
                           for( i in 1..5)
                           {
                               myData.addAll(myData)
                           }
                       }


                        val adapter = MyViewPagerAdapter(mContext, myData)
                        viewPager.adapter = adapter
                    }


                } else {
                    onload(false)
                    Log.e("Banner Ad  ", data.message)
                }


            }

        })
    }


}


class MyViewPagerAdapter(
    private val activity: Activity,
    private val data: List<AdResponse>
) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(activity).inflate(R.layout.image_adapter, null)

        val image = view.findViewById<AppCompatImageView>(R.id.adImage)

        val ref = data[position]

        image.setOnClickListener {

            activity.openUrl(ref.clickUrl)
        }


        image.load(activity, ref.adUrl)


        container.addView(view)



        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as AppCompatImageView?)
    }

    override fun getCount(): Int {

        return data.size
    }


}

private fun Context.openUrl(url : String){



    try {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(myIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No application can handle this request." + " Please install a web browser", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}