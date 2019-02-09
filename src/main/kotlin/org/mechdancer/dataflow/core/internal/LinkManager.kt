package org.mechdancer.dataflow.core.internal

import org.mechdancer.dataflow.core.Feedback.DecliningPermanently
import org.mechdancer.dataflow.core.intefaces.ILink
import org.mechdancer.dataflow.core.intefaces.ISource
import org.mechdancer.dataflow.core.intefaces.ITarget
import org.mechdancer.dataflow.core.options.LinkOptions
import java.util.concurrent.ConcurrentSkipListSet

/**
 * 链接管理
 *
 * @param owner 管理器从属的源
 */
internal class LinkManager<T>(private val owner: ISource<T>) {
    // 链接表
    private val _set = ConcurrentSkipListSet<ILink<T>>()

    val set = object : Set<ILink<T>> by _set {}

    /**
     * 建立一个新的链接
     *
     * @param target  目标宿
     * @param options 链接选项
     * @return 新链接的引用
     */
    fun linkTo(target: ITarget<T>, options: LinkOptions<T>): ILink<T> =
        Link(owner, target, options).also(_set::plusAssign)

    /**
     * 向所有链接发送值
     */
    fun offer(id: Long, value: T) =
        _set.filter { it.options.predicate(value) }
            .groupBy { it offer id }
            .also { it[DecliningPermanently]?.let(_set::removeAll) }
            .keys
            .toList()
}
