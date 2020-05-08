package org.neotao.ch16.my.print.sample01.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class PrintNumThread extends Thread {
    // class PrintNumThread implements Runnable {
    int count = 0;
    Lock lock;
    Condition condition;
    ConcurrentHashMap<String, Integer> map;

    public PrintNumThread(Lock lock, Condition condition, ConcurrentHashMap<String, Integer> map) {
        this.lock = lock;
        this.condition = condition;
        this.map = map;
    }


    @Override
    public void run() {
        for (int i = 0; i < 26; i++) {
            printNum();
        }
    }

    private void printNum() {
        try {
            lock.lock();
            while (PrintConst.NUMBER != map.get(PrintConst.FLAG)) {
                condition.await();
            }
            System.out.println(2 * count + 1);
            System.out.println(2 * count + 2);
            count++;

            map.put(PrintConst.FLAG, PrintConst.UPPER_CASE);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
