package org.neotao.ch16.my.print.sample01.sync;

import org.neotao.ch16.my.print.sample01.lock.PrintConst;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PrintNumThread extends AbstractPrintThread {

    public PrintNumThread(Map<String, Integer> flagMap, Lock lock, Condition condition) {
        super(flagMap, lock, condition);
    }

    @Override
    protected void printContent() {
        System.out.print(2 * count + 1);
        System.out.print(2 * count + 2 + " ");
    }

    @Override
    public int myFlag() {
        return PrintConst.NUMBER;
    }

    @Override
    protected int nextFlag() {
        return PrintConst.UPPER_CASE;
    }
}
