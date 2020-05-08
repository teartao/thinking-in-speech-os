package org.neotao.ch16.my.print.sample01.sync;

import org.neotao.ch16.my.print.sample00.ReentrantLockDemo;
import org.neotao.ch16.my.print.sample01.lock.PrintConst;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用synchronized，并拆分打印线程写法
 */
public class DesignedRunnableWithLock {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> flagMap = new ConcurrentHashMap<>();
        flagMap.put(PrintConst.FLAG, PrintConst.NUMBER);

        new Thread(new PrintNumThread(flagMap, lock, condition), "PrintNumThread").start();
        new Thread(new PrintUpperThread(flagMap, lock, condition), "PrintUpperThread").start();
        new Thread(new PrintLowerThread(flagMap, lock, condition), "PrintLowerThread").start();
    }
}
