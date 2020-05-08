package org.neotao.ch16.my.print.sample01.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用lock，并拆分打印线程 写法
 */
public class AppExtendsThread {
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    //存放当前执行的打印线程的map，用于线程间数据共享和通信，以便线程控制其它打印线程的运行和停止
    static ConcurrentHashMap<String, Integer> printThreadFlagMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        printThreadFlagMap.put(PrintConst.FLAG, PrintConst.NUMBER);

        PrintNumThread printNumThread = new PrintNumThread(lock, condition, printThreadFlagMap);
        PrintUpperThread printUpperThread = new PrintUpperThread(lock, condition, printThreadFlagMap);
        PrintLowerThread printLowerThread = new PrintLowerThread(lock, condition, printThreadFlagMap);

        printNumThread.start();
        printUpperThread.start();
        printLowerThread.start();
    }

}

