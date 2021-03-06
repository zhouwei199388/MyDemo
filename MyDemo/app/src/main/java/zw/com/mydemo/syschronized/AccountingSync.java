package zw.com.mydemo.syschronized;


/**
 * Created by ZouWei on 2018/9/26.
 */
public class AccountingSync implements Runnable {

    static int i = 0;

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            increase();
        }
    }

    private void increase() {
        synchronized (this) {
            i++;
            System.out.println("Thread:" + Thread.currentThread().getName() + "  i:" + i);
        }
    }

    public static void main(String[] args) throws Throwable {
        AccountingSync accountingSync = new AccountingSync();
        AccountingSync accountingSync1 = new AccountingSync();
        Thread thread = new Thread(accountingSync);
        Thread thread1 = new Thread(accountingSync1);

        thread.start();
        thread1.start();
//        thread.join();
//        thread1.join();
//        System.out.print(i);
    }


}
