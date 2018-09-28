package zw.com.mydemo.threadPool;


import android.app.Activity;
import android.app.ActivityManager;
import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ZouWei on 2018/9/21.
 * <p>
 * <p>
 * Thread 多线程操作
 * <p>
 * main方法kotlin无法执行 所以用java
 */
public class ThreadDemo {

    private static volatile int mSize = 0;
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String args[]) {

        Thread thread = new Thread(myThread);
        Thread thread1 = new Thread(myThread);
        thread.start();
        thread1.start();
    }


    public static Runnable myThread = new Runnable() {
        @Override
        public void run() {
            addSize();
        }
    };


    public static void addSize() {
        lock.lock();
        for (int i = 0; i < 10; i++) {
            System.out.println("thread:" + Thread.currentThread().getName() + "mSize:" + mSize);
            mSize++;
            lock.unlock();
        }
    }


}
