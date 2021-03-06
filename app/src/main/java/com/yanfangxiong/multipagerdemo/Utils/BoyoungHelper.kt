package com.yanfangxiong.multipagerdemo.Utils


import android.content.Context
import android.graphics.Movie
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object BoyoungHelper {

    //enum
    enum class BoyoungKey(val KEY_ID: String, val KEY_POSTER_URI: String, val KEY_TITLE: String, val KEY_OVERVIEW: String){
        INTENT("id", "imageUri", "title", "overview")
    }

    fun getBoyoungFromJson(fileName: String, context: Context): ArrayList<Boyoung> {

        val boyoung = ArrayList<Boyoung>()

        try {
            // Load the JSONArray from the file
            CustomLog.d(fileName)
            val jsonString = loadJsonFromFile(fileName, context)
            CustomLog.d(jsonString)
            val json = JSONObject(jsonString)
            val jsonboyoung = json.getJSONArray("boyoung")

            // Create the list of Boyoung
            for (index in 0 until jsonboyoung.length()) {
                val boyoungId = jsonboyoung.getJSONObject(index).getString(BoyoungKey.INTENT.KEY_ID)
                val boyoungTitle = jsonboyoung.getJSONObject(index).getString(BoyoungKey.INTENT.KEY_TITLE)
                val boyoungPosterUri = jsonboyoung.getJSONObject(index).getString(BoyoungKey.INTENT.KEY_POSTER_URI)
                val boyoungOverview = jsonboyoung.getJSONObject(index).getString(BoyoungKey.INTENT.KEY_OVERVIEW)

                boyoung.add(Boyoung(boyoungId, boyoungPosterUri ,boyoungTitle, boyoungOverview))
            }
        } catch (e: JSONException) {
            return boyoung
        }

        return boyoung
    }

    private fun loadJsonFromFile(filename: String, context: Context): String {
        var json = ""

        try {
            CustomLog.d(filename)
            val jsonfilename = String.format("%s.json",filename )
            val input = context.assets.open(jsonfilename )

            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = buffer.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}