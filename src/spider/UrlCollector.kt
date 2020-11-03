package spider

import util.getProperty
import java.net.URL


class UrlCollector {
    val baseUrl = getProperty("baseUrl")
    val todo = HashSet<String>()
    val visited = HashSet<String>()

    fun traverseTodo() {
        while (todo.isNotEmpty()) {
            val strUrl = todo.iterator().next()
            val content = downLoadPageContent(strUrl)
            val newLinks:HashSet<String> = retrieveLinks(content, URL(strUrl))
            todo.addAll(newLinks)
            visited.add(strUrl)
        }
    }

    //遍历规则：深度优先，广度优先，最佳优先
    private fun retrieveLinks(content: String, url: URL): java.util.HashSet<String> {
        TODO("retriveLinks")
    }

    fun downLoadPageContent(strUrl: String): String {

        //todo:downLoadPageContent

        val content = ""
        return content
    }
}