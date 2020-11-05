package spider

import util.getProperty
import util.getUrlFromHtml
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL


class UrlCollector {
    val baseUrl = getProperty("baseUrl")
    val todo = HashSet<String>()
    val visited = HashSet<String>()

    // traverse the url list to get all the urls
    fun traverseTodo() {
        while (todo.isNotEmpty()) {
            val strUrl = todo.iterator().next()
            val content = downLoadPageContent(strUrl)
            val newLinks: HashSet<String> = retrieveLinks(content, URL(strUrl))
            todo.addAll(newLinks)
            visited.add(strUrl)
        }
    }

    //遍历规则：深度优先✔，广度优先，最佳优先
    private fun retrieveLinks(content: String, url: URL): HashSet<String> {
        return getUrlFromHtml(content)
    }

    //download the content of web page
    private fun downLoadPageContent(strUrl: String): String {
        val pageUrl = URL(strUrl)
        val reader = BufferedReader(InputStreamReader(pageUrl.openStream()))
        var content = ""
        val sb = StringBuilder()
        content = reader.readLine()
        while (content != null) {
            sb.append(content)
            content = reader.readLine()
        }
        return sb.toString()
    }
}