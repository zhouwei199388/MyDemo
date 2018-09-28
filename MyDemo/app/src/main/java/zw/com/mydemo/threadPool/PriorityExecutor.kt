package zw.com.mydemo.threadPool

import java.util.Comparator
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by ZouWei on 2018/9/20.
 *
 * 自定义线程池
 */
class PriorityExecutor(
        corePoolSize: Int = 5,
        maximumPoolSize: Int = 256,
        keepAliveTime: Long = 1,
        unit: TimeUnit = TimeUnit.MINUTES,
        workQueue: PriorityBlockingQueue<Runnable> = PriorityBlockingQueue(256, LIFO),
        threadFactory: ThreadFactory = sThreadFactory)
    : ThreadPoolExecutor(corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        threadFactory) {

    companion object {
        enum class Priority {
            HIGH, NORMAL, LOW
        }

        /**
         * 线程队列方式 先进先出
         */
        private val FIFO = Comparator<Runnable> { lhs, rhs ->
            if (lhs is PriorityRunnable && rhs is PriorityRunnable) {
                val lpr = lhs as PriorityRunnable
                val rpr = rhs as PriorityRunnable
                val result = lpr.priority.ordinal - rpr.priority.ordinal
                if (result == 0) (lpr.SEQ - rpr.SEQ).toInt() else result
            } else {
                0
            }
        }
        /**
         * 线程队列方式 后进先出
         */
        private val LIFO = Comparator<Runnable> { lhs, rhs ->
            if (lhs is PriorityRunnable && rhs is PriorityRunnable) {
                val lpr = lhs as PriorityRunnable
                val rpr = rhs as PriorityRunnable
                val result = lpr.priority.ordinal - rpr.priority.ordinal
                if (result == 0) (rpr.SEQ - lpr.SEQ).toInt() else result
            } else {
                0
            }
        }
        /**
         * 创建线程工厂
         */
        private val sThreadFactory = object : ThreadFactory {
            val mCount = AtomicInteger(1)
            override fun newThread(r: Runnable?): Thread {
                return Thread(r, "download#${mCount.getAndIncrement()}")
            }
        }
        private val SEQ_SEED = AtomicLong(0)
    }


    fun isBusy(): Boolean {
        return activeCount >= corePoolSize
    }

    override fun execute(command: Runnable?) {
        if (command == null) return
        if (command is PriorityRunnable) {
            command.SEQ = SEQ_SEED.getAndIncrement()
        }
        super.execute(command)
    }

}