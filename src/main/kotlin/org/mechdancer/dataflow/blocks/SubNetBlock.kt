package org.mechdancer.dataflow.blocks

import org.mechdancer.dataflow.core.*
import org.mechdancer.dataflow.core.internal.view
import java.util.*

class SubNetBlock<TIn, TOut>(
    override val name: String = "SubNetBlock",
    private val i: ITarget<TIn>,
    private val o: ISource<TOut>,
    links: List<LinkInfo<*>>
) : IPropagatorBlock<TIn, TOut> {
    override val uuid = UUID.randomUUID()
    override val defaultSource = DefaultSource(this)

    init {
        assert(
            links.any { it.source === i } && links.any { it.target === o }
        ) { "子网端口需要属于子网" }
        links.forEach { it buildIn view() }
    }

    override fun offer(id: Long, link: Link<TIn>) = i.offer(id, link)

    override fun consume(id: Long) = o.consume(id)

    override fun linkTo(target: ITarget<TOut>, options: LinkOptions<TOut>) =
        Link(o, target, options)
    //TODO 如何隐藏子网出口？
}
