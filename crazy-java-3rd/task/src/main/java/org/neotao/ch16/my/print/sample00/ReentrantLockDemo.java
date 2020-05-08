package org.neotao.ch16.my.print.sample00;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main方法全局变量lock写法
 */
public class ReentrantLockDemo {

    private volatile boolean flag = true;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int num = 1;

    public void printNum() {
        try {
            lock.lock();
            while (!flag) {
                try {
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.print(num * 2 - 1);
            System.out.print(num * 2);

            flag = false;
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printABC() {
        try {
            lock.lock();
            while (flag) {
                try {
                    condition.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.print((char) ('A' + num - 1));
            num++;
            flag = true;
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                reentrantLockDemo.printNum();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                reentrantLockDemo.printABC();
            }
        }).start();
    }
}
