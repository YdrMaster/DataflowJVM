package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.IReceivable
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.internal.LinkManager
import site.syzk.dataflow.core.internal.SourceCore
import site.syzk.dataflow.core.internal.TargetCore
import site.syzk.dataflow.core.internal.otherwise

class BufferBlock<T> : ITarget<T>, ISource<T>, IReceivable<T> {
    override val defaultSource = DefaultSource<T>()

    private val manager = LinkManager(this)
    private val receiveLock = Object()
    private val sourceCore = SourceCore<T>()
    private val targetCore = TargetCore<T> { event ->
        val newId = sourceCore.offer(event)
        manager.targets
                .map { it.offer(newId, this) }
                .any { it.positive }
                .otherwise {
                    synchronized(receiveLock) {
                        receiveLock.notifyAll()
                    }
                }
    }

    val count get() = sourceCore.bufferCount

    override fun offer(id: Long, source: ISource<T>) = targetCore.offer(id, source)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<T>) = manager.linkTo(target)
    override fun unlink(target: ITarget<T>) = manager.unlink(target)

    override fun receive(): T {
        synchronized(receiveLock) {
            var pair = sourceCore.consumeFirst()
            while (!pair.first) {
                receiveLock.wait()
                pair = sourceCore.consumeFirst()
            }
            @Suppress("UNCHECKED_CAST") return pair.second as T
        }
    }
}
