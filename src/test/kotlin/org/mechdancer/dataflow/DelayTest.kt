package org.mechdancer.dataflow

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mechdancer.dataflow.core.delayBlock
import org.mechdancer.dataflow.core.post

class DelayTest {
    /** 测试延时模块 */
    @Test
    fun test() = runBlocking {
        val delay = delayBlock<Int>(time = 2000)
        delay post 10
        System.nanoTime()
            .also { delay.receive() }
            .let {
                val time = (System.nanoTime() - it) / 1E9
                println(time)
                Assert.assertEquals(2.0, time, 0.1)
            }
    }
}

