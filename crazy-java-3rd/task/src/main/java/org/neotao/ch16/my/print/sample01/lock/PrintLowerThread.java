package org.neotao.ch16.my.print.sample01.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// class PrintLowerThread implements Runnable {
class PrintLowerThread extends Thread {
    int count;

    Lock lock;
    Condition condition;

    ConcurrentHashMap<String, Integer> map;

    public PrintLowerThread(Lock lock, Condition condition, ConcurrentHashMap<String, Integer> map) {
        this.lock = lock;
        this.condition = condition;
        this.map = map;
    }

    @Override
    public void run() {
        for (int i = 0; i < 26; i++) {
            printLower();
        }
    }

    private void printLower() {
        try {
            lock.lock();
            while (PrintConst.LOWER_CASE != map.get(PrintConst.FLAG)) {
                condition.await();
            }
            System.out.println((char) ('a' + count));
            count++;
            map.put(PrintConst.FLAG, PrintConst.NUMBER);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

