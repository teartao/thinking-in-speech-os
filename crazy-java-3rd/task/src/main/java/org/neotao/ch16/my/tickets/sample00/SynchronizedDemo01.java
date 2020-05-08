package org.neotao.ch16.my.tickets.sample00;

import org.neotao.ch16.my.tickets.TicketUtils;

/**
 * 多线程问题-多线程售票
 **/

public class SynchronizedDemo01 {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                ticket.sales();
                TicketUtils.waitMoment();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                ticket.sales();
                TicketUtils.waitMoment();
            }
        }, "B").start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                ticket.sales();
                TicketUtils.waitMoment();
            }
        }, "C").start();
    }

}

class Ticket {
    private int num = 300;

    public synchronized void sales() {
        if (num > 0) {
            num--;//卖出
            System.out.println(Thread.currentThread().getName() + "卖出了第" + (300 - num) + "张票，" + "剩余" + num + "张票");
        }
    }
}