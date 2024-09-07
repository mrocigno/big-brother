package br.com.mrocigno.bigbrother.database

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParser.TEXT
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File

class SharedPreferencesHelper(
    private val file: File
) {

    val name: String = file.name
    val data: HashMap<String, Any> = hashMapOf()

    private val parser = XmlPullParserFactory.newInstance().newPullParser()

    fun parse() {
        parser.setInput(file.inputStream(), "UTF-8")
        do {
            val eventType = parser.next()
            val type = parser.name?.takeIf { eventType == START_TAG } ?: continue
            when (type) {
                "string" -> getString()
                "int" -> getInt()
                "long" -> getLong()
                "boolean" -> getBoolean()
                "float" -> getFloat()
                "set" -> getStringSet()
            }
        } while (eventType != XmlPullParser.END_DOCUMENT)
    }

    fun clear() = data.clear()

    private fun getString() {
        val propertyName = parser.getAttributeValue(null, "name")
        parser.next()
        val propertyValue = parser.text
        data[propertyName] = propertyValue
    }

    private fun getStringSet() {
        val set = mutableSetOf<String>()
        val propertyName = parser.getAttributeValue(null, "name")
        do {
            val eventType = parser.next()
            val value = parser.text?.trim()?.takeIf { eventType == TEXT && it.isNotBlank() } ?: continue
            set.add(value)
        } while (eventType != END_TAG || parser.name != "set")
        data[propertyName] = set
    }

    private fun getInt() {
        val propertyName = parser.getAttributeValue(null, "name")
        val propertyValue = parser.getAttributeValue(null, "value")
        data[propertyName] = propertyValue.toInt()
    }

    private fun getLong() {
        val propertyName = parser.getAttributeValue(null, "name")
        val propertyValue = parser.getAttributeValue(null, "value")
        data[propertyName] = propertyValue.toLong()
    }

    private fun getBoolean() {
        val propertyName = parser.getAttributeValue(null, "name")
        val propertyValue = parser.getAttributeValue(null, "value")
        data[propertyName] = propertyValue.toBoolean()
    }

    private fun getFloat() {
        val propertyName = parser.getAttributeValue(null, "name")
        val propertyValue = parser.getAttributeValue(null, "value")
        data[propertyName] = propertyValue.toFloat()
    }
}