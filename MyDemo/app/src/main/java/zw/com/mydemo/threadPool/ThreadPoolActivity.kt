package zw.com.mydemo.threadPool

import android.os.Bundle
import android.renderscript.RenderScript
import android.support.v7.app.AppCompatActivity
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_thread.*
import zw.com.mydemo.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by ZouWei on 2018/9/19.
 */
class ThreadPoolActivity : AppCompatActivity() {
    private lateinit var mExecutorService: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)
        init()
    }

    private fun init() {
        fixedThreadPool.setOnClickListener {
            //该方法返回一个固定线程数量的线程池，
            // 该线程池中的线程数量始终不变，
            // 即不会再创建新的线程，也不会销毁已经创建好的线程，
            // 自始自终都是那几个固定的线程在工作，所以该线程池可以控制线程的最大并发数。
            mExecutorService = Executors.newFixedThreadPool(3)
            addRunnable()
        }
        cacheThreadPool.setOnClickListener {
            //该方法返回一个可以根据实际情况调整线程池中线程的数量的线程池。
            // 即该线程池中的线程数量不确定，是根据实际情况动态调整的。
            mExecutorService = Executors.newCachedThreadPool()
            addRunnable()
        }
        scheduleThreadPool.setOnClickListener {
            //该方法返回一个可以控制线程池内线程定时或周期性执行某任务的线程池。
            mExecutorService = Executors.newScheduledThreadPool(5)
            addRunnable()
        }

        singleThreadPool.setOnClickListener {
            //该方法返回一个只有一个线程的线程池，即每次只能执行一个线程任务，
            // 多余的任务会保存到一个任务队列中，等待这一个线程空闲，
            // 当这个线程空闲了再按FIFO方式顺序执行任务队列中的任务。
            mExecutorService = Executors.newSingleThreadExecutor()
            addRunnable()
        }

        myThreadPool.setOnClickListener {
            mExecutorService = PriorityExecutor()
            addMyRunnable()
        }
    }


    private fun addMyRunnable() {
        for (i in 1..20) {
            val priorityRunnable: PriorityRunnable = when {
                (i % 3 == 1) -> PriorityRunnable(PriorityExecutor.Companion.Priority.HIGH, Runnable {
                    Log.d("${Thread.currentThread().name} 优先级高")
                })
                (i % 5 == 0) -> PriorityRunnable(PriorityExecutor.Companion.Priority.LOW, Runnable {
                    Log.d("${Thread.currentThread().name} 优先级低")
                })
                else -> {
                    PriorityRunnable(PriorityExecutor.Companion.Priority.NORMAL, Runnable {
                        Log.d("${Thread.currentThread().name} 优先级正常")
                    })
                }
            }
            mExecutorService.execute(priorityRunnable)
        }
    }

    private fun addRunnable() {
        for (i in 0..10) {
            mExecutorService.execute(TestRunnable(i))
        }
    }


    class TestRunnable(var priority: Int) : Runnable {
        override fun run() {
            val threadName = Thread.currentThread().name
            Log.d("线程：$threadName 正在执行优先级为$priority 的线程")
            Thread.sleep(1000 * 4)
            Log.d("线程：$threadName 已经执行完优先级为$priority 的线程")
        }
    }
}