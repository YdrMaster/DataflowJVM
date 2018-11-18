//package org.mechdancer.dataflow.external.eventbus
//
//import java.util.concurrent.Executor
//import kotlin.reflect.KClass
//
//interface EventBus {
//    /**
//     * 注册一个类中所有具有 [@Subscribe] 标记的函数
//     * @param receiver 类的实例
//     */
//    fun register(receiver: Any)
//
//    /**
//     * 取消注册一个类中所有具有 [@Subscribe] 标记的函数
//     * @param receiver 类的实例
//     */
//    fun unregister(receiver: Any)
//
//    /**
//     * 发布一个事件
//     * @param event 事件
//     */
//    fun post(event: IEvent)
//
//    /**
//     * 发布一个粘性事件
//     * 粘性事件一直保留在系统中，除非手动移除。
//     * `sticky` 为 `true` 的订阅者订阅时会额外调用粘性事件，
//     * 这样可以先发布事件，后订阅的订阅者也可消费该事件。
//     *
//     * 每种类型的粘性事件是唯一的，在系统中只会留下一个。
//     */
//    fun postSticky(event: IEvent)
//
//    /**
//     * 通过事件类型获取一个粘性事件
//     * @param kClass 事件类型
//     * @return 如果未找到，则返回 `null`
//     */
//    fun getStickyEvent(kClass: KClass<out IEvent>): IEvent?
//
//    /**
//     * 通过事件类型移除一个粘性事件
//     * @param kClass 事件类型
//     * @return 是否移除成功
//     */
//    fun removeStickyEvent(kClass: KClass<out IEvent>): Boolean
//
//    /**
//     * 移除全部粘性事件
//     */
//    fun removeAllStickyEvents()
//
//    companion object {
//        const val DefaultExecutor = "Default"
//
//        val executors = mutableMapOf<String, Executor?>()
//
//        private val defaultInstance: EventBus by lazy { EventBusImpl() }
//
//        fun getDefault() = defaultInstance
//    }
//}
