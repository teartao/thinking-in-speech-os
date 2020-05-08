package org.neotao.ch16.my.tickets.sample00;

import org.neotao.ch16.my.tickets.TicketUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock实现
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {
        Tickets tickets = new Tickets();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                tickets.sales();
                TicketUtils.waitMoment();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                tickets.sales();
                TicketUtils.waitMoment();
            }
        }, "B").start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                tickets.sales();
                TicketUtils.waitMoment();
            }
        }, "C").start();
    }
}


class Tickets {
    private int num = 300;
    private Lock lock = new ReentrantLock();

    public void sales() {
        try {
            lock.lock();
            if (num > 0) {
                num--;
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (300 - num) + "张票，" + "剩余" + num + "张票");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}