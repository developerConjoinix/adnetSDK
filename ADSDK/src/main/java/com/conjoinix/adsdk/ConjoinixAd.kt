package com.conjoinix.adsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.io.Serializable


/**
 * Created by deepakkanyan on 2019-08-20 , 11:29.
 */
class ConjoinixAd {


    data class Builder(val context: Activity, var adKey: String? = null,
                       var adUrl: String? = null, var adType: String? = null
                       , var zoneID: String? = null, var screenType: String? = null) {


        fun build(onCallBack: (isSuccess: Boolean, msg: String) -> Unit?) {


            val param = HashMap<String, String>()

            param["apiKey"] = adKey!!
            param["adType"] = if (!adType.isNullOrBlank()) adType!! else "SPLASH"
            param["zoneID"] = if (!zoneID.isNullOrBlank()) zoneID!! else "0"
            param["screenType"] = if (!screenType.isNullOrBlank()) screenType!! else "SPLASH"

            run(param, onCallBack)
        }

        fun adKey(adKey: String) = apply { this.adKey = adKey }
        fun adType(adType: String) = apply { this.adType = adType }
        fun zoneID(zoneID: String) = apply { this.zoneID = zoneID }
        fun screenType(screenType: String) = apply { this.screenType = screenType }

        fun run(parameters: HashMap<String, String>,
                onCallBack: (isSuccess: Boolean, msg: String) -> Unit?) {

            val builder = FormBody.Builder()
            val it = parameters.entries.iterator()
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

                 onCallBack(false, "onFailure")

                }

                override fun onResponse(call: Call, response: Response) {
                    val responsea = response.body()!!.string().toString()
                    val json = Gson()
                    val data = json.fromJson(responsea, AdHeader::class.java)



                    context.runOnUiThread {


                        if (data.code == 1001) {
                            val bundle = Bundle()
                            bundle.putSerializable("data", data.data)
                            val intent = Intent(context, AdActivity::class.java)
                            intent.putExtra("Bundle", bundle)
                            context.startActivity(intent)
                            AdActivity.setUpListener(object : MyListener {
                                override fun onSuccess() {
                                    onCallBack(true, data.message)
                                    Log.e("onSuccess", "DEEPS{AL ")
                                }
                            })

                        } else {
                            onCallBack(false, data.message)
                        }
                    }


                }

            })
        }


    }


}


const val AD_URL = "http://adnetapi.conjoinix.com/User/"
const val AD_URL_CLICKED = "http://adnetapi.conjoinix.com/user/adclicked"

data class AdHeader(val code: Int, val message: String, val data: AdResponse? = null)
data class AdResponse(val logKey: String,

                      val pkgID: String,

                      val maxduration: Int,

                      val minDuration: Int,

                      val adType: String,

                      val adUrl: String,

                      val downloadUrl: String,

                      val allowedDays: String,

                      val screenType: String,


                      val saleContact: String,


                      val website: String,

                      val clickUrl: String) : Serializable
