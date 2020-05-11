package org.neotao.ch16.my.print.sample01;

import org.neotao.ch16.my.print.sample01.lock.PrintConst;
import org.neotao.ch16.my.print.sample01.sync.PrintLowerThread;
import org.neotao.ch16.my.print.sample01.sync.PrintNumThread;
import org.neotao.ch16.my.print.sample01.sync.PrintUpperThread;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程池写法。
 * 功能线程实现类参考 <p>org.neotao.ch16.my.print.sample01.sync</p> 包下
 */
public class ThreadPool {
    // ExecutorService threadPool = Executors.newCachedThreadPool();
    static ExecutorService threadPool = Executors.newFixedThreadPool(3);
    // ExecutorService threadPool = Executors.newScheduledThreadPool(3);
    // ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> flagMap = new ConcurrentHashMap<>();
        flagMap.put(PrintConst.FLAG, PrintConst.NUMBER);

        threadPool.submit(new PrintNumThread(flagMap, lock, condition));
        threadPool.submit(new PrintUpperThread(flagMap, lock, condition));
        threadPool.submit(new PrintLowerThread(flagMap, lock, condition));
    }
}
