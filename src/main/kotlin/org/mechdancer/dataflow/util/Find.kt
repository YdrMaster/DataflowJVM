//package org.mechdancer.dataflow.util
//
//import org.mechdancer.dataflow.core.intefaces.IBlock
//import org.mechdancer.dataflow.core.intefaces.ILink
//import org.mechdancer.dataflow.core.intefaces.ISource
//import org.mechdancer.dataflow.core.intefaces.ITarget
//
///** 查找一级前驱节点 */
//fun ITarget<*>.prior(): List<IBlock> =
//    ILink.list
//        .filter { it.target === this }
//        .map { it.source }
//
///** 查找一级后继节点 */
//fun ISource<*>.next(): List<IBlock> =
//    ILink.list
//        .filter { it.source === this }
//        .map { it.target }
//
//private infix fun ITarget<*>.recordPriorTo(list: MutableList<IBlock>) {
//    if (this in list) return
//    this.prior().forEach {
//        if (it !in list) {
//            list.add(it)
//            (it as? ITarget<*>)?.recordPriorTo(list)
//        }
//    }
//}
//
//private infix fun ISource<*>.recordNextTo(list: MutableList<IBlock>) {
//    if (this in list) return
//    this.next().forEach {
//        if (it !in list) {
//            list.add(it)
//            (it as? ISource<*>)?.recordNextTo(list)
//        }
//    }
//}
//
///** 列出所有前驱 */
//fun ITarget<*>.allPrior() =
//    mutableListOf<IBlock>()
//        .also { this recordPriorTo it }
//        .toList()
//
///** 列出所有后继 */
//fun ISource<*>.allNext() =
//    mutableListOf<IBlock>()
//        .also { this recordNextTo it }
//        .toList()
//
///** 根据链接集找到所有纯源 */
//fun Iterable<ILink<*>>.pureSources() =
//    this.map { it.target as? IBlock }
//        .distinct()
//        .let { targets ->
//            this.map { it.source }
//                .distinct()
//                .filter { it !in targets }
//        }
