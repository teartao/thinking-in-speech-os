package org.neotao.ch16.my.print.sample01.sync;

import org.neotao.ch16.my.print.sample01.lock.PrintConst;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public abstract class AbstractPrintThread implements Runnable {
    protected Map<String, Integer> flagMap;
    protected Lock lock;
    protected Condition condition;
    protected int count = 0;

    public AbstractPrintThread(Map<String, Integer> flagMap, Lock lock, Condition condition) {
        this.flagMap = flagMap;
        this.lock = lock;
        this.condition = condition;
    }
    @Override
    public void run() {
        loopPrint();
    }

    private void loopPrint() {
        for (int i = 0; i < 26; i++) {
            lockPrint();
            count++;
        }
    }

    private void lockPrint() {
        try {
            lock.lock();
            while (myFlag() != flagMap.get(PrintConst.FLAG)) {
                condition.await();
            }
            printContent();
            flagMap.put(PrintConst.FLAG, nextFlag());
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }


    //获取flag状态
    protected abstract int myFlag();

    protected abstract void printContent();

    protected abstract int nextFlag();



}
