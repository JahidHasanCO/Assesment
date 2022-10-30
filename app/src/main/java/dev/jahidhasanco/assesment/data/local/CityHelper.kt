package dev.jahidhasanco.assesment.data.local


import android.content.Context
import android.util.Log.d
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONObject
import java.io.IOException


fun getCountryToCity(countryName: String, context: Context): List<String> {
    var city: List<String> = listOf()
    try {
        val obj = getJsonDataFromAsset(context, "countriesToCities.json")?.let { JSONObject(it) }
        val array = obj?.getJSONArray(countryName)
        Gson().fromJson<List<String>>(array.toString(), object : TypeToken<List<String>>() {}.type)
            ?.let {
                city = it
            }
    } catch (ioException: IOException) {
        d("TAG", "getCountryToCity: $ioException")
    }

    return city
}

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}