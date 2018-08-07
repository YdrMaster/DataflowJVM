package site.syzk.dataflow.blocks

import site.syzk.dataflow.core.DefaultSource
import site.syzk.dataflow.core.IReceivable
import site.syzk.dataflow.core.ISource
import site.syzk.dataflow.core.ITarget
import site.syzk.dataflow.core.internal.LinkManager
import site.syzk.dataflow.core.internal.SourceCore
import site.syzk.dataflow.core.internal.TargetCore
import site.syzk.dataflow.core.internal.otherwise

/**
 * 转换模块
 * @param map 转换函数
 */
class TransformBlock<TIn, TOut>(
        private val map: (TIn) -> TOut
) : ITarget<TIn>, ISource<TOut>, IReceivable<TOut> {
    override val defaultSource = DefaultSource<TIn>()

    private val manager = LinkManager(this)

    //--------------------------
    // ITarget & ISource
    //--------------------------
    private val sourceCore = SourceCore<TOut>()
    private val targetCore = TargetCore<TIn> { event ->
        val out = map(event)
        val newId = sourceCore.offer(out)
        manager.targets
                .map { it.offer(newId, this) }
                .any { it.positive }
                .otherwise {
                    sourceCore.drop(newId)
                    synchronized(receiveLock) {
                        receivable = true
                        value = out
                        receiveLock.notifyAll()
                    }
                }
    }

    //--------------------------
    // IReceivable
    //--------------------------
    private val receiveLock = Object()
    private var receivable = false
    private var value: TOut? = null

    //--------------------------
    // Methods
    //--------------------------

    override fun offer(id: Long, source: ISource<TIn>) = targetCore.offer(id, source)
    override fun consume(id: Long) = sourceCore.consume(id)

    override fun linkTo(target: ITarget<TOut>) = manager.linkTo(target)
    override fun unlink(target: ITarget<TOut>) = manager.unlink(target)

    override fun receive(): TOut {
        synchronized(receiveLock) {
            while (!receivable) receiveLock.wait()
            receivable = false
            @Suppress("UNCHECKED_CAST") return value as TOut
        }
    }
}
