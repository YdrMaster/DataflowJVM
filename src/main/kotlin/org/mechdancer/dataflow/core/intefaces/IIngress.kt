package org.mechdancer.dataflow.core.intefaces

import org.mechdancer.dataflow.core.Feedback

/**
 * Event entry
 *
 * Represents a object can notify target event coming
 *
 * 事件入口
 *
 * 可通知其事件到达的端口
 */
interface IIngress<T> {
    /**
     * Notify target that some source linked to this entry passing a event.
     * Called by source linked to this entry.
     *
     * 通知目标节点，链接了该节点的某个源有事件到来
     * 由链接节点的源调用
     *
     * @param id event id 源内部事件的唯一标识，供节点查找
     * @param egress exit of this event 事件到来的出口
     * @return feedback of this event 入口对事件的态度
     */
    fun offer(id: Long, egress: IEgress<T>): Feedback
}
