package zw.com.mydemo.threadPool

import android.renderscript.RenderScript

/**
 * Created by ZouWei on 2018/9/20.
 */
class PriorityRunnable(
        var priority: PriorityExecutor.Companion.Priority = PriorityExecutor.Companion.Priority.NORMAL,
        var runnable: Runnable
) : Runnable {

    var SEQ: Long = 0
    override fun run() {
        runnable.run()
    }
}