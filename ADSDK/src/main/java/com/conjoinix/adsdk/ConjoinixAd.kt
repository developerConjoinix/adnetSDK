package com.conjoinix.adsdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.io.Serializable


/**
 * Created by deepakkanyan on 2019-08-20 , 11:29.
 */
class ConjoinixAd {


    data class Builder(
        val context: Activity, var adKey: String? = null,
        var adUrl: String? = null, var adType: String? = null
        , var zoneID: String? = null, var screenType: String? = null,
        var isBulk: String? = null, var latitude : Double? = 0.0 ,
        var longitude : Double? = 0.0
    ) {


        fun build(onCallBack: (isSuccess: Boolean, msg: String) -> Unit?) {


            val param = HashMap<String, String>()
            param["apiKey"] = adKey!!
            param["adType"] = if (!adType.isNullOrBlank()) adType!! else "SPLASH"
            param["zoneID"] = if (!zoneID.isNullOrBlank()) zoneID!! else "0"
            param["screenType"] = if (!screenType.isNullOrBlank()) screenType!! else "SPLASH"
            run(param, onCallBack)
        }


        fun buildBulk(onCallBack: (isSuccess: Boolean, data: ArrayList<AdResponse>?) -> Unit?) {


            val param = HashMap<String, String>()
            param["apiKey"] = adKey!!
            param["adType"] = if (!adType.isNullOrBlank()) adType!! else "BANNERCOUPON"
            param["zoneID"] = if (!zoneID.isNullOrBlank()) zoneID!! else "0"
            param["screenType"] = if (!screenType.isNullOrBlank()) screenType!! else "BANNERCOUPON"

            param["latitude"] = if (!"$latitude".isBlank()) "$latitude" else "0"
            param["longitude"] = if (!"$longitude".isBlank()) "$longitude" else "0"
            param["isBulk"] = if (!"$isBulk".isBlank()) "$isBulk" else "1"

            getBulk(param, onCallBack)

        }

        fun adKey(adKey: String) = apply { this.adKey = adKey }
        fun adType(adType: String) = apply { this.adType = adType }
        fun zoneID(zoneID: String) = apply { this.zoneID = zoneID }
        fun screenType(screenType: String) = apply { this.screenType = screenType }

        fun isBulk(isBulk: String)       = apply { this.isBulk = isBulk }
        fun latitude(latitude: Double)   = apply { this.latitude = latitude }
        fun longitude(longitude: Double) = apply { this.longitude = longitude }

        fun run(
            parameters: HashMap<String, String>,
            onCallBack: (isSuccess: Boolean, msg: String) -> Unit? ) {

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

            //  Log.e("Hi post ", buffer.readUtf8())

            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {

                    context.runOnUiThread {
                        onCallBack(false, "Ad is failed to load ")
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    val responsea = response.body()!!.string().toString()
                    val json = Gson()
                    val data = json.fromJson(responsea, AdHeader::class.java)



                    context.runOnUiThread {


                        if (data.code == 1001) {
                            val bundle = Bundle()
                            val adInfo = data.data
                            adInfo?.adID = adKey
                            Log.e("Hlo Hlo","${adInfo?.adID}")
                            bundle.putSerializable("data", data.data)
                            val intent = Intent(context, AdActivity::class.java)
                            intent.putExtra("Bundle", bundle)
                            context.startActivity(intent)
                            AdActivity.setUpListener(object : AdActivity.MyListener {
                                override fun onSuccess(boolean: Boolean) {

                                    onCallBack(boolean, data.message)

                                }
                            })

                        } else {
                            onCallBack(false, "Ad is failed to load ")
                        }
                    }


                }

            })
        }



        fun getBulk(
            parameters: HashMap<String, String>,
            onCallBack: (isSuccess: Boolean, data: ArrayList<AdResponse>?) -> Unit? )
            {

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

                    context.runOnUiThread {
                        onCallBack(false, null)
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    val responsea = response.body()!!.string().toString()

                    Log.e("DDDD ", responsea)
                    val json = Gson()
                    val data = json.fromJson(responsea, AdHeaderMulti::class.java)



                    context.runOnUiThread {


                        if (data.code == 1001) {

                            onCallBack(true, data.data)

                        } else {
                            onCallBack(false, null)
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

data class AdHeaderMulti(val code: Int, val message: String, val data: ArrayList<AdResponse>? = null)
data class AdResponse(
    var adID: String?,//Custom Added key
    val logKey: String,
    val pkgID: String,
    val maxduration: Int,
    val minDuration: Int,
    val adType: String,
    var adUrl: String,
    val downloadUrl: String,
    val allowedDays: String,
    val screenType: String,
    val saleContact: String,
    val website: String,
    val clickUrl: String
) : Serializable
