import org.mechdancer.dataflow.core.broadcast
import org.mechdancer.dataflow.core.minus
import org.mechdancer.dataflow.core.post

fun main(args: Array<String>) {
    val source = broadcast<Int>()
    val link = source - { it - 1 } - source
    source - { it + 1 } - source
    val time = System.currentTimeMillis()
    source - { println(link.count / (System.currentTimeMillis() - time)) }
    source post 100
    while (true) {
        readLine()
        println("收到: ${source.receive()}")
    }
}
