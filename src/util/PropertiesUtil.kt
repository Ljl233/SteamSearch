package util

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.util.*

const val propertiesUrl = "steam.properties"

fun getProperty(property: String): String {
    val inputStream = BufferedInputStream(FileInputStream(propertiesUrl))
    val p = Properties()
    p.load(inputStream)
    return p.getProperty(property)
}