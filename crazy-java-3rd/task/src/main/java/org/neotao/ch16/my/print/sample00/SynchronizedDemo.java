package org.neotao.ch16.my.print.sample00;

/**
 * 2个线程轮流打印1-52 和A-Z，输出格式为12A 34B 56C... 5152Z
 * Main方法全局变量synchronized写法
 */
public class SynchronizedDemo {
    private volatile boolean flag = true;
    private int count = 1;

    public synchronized void printNum() {
        while (!flag) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print(2 * count - 1);
        System.out.print(2 * count);
        flag = false;
        this.notify();
    }

    public synchronized void printABC() {
        while (flag) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print((char) ('A' + count - 1) + " ");
        count++;
        flag = true;
        this.notify();
    }


    public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                synchronizedDemo.printNum();
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                synchronizedDemo.printABC();
            }
        }).start();
    }
}
