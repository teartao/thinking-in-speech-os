package org.neotao.ch16.my.print.sample01.sync;

import org.neotao.ch16.my.print.sample01.lock.PrintConst;

import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class PrintLowerThread extends AbstractPrintThread {

    public PrintLowerThread(Map<String, Integer> flagMap, Lock lock, Condition condition) {
        super(flagMap, lock, condition);
    }

    @Override
    protected void printContent() {
        System.out.println((char) ('a' + count));
    }

    @Override
    public int myFlag() {
        return PrintConst.LOWER_CASE;
    }

    @Override
    protected int nextFlag() {
        return PrintConst.NUMBER;
    }
}
