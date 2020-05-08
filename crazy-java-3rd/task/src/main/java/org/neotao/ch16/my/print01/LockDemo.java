package org.neotao.ch16.my.print01;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock写法
 */
public class LockDemo {
    private volatile boolean flag = true;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void sub() {
        try {
            lock.lock();
            while (!flag) {
                try {
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 2; i++) {
                System.out.println("sub run" + i);
            }
            flag = false;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void main() {
        try {
            lock.lock();
            while (flag) {
                try {
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 2; i++) {
                System.out.println("main run" + i);
            }
            flag = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LockDemo lockDemo = new LockDemo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    lockDemo.sub();
                }
            }
        }).start();
        for (int i = 0; i < 10; i++) {
            lockDemo.main();
        }
    }
}


