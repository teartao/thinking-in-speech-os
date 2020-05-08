package org.neotao.ch16.my.print.sample01;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * reentrantLock 写法
 */
public class ReentrantLockMain {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int count = 1;
    private int printFlag = 0;

    public static void main(String[] args) {
        ReentrantLockMain lockDemo01 = new ReentrantLockMain();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                lockDemo01.printNumber();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                lockDemo01.printUpper();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                lockDemo01.printLower();
            }
        }).start();
    }

    public void printNumber() {
        try {
            lock.lock();
            while (printFlag != 0) {
                condition.await();
            }
            System.out.print(2 * count - 1);
            System.out.print(2 * count);
            count++;
            printFlag = 1;
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printUpper() {
        try {
            lock.lock();
            while (printFlag != 1) {
                condition.await();
            }
            System.out.print((char) ('A' + count - 2));
            printFlag = 2;
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printLower() {
        try {
            lock.lock();
            while (printFlag != 2) {
                condition.await();
            }
            System.out.print((char) ('a' + count - 2));
            printFlag = 0;
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
