package org.neotao.ch16.my.print.sample01.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class PrintUpperThread extends Thread {
    // class PrintUpperThread implements Runnable {
    int count;

    Lock lock;
    Condition condition;
    ConcurrentHashMap<String, Integer> map;

    public PrintUpperThread(Lock lock, Condition condition, ConcurrentHashMap<String, Integer> map) {
        this.lock = lock;
        this.condition = condition;
        this.map = map;
    }

    @Override
    public void run() {
        for (int i = 0; i < 26; i++) {
            printUpper();
        }
    }

    private void printUpper() {
        try {
            lock.lock();
            while (PrintConst.UPPER_CASE != map.get(PrintConst.FLAG)) {
                condition.await();
            }
            System.out.println((char) ('A' + count));
            count++;
            map.put(PrintConst.FLAG, PrintConst.LOWER_CASE);

            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
