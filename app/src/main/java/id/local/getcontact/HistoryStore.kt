package id.local.getcontact

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryEntry(val number: String, val mode: String, val time: String)

class HistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("lookup_history", Context.MODE_PRIVATE)
    fun all(): List<HistoryEntry> {
        val array = JSONArray(prefs.getString("items", "[]"))
        return (0 until array.length()).map {
            val o = array.getJSONObject(it)
            HistoryEntry(o.getString("number"), o.getString("mode"), o.getString("time"))
        }.reversed()
    }
    fun add(number: String, mode: String, result: String) {
        val array = JSONArray()
        all().reversed().take(49).forEach { e ->
            array.put(JSONObject().put("number", e.number).put("mode", e.mode).put("time", e.time))
        }
        val time = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date())
        array.put(JSONObject().put("number", number).put("mode", mode).put("time", time))
        prefs.edit().putString("items", array.toString()).apply()
    }
    fun clear() { prefs.edit().remove("items").apply() }
}
