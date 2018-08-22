package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.SourceCore
import org.mechdancer.dataflow.core.internal.TargetCore
import org.mechdancer.dataflow.core.internal.otherwise
import java.util.*

/**
 * 缓冲模块
 * 未消耗的数据将保留，直到被消费
 */
class BufferBlock<T>(
        override val name: String = "buffer",
        size: Int = Int.MAX_VALUE)
    : IPropagatorBlock<T, T>, IReceivable<T> {
    override val uuid: UUID = UUID.randomUUID()
    override val defaultSource by lazy { DefaultSource(this) }

    private val receiveLock = Object()
    private val sourceCore = SourceCore<T>(size)
    private val targetCore = TargetCore<T> { event ->
        val newId = sourceCore.offer(event)
        Link[this]
                .filter { it.options.predicate(event) }
                .any { it.target.offer(newId, it).positive }
                .otherwise { synchronized(receiveLock) { receiveLock.notifyAll() } }
    }

    val count get() = sourceCore.bufferCount
    fun clear() = sourceCore.clear()

    override fun offer(id: Long, link: Link<T>) = targetCore.offer(id, link)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<T>, options: LinkOptions<T>) =
            Link(this, target, options)

    override fun receive(): T {
        synchronized(receiveLock) {
            var pair = sourceCore.consume()
            while (!pair.first) {
                receiveLock.wait()
                pair = sourceCore.consume()
            }
            @Suppress("UNCHECKED_CAST")
            return pair.second as T
        }
    }
}
